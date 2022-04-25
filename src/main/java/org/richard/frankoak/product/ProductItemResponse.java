package org.richard.frankoak.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.List;

public record ProductItemResponse(
    Long id,
    String title,
    @JsonProperty("body_html")
    String bodyHtml,
    String vendor,
    @JsonProperty("product_type")
    String productType,
    @JsonProperty("created_at")
    Instant createdAt,

    @JsonProperty("updated_at")
    Instant updatedAt,

    @JsonProperty("template_suffix")
    String templateSuffix,

    @JsonProperty("published_scope")
    String publishedScope,
    String tags,
    List<VariantItem> variants,
    List<OptionItem> options,
    List<ImageItem> images,
    ImageItem image,
    String handle
) {

    public ProductItemResponse withHandle(String handle) {
        return new ProductItemResponse(id, title, bodyHtml, vendor, productType, createdAt, updatedAt, templateSuffix,
            publishedScope, tags, variants,
            options, images, image, handle);
    }
}
