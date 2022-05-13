package org.richard.infra.jooq;

import java.time.Instant;
import org.jetbrains.annotations.Nullable;
import org.jooq.RecordMapper;
import org.microshopify.jooq.tables.records.ImageRecord;
import org.richard.product.Image;

public class ImageRecordMapper implements RecordMapper<ImageRecord, Image> {

    @Override
    public @Nullable Image map(ImageRecord record) {
        return Image.builder()
            .id(record.getId())
            .imageSize(record.getWidth(), record.getHeight())
            .position(record.getPosition())
            .src(record.getSrc())
            .alt(record.getAlt())
            .createdAt(Instant.parse(record.getCreatedAt()))
            .updatedAt(Instant.parse(record.getUpdatedAt()))
            .build();
    }
}
