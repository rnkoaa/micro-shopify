package org.richard.frankoak.category;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ProductVariant(
    boolean available,
    String title,
    String id,
    String price,
    @JsonProperty("compare_at_price")
    String compareAtPrice,
    VariantImage image,
    String option1,
    String option2,
    String option3,
    @JsonProperty("inventory_quantity")
    int inventoryQuantity
) {
}
