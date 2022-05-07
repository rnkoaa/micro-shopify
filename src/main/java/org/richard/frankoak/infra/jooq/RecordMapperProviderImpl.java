package org.richard.frankoak.infra.jooq;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.RecordMapperProvider;
import org.jooq.RecordType;
import org.jooq.impl.DefaultRecordMapper;
import org.microshopify.jooq.tables.records.CategoryRecord;
import org.microshopify.jooq.tables.records.ImageRecord;
import org.microshopify.jooq.tables.records.ProductRecord;
import org.microshopify.jooq.tables.records.VariantRecord;
import org.richard.product.Category;
import org.richard.product.Image;
import org.richard.product.Product;
import org.richard.product.Variant;

@SuppressWarnings("unchecked")
public class RecordMapperProviderImpl implements RecordMapperProvider {

    private final CategoryRecordMapper categoryRecordMapper;
    private final ProductRecordMapper productRecordMapper;
    private final ImageRecordMapper imageRecordMapper;
    private final VariantRecordMapper variantRecordMapper;

    public RecordMapperProviderImpl(ObjectMapper objectMapper) {
        this.categoryRecordMapper = new CategoryRecordMapper(objectMapper);
        this.productRecordMapper = new ProductRecordMapper(objectMapper);
        this.imageRecordMapper = new ImageRecordMapper();
        this.variantRecordMapper = new VariantRecordMapper();
    }

    @Override
    public @NotNull <R extends Record, E> RecordMapper<R, E> provide(RecordType<R> recordType,
        Class<? extends E> type) {
        if (type == Category.class) {
            return record -> (E) categoryRecordMapper.map((CategoryRecord) record);
        }
        if (type == Product.class) {
            return record -> (E) productRecordMapper.map((ProductRecord) record);
        }
        if (type == Image.class) {
            return record -> (E) imageRecordMapper.map((ImageRecord) record);
        }
        if (type == Variant.class) {
            return record -> (E) variantRecordMapper.map((VariantRecord) record);
        }

        return new DefaultRecordMapper<>(recordType, type);
    }
}
