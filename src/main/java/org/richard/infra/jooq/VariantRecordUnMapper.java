package org.richard.infra.jooq;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import org.jetbrains.annotations.NotNull;
import org.jooq.RecordUnmapper;
import org.jooq.exception.MappingException;
import org.microshopify.jooq.tables.records.VariantRecord;
import org.richard.product.Variant;

public class VariantRecordUnMapper extends JooqJsonHandler implements RecordUnmapper<Variant, VariantRecord> {

    public VariantRecordUnMapper(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public @NotNull VariantRecord unmap(Variant source) throws MappingException {
        VariantRecord variantRecord = new VariantRecord();
        if (source.id() > 0) {
            variantRecord.setId(source.id());
        }
        variantRecord.setTitle(source.title());
        variantRecord.setPrice(source.price());
        variantRecord.setAvailable(source.available() ? 1 : 0);
        variantRecord.setProductId(source.product().id());
        variantRecord.setHandle(source.handle());

        if (source.inventory() != null) {
            variantRecord.setInventory(parseJSON(source.inventory()));
        }

        if (source.image() != null && source.image().id() > 0) {
            variantRecord.setImageId(source.image().id());
        }

        variantRecord.setCreatedAt(
            source.createdAt() != null ? source.createdAt().toString() : Instant.now().toString());
        variantRecord.setUpdatedAt(
            source.updatedAt() != null ? source.updatedAt().toString() : Instant.now().toString());
        return variantRecord;
    }
}
