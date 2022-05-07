package org.richard.infra.jooq;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import org.jetbrains.annotations.NotNull;
import org.jooq.RecordUnmapper;
import org.jooq.exception.MappingException;
import org.microshopify.jooq.tables.records.ProductRecord;
import org.richard.product.Image;
import org.richard.product.Product;

public class ProductRecordUnMapper extends JooqJsonHandler implements RecordUnmapper<Product, ProductRecord> {

    public ProductRecordUnMapper(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public @NotNull ProductRecord unmap(Product source) throws MappingException {
        var record = new ProductRecord();
        if (source.id() > 0) {
            record.setId(source.id());
        }

        Instant createdAt = source.createdAt() != null ? source.createdAt() : Instant.now();
        Instant updatedAt = source.updatedAt() != null ? source.updatedAt() : Instant.now();

        record.setCreatedAt(createdAt.toString());
        record.setUpdatedAt(updatedAt.toString());

        record.setTitle(source.title());
        record.setHandle(source.link());
        record.setProductType(source.type());
        record.setPrice(source.price());
        record.setOptions(parseJSON(source.options()));
        record.setSwatchColor(parseJSON(source.swatchColor()));
        record.setVendor(source.vendor());
        record.setAvailable(source.available() ? 1 : 0);
        record.setHtmlDescription(source.htmlDescription());
        record.setTags(parseJSON(source.tags()));

        record.setCreatedAt(Instant.now().toString());
        record.setUpdatedAt(Instant.now().toString());

        var featuredImage = new Image(0, source.coverImage(), "", 0, source, null, null, null);
        record.setFeaturedImage(parseJSON(featuredImage));

        return record;
    }
}
