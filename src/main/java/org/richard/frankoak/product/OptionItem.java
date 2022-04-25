package org.richard.frankoak.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record OptionItem(
    long id,
    @JsonProperty("product_id")
    long productId,
    String name,
    int position,
    List<String> values
) {
}
