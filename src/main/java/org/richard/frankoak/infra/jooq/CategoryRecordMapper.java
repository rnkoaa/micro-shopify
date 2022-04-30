package org.richard.frankoak.infra.jooq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.Nullable;
import org.jooq.JSON;
import org.jooq.RecordMapper;
import org.microshopify.jooq.tables.records.CategoryRecord;
import org.richard.Category;
import org.richard.Category.Builder;
import org.richard.frankoak.SortOption;

public class CategoryRecordMapper implements RecordMapper<CategoryRecord, Category> {

    private final ObjectMapper objectMapper;

    static TypeReference<List<String>> typeReference = new TypeReference<>() {};
    static TypeReference<List<SortOption>> sortOptionsTypeReference = new TypeReference<>() {};
    static TypeReference<Map<String, String>> heroTypeReference = new TypeReference<>() {};

    public CategoryRecordMapper(ObjectMapper objectMapper) {this.objectMapper = objectMapper;}

    @Override
    public @Nullable Category map(CategoryRecord record) {
        Builder categoryBuilder = Category.builder();

        categoryBuilder.id(record.getId())
            .name(record.getTitle())
            .url(record.getHandle())
            .position(record.getPosition())
            .description(record.getDescription() != null ? record.getDescription() : "");

        String updatedAt = record.getUpdatedAt();
        String createdAt = record.getCreatedAt();
        JSON defaultFilterGroups = record.getDefaultFilterGroups();
        JSON hero = record.getHero();

        if (record.getSortOptions() != null) {
            String data = record.getSortOptions().data();
            if (!data.isEmpty()) {
                List<SortOption> sortOptions = deserialize(objectMapper, data, sortOptionsTypeReference);
                categoryBuilder.sortOptions(sortOptions);
            }
        }

        if (defaultFilterGroups != null) {
            String defaultFilterGroupsData = defaultFilterGroups.data();
            if (!defaultFilterGroupsData.isEmpty()) {
                List<String> defaultFilterGroupsRes = deserialize(objectMapper, defaultFilterGroupsData, typeReference);
                categoryBuilder.defaultFilterGroups(defaultFilterGroupsRes);
            }
        }

        if (hero != null) {
            String heroData = hero.data();
            if (!heroData.isEmpty()) {
                Map<String, String> res = deserializeMap(objectMapper, heroData, heroTypeReference);
                categoryBuilder.hero(res);
            }
        }

        return categoryBuilder.build();
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

    static <T, U> Map<T, U> deserializeMap(ObjectMapper objectMapper, String value,
        TypeReference<Map<T, U>> typeReference) {
        try {
            return objectMapper.readValue(value, typeReference);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
