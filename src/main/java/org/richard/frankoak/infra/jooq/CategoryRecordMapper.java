package org.richard.frankoak.infra.jooq;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.Nullable;
import org.jooq.JSON;
import org.jooq.RecordMapper;
import org.microshopify.jooq.tables.records.CategoryRecord;
import org.richard.frankoak.SortOption;
import org.richard.product.Category;
import org.richard.product.Category.Builder;

public class CategoryRecordMapper extends JooqJsonHandler implements RecordMapper<CategoryRecord, Category> {

    static TypeReference<List<String>> typeReference = new TypeReference<>() {};
    static TypeReference<List<SortOption>> sortOptionsTypeReference = new TypeReference<>() {};
    static TypeReference<Map<String, String>> heroTypeReference = new TypeReference<>() {};

    public CategoryRecordMapper(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public @Nullable Category map(CategoryRecord record) {
        Builder categoryBuilder = Category.builder();

        categoryBuilder.id(record.getId())
            .name(record.getTitle())
            .url(record.getHandle())
            .position(record.getPosition())
            .description(record.getDescription() != null ? record.getDescription() : "");

        String updatedAt = record.getUpdatedAt();
        if (updatedAt != null && !updatedAt.isEmpty()) {
            categoryBuilder.updatedAt(Instant.parse(updatedAt));
        }
        String createdAt = record.getCreatedAt();
        if (createdAt != null && !createdAt.isEmpty()) {
            categoryBuilder.createdAt(Instant.parse(createdAt));
        }
        JSON defaultFilterGroups = record.getDefaultFilterGroups();
        JSON hero = record.getHero();

        if (record.getSortOptions() != null) {
            String data = record.getSortOptions().data();
            if (!data.isEmpty()) {
                List<SortOption> sortOptions = deserialize(data, sortOptionsTypeReference);
                categoryBuilder.sortOptions(sortOptions);
            }
        }

        if (defaultFilterGroups != null) {
            String defaultFilterGroupsData = defaultFilterGroups.data();
            if (!defaultFilterGroupsData.isEmpty()) {
                List<String> defaultFilterGroupsRes = deserialize(defaultFilterGroupsData, typeReference);
                categoryBuilder.defaultFilterGroups(defaultFilterGroupsRes);
            }
        }

        if (hero != null) {
            String heroData = hero.data();
            if (!heroData.isEmpty()) {
                Map<String, String> res = deserializeMap(heroData, heroTypeReference);
                categoryBuilder.hero(res);
            }
        }

        return categoryBuilder.build();
    }

}
