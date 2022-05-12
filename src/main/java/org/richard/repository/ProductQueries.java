package org.richard.repository;

import static org.microshopify.jooq.tables.Image.IMAGE;
import static org.microshopify.jooq.tables.Product.PRODUCT;
import static org.microshopify.jooq.tables.Variant.VARIANT;

import java.time.Instant;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.JSON;
import org.jooq.Record16;
import org.jooq.Record8;
import org.jooq.RecordMapper;
import org.jooq.SelectJoinStep;
import org.jooq.impl.DSL;
import org.richard.infra.jooq.VariantColumnRecordMapper;
import org.richard.product.Image;
import org.richard.product.ImageSize;
import org.richard.product.Variant;
import org.richard.utils.Strings;

public class ProductQueries {
    @NotNull
    public static SelectJoinStep<Record16<Integer, String, String, String, String, JSON, String, JSON, String, Integer, JSON, String, String, JSON, List<Image>, List<Variant>>> selectProductWithImageAndVariants(
        DSLContext dslContext, VariantColumnRecordMapper variantColumnRecordMapper) {
        return dslContext
            .select(
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
                        dslContext.select(IMAGE.ID,
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
                        dslContext.select(
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
            .from(PRODUCT);
    }

    @NotNull
    static RecordMapper<Record8<Integer, String, Integer, String, Integer, Integer, String, String>, Image> mapImageRecord() {
        return rec -> Image.builder().id(safeInt(rec.value1()))
            .position(safeInt(rec.value3()))
            .src(rec.value2())
            .alt(rec.value4())
            .imageSize(createImageSize(rec.value5(), rec.value6()))
            .createdAt(mapToInstant(rec.value7()))
            .updatedAt(mapToInstant(rec.value8()))
            .build();
    }


    private static ImageSize createImageSize(Integer width, Integer height) {
        return new ImageSize(safeInt(width), safeInt(height));
    }

    public static boolean mapToBoolean(Integer value) {
        return value != null && value > 0;
    }

    public static Instant mapToInstant(String value) {
        if (Strings.isNullOrEmpty(value)) {
            return Instant.now();
        }
        return Instant.parse(value);
    }

    public static int safeInt(Integer value){
        return value == null || value <= 0 ? 0 : value;
    }
}
