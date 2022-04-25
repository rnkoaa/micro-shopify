package org.richard.frankoak.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.List;

public record ImageItem(
    long id,
    @JsonProperty("product_id")
    long productId,
    int position,
    @JsonProperty("created_at")
    Instant createdAt,

    @JsonProperty("updated_at")
    Instant updatedAt,
    String alt,
    int width,
    int height,
    String src,
    @JsonProperty("variant_ids")
    List<Long> variantIds
) {
}
