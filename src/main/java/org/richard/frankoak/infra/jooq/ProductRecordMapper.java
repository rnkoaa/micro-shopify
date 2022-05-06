package org.richard.frankoak.infra.jooq;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import org.jetbrains.annotations.Nullable;
import org.jooq.RecordMapper;
import org.microshopify.jooq.tables.records.ProductRecord;
import org.richard.product.Product;

public class ProductRecordMapper implements RecordMapper<ProductRecord, Product> {

    private final ObjectMapper objectMapper;

    public ProductRecordMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public @Nullable Product map(ProductRecord record) {
        if (record == null) {
            return null;
        }

        var productBuilder = Product.builder()
            .id(record.getId())
            .title(record.getTitle())
            .link(record.getHandle())
            .type(record.getProductType())
            .price(record.getPrice())
            .available(record.getAvailable() > 0)
            .vendor(record.getVendor())
            .createdAt(Instant.parse(record.getCreatedAt()))
            .updatedAt(Instant.parse(record.getUpdatedAt()));

        return productBuilder.build();
    }
}
