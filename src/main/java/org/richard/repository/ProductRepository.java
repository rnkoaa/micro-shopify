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
import org.jooq.Record17;
import org.jooq.Record8;
import org.jooq.RecordMapper;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.microshopify.jooq.tables.records.ProductRecord;
import org.richard.infra.jooq.ImageRecordUnMapper;
import org.richard.infra.jooq.ProductRecordMapper;
import org.richard.infra.jooq.ProductRecordUnMapper;
import org.richard.infra.jooq.VariantColumnRecordMapper;
import org.richard.infra.jooq.VariantRecordUnMapper;
import org.richard.product.Image;
import org.richard.product.ImageSize;
import org.richard.product.Product;
import org.richard.product.Variant;
import org.richard.utils.Strings;

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
        Product product = getDsl().selectFrom(PRODUCT)
            .where(PRODUCT.HANDLE.eq(handle))
            .fetchOneInto(Product.class);
        return Optional.ofNullable(product);
    }

    @Override
    // https://stackoverflow.com/questions/66742834/update-a-column-when-value-is-not-null-jooq
//    https://blog.jooq.org/how-to-use-jooqs-updatablerecord-for-crud-to-apply-a-delta/
    public boolean update(Product value) {
        getDsl().transactionResult(trx -> {
            DSLContext dsl = trx.dsl();
            ProductRecord productRecord = dsl.selectFrom(PRODUCT)
                .where(PRODUCT.HANDLE.eq(value.link()))
                .fetchOne();
            if (productRecord == null) {
                return false;
            }
            productRecord.setUpdatedAt(Instant.now().toString());
            productRecord.setTags(parseJSON(value.tags()));
            productRecord.setTitle(value.title());
            productRecord.setAvailable((value.available()) ? 1 : 0);
            if (Strings.isNotNullOrEmpty(value.price())) {
                productRecord.setPrice(value.price());
            }
            if (Strings.isNotNullOrEmpty(value.htmlDescription())) {
                productRecord.setHtmlDescription(value.htmlDescription());
            }
            if (value.swatchColor() != null) {
                productRecord.setSwatchColor(parseJSON(value.swatchColor()));
            }
            if (Strings.isNotNullOrEmpty(value.vendor())) {
                productRecord.setVendor(value.vendor());
            }

            return true;
        });
        return false;
    }
    /*
      return dsl.select(
                PRODUCT.ID,
                PRODUCT.NAME,
                PRODUCT.DESCRIPTION,
                PRODUCT.PRICE,
                DSL.multiset(
                        dsl.select(CATEGORY.ID, CATEGORY.NAME)
                            .from(CATEGORY)
                            .join(PRODUCT_CATEGORY)
                            .on(PRODUCT_CATEGORY.CATEGORY_ID.eq(CATEGORY.ID))
                            .where(PRODUCT_CATEGORY.PRODUCT_ID.eq(PRODUCT.ID))
                    ).as("categories")
                    .convertFrom(r -> r.map(rec -> new Category(rec.value1(), rec.value2())))
            )
            .from(PRODUCT);
     */

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
        Product product = getDsl().select(
                PRODUCT.ID,
                PRODUCT.TITLE,
                PRODUCT.HANDLE,
                PRODUCT.PRODUCT_TYPE,
                PRODUCT.PRICE,
                PRODUCT.TAGS,
                PRODUCT.VENDOR,
                PRODUCT.FEATURED_IMAGE,
                PRODUCT.HTML_DESCRIPTION,
                PRODUCT.AVAILABLE,
                PRODUCT.SWATCH_COLOR,
                PRODUCT.CREATED_AT,
                PRODUCT.UPDATED_AT,
                PRODUCT.OPTIONS,
                DSL.multiset(
                        getDsl().select(IMAGE.ID,
                                IMAGE.SRC,
                                IMAGE.POSITION,
                                IMAGE.ALT,
                                IMAGE.WIDTH, IMAGE.HEIGHT,
                                IMAGE.CREATED_AT,
                                IMAGE.UPDATED_AT
                            )
                            .from(IMAGE)
                            .join(PRODUCT)
                            .on(IMAGE.PRODUCT_ID.eq(PRODUCT.ID))
                            .where(IMAGE.PRODUCT_ID.eq(PRODUCT.ID))
                    ).as("images")
                    .convertFrom(r -> r.map(mapImageRecord())),
                DSL.multiset(
                        getDsl().select(
                                VARIANT.ID,
                                VARIANT.TITLE,
                                VARIANT.PRICE,
                                VARIANT.COMPARE_AT_PRICE,
                                VARIANT.POSITION,
                                VARIANT.SKU,
                                VARIANT.HANDLE,
                                VARIANT.INVENTORY,
                                VARIANT.AVAILABLE,
                                VARIANT.TAX_CODE,
                                VARIANT.TAXABLE,
                                VARIANT.BARCODE,
                                VARIANT.WEIGHT,
                                VARIANT.FULFILLMENT_SERVICE,
                                VARIANT.REQUIRES_SHIPPING,
                                VARIANT.CREATED_AT,
                                VARIANT.UPDATED_AT
                            )
                            .from(VARIANT)
                            .join(PRODUCT)
                            .on(VARIANT.PRODUCT_ID.eq(PRODUCT.ID))
                            .where(VARIANT.PRODUCT_ID.eq(PRODUCT.ID))
                    ).as("variants")
                    .convertFrom(r -> r.map(variantColumnRecordMapper))
            )
            .from(PRODUCT)
            .where(PRODUCT.ID.eq(id))
            .fetchOne(f -> Product.builder()
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
                .build());
        return Optional.ofNullable(product);
    }

    @NotNull
    private RecordMapper<Record8<Integer, String, Integer, String, Integer, Integer, String, String>, Image> mapImageRecord() {
        return rec -> Image.builder().id(safeInt(rec.value1()))
            .position(safeInt(rec.value3()))
            .src(rec.value2())
            .alt(rec.value4())
            .imageSize(createImageSize(rec.value5(), rec.value6()))
            .createdAt(mapToInstant(rec.value7()))
            .updatedAt(mapToInstant(rec.value8()))
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

    private ImageSize createImageSize(Integer width, Integer height) {
        return new ImageSize(safeInt(width), safeInt(height));
    }
}
