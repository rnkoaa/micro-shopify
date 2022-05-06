package org.richard.product;

import java.util.Set;

public record ProductOption(
    String name,
    Set<String> values
) {
}
