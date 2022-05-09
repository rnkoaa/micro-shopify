package org.richard.product;

import org.richard.utils.Strings;

public record SwatchColor(
    String name,
    String value
) {

    public boolean valid() {
        return Strings.isNotNullOrEmpty(name) && Strings.isNullOrEmpty(value);
    }
}
