package org.richard.frankoak.infra.jooq;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.RecordMapperProvider;
import org.jooq.RecordType;
import org.jooq.impl.DefaultRecordMapper;
import org.microshopify.jooq.tables.records.CategoryRecord;
import org.richard.Category;

@SuppressWarnings("unchecked")
public class RecordMapperProviderImpl implements RecordMapperProvider {

    private final CategoryRecordMapper categoryRecordMapper;

    public RecordMapperProviderImpl(ObjectMapper objectMapper) {
        this.categoryRecordMapper = new CategoryRecordMapper(objectMapper);
    }

    @Override
    public @NotNull <R extends Record, E> RecordMapper<R, E> provide(RecordType<R> recordType,
        Class<? extends E> type) {
        if (type == Category.class) {
            return record -> (E) categoryRecordMapper.map((CategoryRecord) record);
        }

//        if (type == EventMessage.class) {
//            return record -> (E) new EventMessageRecordMapper(new EventJsonConverter(objectMapper))
//                .map((EventRecord) record);
//        }
        return new DefaultRecordMapper<>(recordType, type);
    }
}