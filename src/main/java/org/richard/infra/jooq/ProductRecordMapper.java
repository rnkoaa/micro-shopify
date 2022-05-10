package org.richard.infra.jooq;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jetbrains.annotations.Nullable;
import org.jooq.JSON;
import org.jooq.RecordMapper;
import org.microshopify.jooq.tables.records.ProductRecord;
import org.richard.utils.Strings;
import org.richard.product.Image;
import org.richard.product.Product;
import org.richard.product.ProductOption;
import org.richard.product.SwatchColor;

public class ProductRecordMapper extends JooqJsonHandler implements RecordMapper<ProductRecord, Product> {

    TypeReference<Set<String>> tagsTypeReference = new TypeReference<>() {};
    private final TypeReference<List<ProductOption>> productOptionsTypeReference = new TypeReference<>() {};

    public ProductRecordMapper(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public @Nullable Product map(ProductRecord record) {
        if (record == null) {
            return null;
        }

//        Object  = record.get("images");

        var productBuilder = Product.builder()
            .id(record.getId())
            .title(record.getTitle())
            .link(record.getHandle())
            .type(record.getProductType())
            .price(record.getPrice())
            .htmlDescription(record.getHtmlDescription())
            .available(record.getAvailable() > 0)
            .vendor(record.getVendor())
            .createdAt(Instant.parse(record.getCreatedAt()))
            .updatedAt(Instant.parse(record.getUpdatedAt()))
            .tags(extractTags(record))
            .options(extractOptions(record.getOptions()))
            .coverImage(extractCoverImage(record.getFeaturedImage()))
            .swatchColor(extractSwatchColor(record.getSwatchColor()));

        return productBuilder.build();
    }

    public String extractCoverImage(JSON featuredImage) {
        if (featuredImage == null) {
            return "";
        }
        String featuredImageData = featuredImage.data();
        if (Strings.isNullOrEmpty(featuredImageData)) {
            return "";
        }

        Image image = deserialize(featuredImageData, Image.class);
        if (image == null) {
            return "";
        }
        return image.src();
    }

    public SwatchColor extractSwatchColor(JSON swatchColor) {
        if (swatchColor == null) {
            return null;
        }
        String swatchColorData = swatchColor.data();
        if (Strings.isNullOrEmpty(swatchColorData)) {
            return null;
        }

        return deserialize(swatchColorData, SwatchColor.class);
    }

    public List<String> extractTags(ProductRecord record) {
        JSON tags = record.getTags();
        if (tags != null) {
            String data = tags.data();
            if (Strings.isNotNullOrEmpty(data)) {
                return new ArrayList<>(deserializeStringSet(data, tagsTypeReference));
            }
        }
        return List.of();
    }

    public Set<ProductOption> extractOptions(JSON options) {
        if (options != null) {
            String data = options.data();
            if (Strings.isNotNullOrEmpty(data)) {
                List<ProductOption> optionItems = deserialize(data, productOptionsTypeReference);
                return new HashSet<>(optionItems);
            }
        }
        return Set.of();
    }

    public boolean mapToBoolean(Integer value) {
        return value != null && value > 0;
    }

    public Instant mapToInstant(String value) {
        if (Strings.isNullOrEmpty(value)) {
            return Instant.now();
        }
        return Instant.parse(value);
    }
}
