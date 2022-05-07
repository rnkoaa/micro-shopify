package org.richard.infra.jooq;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import org.jetbrains.annotations.NotNull;
import org.jooq.RecordUnmapper;
import org.jooq.exception.MappingException;
import org.microshopify.jooq.tables.records.CategoryRecord;
import org.richard.product.Category;

public class CategoryRecordUnMapper extends JooqJsonHandler implements RecordUnmapper<Category, CategoryRecord> {

    public CategoryRecordUnMapper(ObjectMapper objectMapper) {
        super(objectMapper);
    }

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

}
