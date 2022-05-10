package org.richard.infra.jooq;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.Nullable;
import org.jooq.JSON;
import org.jooq.Record17;
import org.jooq.RecordMapper;
import org.richard.product.Inventory;
import org.richard.product.Variant;
import org.richard.product.Weight;
import org.richard.utils.Strings;

public class VariantColumnRecordMapper extends JooqJsonHandler implements
    RecordMapper<Record17<Integer, String, String, String, Integer, String, String, JSON, Integer, String, Integer, String, JSON, String, Integer, String, String>, Variant> {

    public VariantColumnRecordMapper(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public @Nullable Variant map(
        Record17<Integer, String, String, String, Integer, String, String, JSON, Integer, String, Integer, String, JSON, String, Integer, String, String>
            record
    ) {
        var builder = Variant
            .builder()
            .id(safeInt(record.value1()))
            .title(record.value2())
            .price(record.value3())
            .compareAtPrice(record.value4())
            .position(safeInt(record.value5()))
            .sku(record.value6())
            .handle(record.value7())
            .available(mapToBoolean(record.value9()))
            .taxCode(record.value10())
            .taxable(mapToBoolean(record.value11()))
            .barcode(record.value12())
            .fulfillmentService(record.value14())
            .requiresShipping(mapToBoolean(record.value15()))
            .createdAt(mapToInstant(record.value16()))
            .updatedAt(mapToInstant(record.value17()));
        if (record.value13() != null) {
            String data = record.value13().data();
            if (Strings.isNotNullOrEmpty(data)) {
                builder.weight(deserialize(data, Weight.class));
            }
        }
        if (record.value8() != null) {
            String data = record.value8().data();
            if (Strings.isNotNullOrEmpty(data)) {
                builder.inventory(deserialize(data, Inventory.class));
            }
        }

        return builder.build();
    }
}
