package org.richard.repository;

import static org.microshopify.jooq.tables.Image.IMAGE;
import static org.microshopify.jooq.tables.Product.PRODUCT;
import static org.microshopify.jooq.tables.Variant.VARIANT;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.JSON;
import org.jooq.Record16;
import org.jooq.exception.DataAccessException;
import org.microshopify.jooq.tables.records.ImageRecord;
import org.microshopify.jooq.tables.records.ProductRecord;
import org.microshopify.jooq.tables.records.VariantRecord;
import org.richard.infra.jooq.ImageRecordUnMapper;
import org.richard.infra.jooq.ProductRecordMapper;
import org.richard.infra.jooq.ProductRecordUnMapper;
import org.richard.infra.jooq.VariantColumnRecordMapper;
import org.richard.infra.jooq.VariantRecordUnMapper;
import org.richard.product.Image;
import org.richard.product.Product;
import org.richard.product.Variant;

public class ProductRepository extends JooqBaseRepository implements Repository<Product, Integer>,
    HasHandle<Product> {

    //    static String UNIQUE_CONSTRAINT_EXCEPTION = "UNIQUE constraint failed";
    static String UNIQUE_CONSTRAINT_EXCEPTION = "SQLITE_CONSTRAINT_UNIQUE";

    private final ProductRecordUnMapper productRecordUnMapper;
    private final ProductRecordMapper productRecordMapper;
    private final ImageRecordUnMapper imageRecordUnMapper;
    private final VariantRecordUnMapper variantRecordUnMapper;
    private final VariantColumnRecordMapper variantColumnRecordMapper;

    public ProductRepository(DSLContext dsl, ObjectMapper objectMapper) {
        super(dsl, objectMapper);
        this.productRecordUnMapper = new ProductRecordUnMapper(objectMapper);
        this.productRecordMapper = new ProductRecordMapper(objectMapper);
        this.imageRecordUnMapper = new ImageRecordUnMapper();
        this.variantRecordUnMapper = new VariantRecordUnMapper(objectMapper);
        this.variantColumnRecordMapper = new VariantColumnRecordMapper(objectMapper);
    }

    @Override
    public Optional<Product> findByHandle(String handle) {
        Product product = ProductQueries.selectProductWithImageAndVariants(getDsl(), variantColumnRecordMapper)
            .where(PRODUCT.HANDLE.eq(handle))
            .fetchOne(this::mapResultsToProduct);
        return Optional.ofNullable(product);
    }

    @Override
//    https://blog.jooq.org/how-to-use-jooqs-updatablerecord-for-crud-to-apply-a-delta/
    public boolean update(Product value) {
        if (value.id() <= 0) {
            return false;
        }

        return getDsl().transactionResult(trx -> {
            DSLContext dsl = trx.dsl();
            ProductRecord productRecord = productRecordUnMapper.unmap(value);

            try {
                if (value.images() != null) {
                    value.images()
                        .forEach(image -> this.saveImage(dsl, value, image));
                }

                if (value.variants() != null) {
                    value.variants()
                        .forEach(variant -> this.saveVariant(dsl, value, variant));
                }

                productRecord.setUpdatedAt(Instant.now().toString());
                return dsl.update(PRODUCT)
                    .set(productRecord)
                    .where(PRODUCT.ID.eq(value.id()))
                    .execute() > 0;
            } catch (DataAccessException ex) {
                System.out.println(ex.getMessage());
            }

            return false;
        });
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
        if (variant.id() > 0) {
            return updateVariant(dsl, product, variant);
        }
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

    private Variant updateVariant(DSLContext dsl, Product product, Variant variant) {
        // TODO - use exists
        VariantRecord variantRecord = dsl.selectFrom(VARIANT)
            .where(VARIANT.ID.eq(variant.id()))
            .fetchOne();
        if (variantRecord == null) {
            return variant;
        }

        VariantRecord vRecord = variantRecordUnMapper.unmap(variant.withProduct(product));
        vRecord.setUpdatedAt(Instant.now().toString());
        int updatedRecords = dsl.update(VARIANT)
            .set(vRecord)
            .where(VARIANT.ID.eq(variant.id()))
            .execute();
        if (updatedRecords > 0) {
            return variant;
        }
        return null;
    }

    private Image saveImage(DSLContext dsl, Product product, Image image) {
        if (image.id() > 0) {
            return updateImage(dsl, image);
        }
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

    private Image updateImage(DSLContext dsl, Image image) {
        ImageRecord fetchedImage = dsl.selectFrom(IMAGE)
            .where(IMAGE.ID.eq(image.id()))
            .fetchOne();
        if (fetchedImage == null) {
            return image;
        }

        fetchedImage.from(image);
        if (fetchedImage.update() > 0) {
            return image;
        }
        return null;
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
        return ProductQueries.selectProductWithImageAndVariants(getDsl(), variantColumnRecordMapper)
            .fetch(this::mapResultsToProduct);
    }

    @Override
    public Optional<Product> findById(Integer id) {
        Product product = ProductQueries.selectProductWithImageAndVariants(getDsl(), variantColumnRecordMapper)
            .where(PRODUCT.ID.eq(id))
            .fetchOne(this::mapResultsToProduct);
        return Optional.ofNullable(product);
    }

    private Product mapResultsToProduct(
        Record16<Integer, String, String, String, String, JSON, String, JSON, String, Integer, JSON, String, String, JSON, List<Image>, List<Variant>> f) {
        return Product.builder()
            .id(f.value1())
            .title(f.value2())
            .link(f.value3())
            .type(f.value4())
            .price(f.value5())
            .tags(deserialize(f.value6().data(), new TypeReference<>() {}))
            .vendor(f.value7())
            .coverImage(productRecordMapper.extractCoverImage(f.value8()))
            .htmlDescription(f.value9())
            .available(productRecordMapper.mapToBoolean(f.value10()))
            .swatchColor(productRecordMapper.extractSwatchColor(f.value11()))
            .createdAt(productRecordMapper.mapToInstant(f.value12()))
            .updatedAt(productRecordMapper.mapToInstant(f.value13()))
            .options(productRecordMapper.extractOptions(f.value14()))
            .images(f.value15())
            .variants(f.value16())
            .build();
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
