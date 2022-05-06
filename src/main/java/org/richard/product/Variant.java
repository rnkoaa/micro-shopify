package org.richard.product;

public record Variant(
    String title,
    String price,
    Image image,
    Inventory inventory,
    boolean available
) {
}
