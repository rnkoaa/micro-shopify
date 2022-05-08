package org.richard.product;

import java.util.Set;

public record ProductOption(
    String name,
    int position,
    Set<String> values
) {

    public ProductOption(String name, Set<String> values) {
        this(name, 0, values);
    }
}
