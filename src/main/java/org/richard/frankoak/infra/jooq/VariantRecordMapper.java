package org.richard.frankoak.infra.jooq;

import java.time.Instant;
import org.jetbrains.annotations.Nullable;
import org.jooq.RecordMapper;
import org.microshopify.jooq.tables.records.VariantRecord;
import org.richard.product.Variant;

public class VariantRecordMapper implements RecordMapper<VariantRecord, Variant> {

    @Override
    public @Nullable Variant map(VariantRecord record) {
        return Variant.builder()
            .id(record.getId())
            .title(record.getTitle())
            .available(record.getAvailable() > 0)
            .price(record.getPrice())
            .createdAt(Instant.parse(record.getCreatedAt()))
            .updatedAt(Instant.parse(record.getUpdatedAt()))
            .build();
    }
}
