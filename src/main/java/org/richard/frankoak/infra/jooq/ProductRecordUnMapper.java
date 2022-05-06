package org.richard.frankoak.infra.jooq;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import org.jetbrains.annotations.NotNull;
import org.jooq.RecordUnmapper;
import org.jooq.exception.MappingException;
import org.microshopify.jooq.tables.records.ProductRecord;
import org.richard.product.Product;

public class ProductRecordUnMapper implements RecordUnmapper<Product, ProductRecord> {

    private final ObjectMapper objectMapper;

    public ProductRecordUnMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public @NotNull ProductRecord unmap(Product source) throws MappingException {
        var record = new ProductRecord();
        if (source.id() > 0) {
            record.setId(source.id());
        }
        record.setTitle(source.title());
        record.setHandle(source.link());
        record.setProductType(source.type());
        record.setPrice(source.price());
        record.setAvailable(source.available() ? 1 : 0);
        record.setCreatedAt(Instant.now().toString());
        record.setUpdatedAt(Instant.now().toString());

        return record;
    }
}
