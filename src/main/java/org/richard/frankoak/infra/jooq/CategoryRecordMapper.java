package org.richard.frankoak.infra.jooq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.jetbrains.annotations.Nullable;
import org.jooq.JSON;
import org.jooq.RecordMapper;
import org.microshopify.jooq.tables.records.CategoryRecord;
import org.richard.Category;
import org.richard.frankoak.SortOption;

public class CategoryRecordMapper implements RecordMapper<CategoryRecord, Category> {

    private final ObjectMapper objectMapper;

    public CategoryRecordMapper(ObjectMapper objectMapper) {this.objectMapper = objectMapper;}

    @Override
    public @Nullable Category map(CategoryRecord record) {
        var id = record.getId();
        String title = record.getTitle();
        String handle = record.getHandle();
        String description = record.getDescription();
        String updatedAt = record.getUpdatedAt();
        String createdAt = record.getCreatedAt();
        JSON defaultFilterGroups = record.getDefaultFilterGroups();
        JSON hero = record.getHero();

        if (record.getSortOptions() != null) {
            String data = record.getSortOptions().data();
            if (!data.isEmpty()) {
                TypeReference<List<SortOption>> typeReference = new TypeReference<>() {};
                List<SortOption> sortOptions = deserialize(objectMapper, data, typeReference);
            }
        }
        return null;
    }

    static <T> T deserialize(ObjectMapper objectMapper, String value, Class<T> clzz) {
        try {
            return objectMapper.readValue(value, clzz);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    static <T> List<T> deserialize(ObjectMapper objectMapper, String value, TypeReference<List<T>> typeReference) {
        try {
            return objectMapper.readValue(value, typeReference);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
