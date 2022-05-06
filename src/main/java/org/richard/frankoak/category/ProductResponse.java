package org.richard.frankoak.category;

import java.util.List;
import java.util.Map;

public record ProductResponse(
    String id,
    List<ProductImage> images,
    List<ProductVariant> variants,
    boolean available,
    String handle,
    String title,
    String vendor,
    String type,
    String featuredImage,
    List<String> tags,
    String swatchColor,
    String swatchColorName,
    String price,
    List<String> optionNames,
    Map<String, List<String>> optionsWithValues,
    Collection siblingCollection
) {
}
