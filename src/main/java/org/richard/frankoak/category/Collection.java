package org.richard.frankoak.category;

import java.util.List;

public record Collection(
    String handle,
    String title,
    String description,
    String image,
    boolean fetched,

    List<CollectionSortOption> sortOptions,
    CollectionHero hero,
    List<String> defaultFilterGroupPrefix
) {}
