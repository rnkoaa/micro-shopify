package org.richard.frankoak.category;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ProductImage(
    String src,
    String alt,
    @JsonProperty("attached_to_variant")
    String attachedToVariant,
    String width,
    String height
) {
}
