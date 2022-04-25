package org.richard.frankoak.category;

import java.util.List;

public record Response(
    Collection collection,
    List<ProductResponse> products
) {}
