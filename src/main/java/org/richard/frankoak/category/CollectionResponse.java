package org.richard.frankoak.category;

import java.util.List;

public record CollectionResponse(
    Collection collection,
    List<ProductResponse> products
) {}
