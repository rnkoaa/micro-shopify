package org.richard.frankoak.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

public record VariantItem(
    long id,
    @JsonProperty("product_id")
    long productId,
    String title,
    String price,
    String sku,
    int position,

    @JsonProperty("compare_at_price")
    String compareAtPrice,

    @JsonProperty("fulfillment_service")
    String fulfillmentService,

    @JsonProperty("inventory_management")
    String inventoryManagement,
    String option1,
    String option2,

    String option3,

    @JsonProperty("created_at")
    Instant createdAt,

    @JsonProperty("updated_at")
    Instant updatedAt,
    boolean taxable,
    String barcode,
    int grams,
    @JsonProperty("image_id")
    String imageId,
    double weight,

    @JsonProperty("weight_unit")
    String weightUnit,

    @JsonProperty("tax_code")
    String taxCode,

    @JsonProperty("requires_shipping")
    boolean requiresShipping
) {
}
