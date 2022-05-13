package org.richard.infra.jooq;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import org.jetbrains.annotations.Nullable;
import org.jooq.JSON;
import org.jooq.RecordMapper;
import org.microshopify.jooq.tables.records.VariantRecord;
import org.richard.utils.Strings;
import org.richard.product.Inventory;
import org.richard.product.Variant;

public class VariantRecordMapper extends JooqJsonHandler implements RecordMapper<VariantRecord, Variant> {

    public VariantRecordMapper(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public @Nullable Variant map(VariantRecord record) {
        var builder = Variant.builder()
            .id(record.getId())
            .title(record.getTitle())
            .available(record.getAvailable() > 0)
            .price(record.getPrice())
            .createdAt(Instant.parse(record.getCreatedAt()))
            .updatedAt(Instant.parse(record.getUpdatedAt()));

        JSON inventory = record.getInventory();
        if (inventory != null) {
            String inventoryData = inventory.data();
            if (!Strings.isNullOrEmpty(inventoryData)) {
                builder.inventory(deserialize(inventoryData, Inventory.class));
            }
        }

        return builder.build();
    }
}
