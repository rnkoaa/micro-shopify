package org.richard.frankoak.infra.jooq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import org.jetbrains.annotations.NotNull;
import org.jooq.JSON;
import org.jooq.RecordUnmapper;
import org.jooq.exception.MappingException;
import org.microshopify.jooq.tables.records.CategoryRecord;
import org.richard.product.Category;

public class CategoryRecordUnMapper implements RecordUnmapper<Category, CategoryRecord> {

    private final ObjectMapper objectMapper;

    public CategoryRecordUnMapper(ObjectMapper objectMapper) {this.objectMapper = objectMapper;}

    @Override
    public @NotNull CategoryRecord unmap(Category source) throws MappingException {
        var record = new CategoryRecord();
        if (source.id() > 0) {
            record.setId(source.id());
        }
        record.setTitle(source.name());
        record.setHandle(source.url());
        record.setPosition(source.position());
        record.setCreatedAt(Instant.now().toString());
        record.setUpdatedAt(Instant.now().toString());

        if (source.parent() != null) {
            record.setParentId(source.parent().id());
        }
        if (source.description() != null && !source.description().isEmpty()) {
            record.setDescription(source.description());
        }
        if (source.sortOptions() != null) {
            record.setSortOptions(parseJSON(source.sortOptions()));
        }
        if (source.defaultFilterGroups() != null) {
            record.setDefaultFilterGroups(parseJSON(source.defaultFilterGroups()));
        }
        if (source.hero() != null) {
            record.setHero(parseJSON(source.hero()));
        }
        return record;
    }

    public JSON parseJSON(Object value) {
        return JSON.valueOf(safeJson(value));
    }

    String safeJson(Object value) {
        if (value == null) {
            return "";
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            return "";
        }
    }
}
