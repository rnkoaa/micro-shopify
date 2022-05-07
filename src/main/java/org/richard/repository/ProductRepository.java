package org.richard.repository;

import static org.microshopify.jooq.tables.Image.IMAGE;
import static org.microshopify.jooq.tables.Product.PRODUCT;
import static org.microshopify.jooq.tables.Variant.VARIANT;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.richard.infra.jooq.ImageRecordUnMapper;
import org.richard.infra.jooq.ProductRecordUnMapper;
import org.richard.infra.jooq.VariantRecordUnMapper;
import org.richard.product.Image;
import org.richard.product.Product;
import org.richard.product.Variant;

public class ProductRepository extends JooqBaseRepository implements Repository<Product, Integer>,
    HasHandle<Product> {

    //    static String UNIQUE_CONSTRAINT_EXCEPTION = "UNIQUE constraint failed";
    static String UNIQUE_CONSTRAINT_EXCEPTION = "SQLITE_CONSTRAINT_UNIQUE";

    private final ProductRecordUnMapper productRecordUnMapper;
    private final ImageRecordUnMapper imageRecordUnMapper;
    private final VariantRecordUnMapper variantRecordUnMapper;

    public ProductRepository(DSLContext dsl, ObjectMapper objectMapper) {
        super(dsl, objectMapper);
        this.productRecordUnMapper = new ProductRecordUnMapper(objectMapper);
        this.imageRecordUnMapper = new ImageRecordUnMapper();
        this.variantRecordUnMapper = new VariantRecordUnMapper(objectMapper);
    }

    @Override
    public Optional<Product> findByHandle(String handle) {
        Product product = getDsl().selectFrom(PRODUCT)
            .where(PRODUCT.HANDLE.eq(handle))
            .fetchOneInto(Product.class);
        return Optional.ofNullable(product);
    }

    @Override
    public boolean update(Product value) {
        return false;
    }

    @Override
    public Product save(Product product) {
        return getDsl().transactionResult(trx -> {
            Product savedProduct = saveOrFind(trx.dsl(), product);

//                // insert all its children with category as
            if (product.images() != null) {
                final Product productCopy = savedProduct;
                List<Image> images = product.images()
                    .stream()
                    .map(image -> saveImage(trx.dsl(), productCopy, image))
                    .toList();
                savedProduct = savedProduct.withImages(images);
            }

            if (product.variants() != null) {
                final Product productCopy = savedProduct;
                List<Variant> savedVariants = product.variants()
                    .stream()
                    .map(variant -> saveVariant(trx.dsl(), productCopy, variant))
                    .toList();
                savedProduct = savedProduct.withVariants(savedVariants);
            }

            return savedProduct;
        });
    }

    private Variant saveVariant(DSLContext dsl, Product product, Variant variant) {
        var toSave = variant.withProduct(product);
        try {
            if (variant.image() != null) {
                toSave = toSave.withImage(saveImage(dsl, product, variant.image()));
            }

            Integer result = dsl
                .insertInto(VARIANT)
                .set(variantRecordUnMapper.unmap(toSave))
                .returningResult(VARIANT.ID)
                .fetchOneInto(Integer.class);

            return variant.withId(result);
        } catch (DataAccessException ex) {
            if (ex.getMessage().contains(UNIQUE_CONSTRAINT_EXCEPTION)) {
                var result = getDsl().selectFrom(VARIANT)
                    .where(VARIANT.HANDLE.eq(toSave.handle()))
                    .fetchOne(VARIANT.ID);
                return toSave.withId(result);
            }
        }
        return toSave;
    }

    private Image saveImage(DSLContext dsl, Product product, Image image) {
        var toSave = image.withProduct(product);
        try {
            var result = dsl
                .insertInto(IMAGE)
                .set(imageRecordUnMapper.unmap(toSave))
                .returningResult(IMAGE.ID)
                .fetchOneInto(Integer.class);
            return toSave.withId(result);
        } catch (DataAccessException ex) {
            if (ex.getMessage().contains(UNIQUE_CONSTRAINT_EXCEPTION)) {
                var result = dsl.selectFrom(IMAGE)
                    .where(IMAGE.SRC.eq(toSave.src()))
                    .fetchOne(IMAGE.ID);
                if (result == null) {
                    return toSave;
                }
                return toSave.withId(result);
            }
        }
        return toSave;
    }

    @Override
    public List<Product> save(List<Product> values) {
        return values.stream()
            .map(this::save)
            .toList();
    }

    private Product saveOrFind(@NotNull DSLContext dsl, Product product) {
        try {
            Integer result = getDsl().insertInto(PRODUCT)
                .set(productRecordUnMapper.unmap(product))
                .returningResult(PRODUCT.ID)
                .fetchOne(PRODUCT.ID);
            return product.withId(result);
        } catch (DataAccessException ex) {
            if (ex.getMessage().contains("SQLITE_CONSTRAINT_UNIQUE")) {
                Integer maybeId = dsl
                    .select(PRODUCT.ID)
                    .from(PRODUCT)
                    .where(PRODUCT.HANDLE.eq(product.link()))
                    .fetchOneInto(Integer.class);
                if (maybeId == null) {
                    throw new RuntimeException("unable to find a product that exists" + product.link());
                }
                return product.withId(maybeId);
            }
        }
        return product;
    }

    @Override
    public int count() {
        return 0;
    }

    @Override
    public List<Product> findAll() {
        return getDsl()
            .selectFrom(PRODUCT)
            .fetchInto(Product.class);
    }

    @Override
    public Optional<Product> findById(Integer id) {
        Product product = getDsl().selectFrom(PRODUCT)
            .where(PRODUCT.ID.eq(id))
            .fetchOneInto(Product.class);
        return Optional.ofNullable(product);
    }

    @Override
    public boolean delete(Integer id) {
        return getDsl().transactionResult(configuration -> {
            if (!deleteImages(id)) {
                return false;
            }
            if (!deleteVariants(id)) {
                return false;
            }
            int execute = getDsl()
                .delete(PRODUCT)
                .where(PRODUCT.ID.eq(id))
                .execute();
            return execute > 0;
        });
    }

    private boolean deleteVariants(Integer id) {
        return getDsl()
            .delete(VARIANT)
            .where(VARIANT.PRODUCT_ID.eq(id))
            .execute() > 0;
    }

    @Override
    public boolean deleteAll(boolean testing) {
        if (testing) {
            return getDsl().transactionResult(configuration -> {
                deleteAllImages(testing);
                deleteAllVariants(testing);

                return getDsl()
                    .delete(PRODUCT)
                    .execute() > 0;
            });
        }
        return false;
    }

    private boolean deleteAllVariants(boolean test) {
        if (test) {
            return getDsl()
                .delete(VARIANT)
                .execute() > 0;
        }
        return false;
    }

    private boolean deleteImages(Integer id) {
        return getDsl()
            .delete(IMAGE)
            .where(IMAGE.PRODUCT_ID.eq(id))
            .execute() > 0;
    }

    private boolean deleteAllImages(boolean test) {
        if (test) {
            return getDsl()
                .delete(IMAGE)
                .execute() > 0;
        }
        return false;
    }
}
