package org.richard.repository;

import static org.microshopify.jooq.tables.Image.IMAGE;
import static org.microshopify.jooq.tables.Product.PRODUCT;
import static org.microshopify.jooq.tables.Variant.VARIANT;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.richard.frankoak.infra.jooq.ImageRecordUnMapper;
import org.richard.frankoak.infra.jooq.ProductRecordUnMapper;
import org.richard.frankoak.infra.jooq.VariantRecordUnMapper;
import org.richard.product.Image;
import org.richard.product.Product;
import org.richard.product.Variant;

public class ProductRepository extends JooqBaseRepository implements Repository<Product, Integer>,
    HasHandle<Product> {

    private final ProductRecordUnMapper productRecordUnMapper;
    private final ImageRecordUnMapper imageRecordUnMapper;
    private final VariantRecordUnMapper variantRecordUnMapper;

    public ProductRepository(DSLContext dsl, ObjectMapper objectMapper) {
        super(dsl, objectMapper);
        this.productRecordUnMapper = new ProductRecordUnMapper(objectMapper);
        this.imageRecordUnMapper = new ImageRecordUnMapper();
        this.variantRecordUnMapper = new VariantRecordUnMapper();
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
        return getDsl().transactionResult(configuration -> {
            try {
                Integer result = getDsl().insertInto(PRODUCT)
                    .set(productRecordUnMapper.unmap(product))
                    .returningResult(PRODUCT.ID)
                    .fetchOneInto(Integer.class);

                final var savedProduct = product.clone()
                    .withId(result);
                // insert all its children with category as
                if (product.images() != null) {
                    List<Image> images = product.images()
                        .stream().map(image -> saveImage(savedProduct, image))
                        .toList();
//                    savedProduct = savedProduct.withImages(images);
                }

                if (product.variants() != null) {
                    List<Variant> savedVariants = product.variants()
                        .stream()
                        .map(variant -> saveVariant(savedProduct, variant))
                        .toList();
                }

                if (result != null) {
                    return savedProduct;
                }
                return product;
            } catch (DataAccessException ex) {
                if (ex.getMessage().contains("SQLITE_CONSTRAINT_UNIQUE")) {
                    System.out.println("Unique Constraint: " + product);
                }else {
                    System.out.println(ex.getMessage());
                }
            }
            return product;
        });
    }

    private Variant saveVariant(Product product, Variant variant) {
        var toSave = variant.withProduct(product);
        Integer result = getDsl()
            .insertInto(VARIANT)
            .set(variantRecordUnMapper.unmap(toSave))
            .returningResult(VARIANT.ID)
            .fetchOneInto(Integer.class);
        return variant.withId(result);
    }

    private Image saveImage(Product product, Image image) {
        var toSave = image.withProduct(product);
        Integer result = getDsl()
            .insertInto(IMAGE)
            .set(imageRecordUnMapper.unmap(toSave))
            .returningResult(IMAGE.ID)
            .fetchOneInto(Integer.class);
        return image.withId(result);
    }

    @Override
    public List<Product> save(List<Product> values) {
        return null;
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
