package org.richard.infra.jooq;

import java.time.Instant;
import org.jetbrains.annotations.NotNull;
import org.jooq.RecordUnmapper;
import org.jooq.exception.MappingException;
import org.microshopify.jooq.tables.records.ImageRecord;
import org.richard.product.Image;

public class ImageRecordUnMapper implements RecordUnmapper<Image, ImageRecord> {

    @Override
    public @NotNull ImageRecord unmap(Image source) throws MappingException {
        var imageRecord = new ImageRecord();

        imageRecord.setSrc(source.src());
        imageRecord.setAlt((source.alt() != null) ? source.alt() : "");

        Instant createdAt = source.createdAt() != null ? source.createdAt() : Instant.now();
        Instant updatedAt = source.updatedAt() != null ? source.updatedAt() : Instant.now();
        imageRecord.setCreatedAt(createdAt.toString());
        imageRecord.setUpdatedAt(updatedAt.toString());

        if (source.product() != null) {
            imageRecord.setProductId(source.product().id());
        }
        if (source.position() > 0) {
            imageRecord.setPosition(source.position());
        }
        if (source.size() != null) {
            imageRecord.setWidth(source.size().width());
            imageRecord.setHeight(source.size().height());
        }
        return imageRecord;
    }
}
