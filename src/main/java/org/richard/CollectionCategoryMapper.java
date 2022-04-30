package org.richard;

import java.util.List;
import java.util.Map;
import org.richard.frankoak.SortOption;
import org.richard.frankoak.category.Collection;
import org.richard.frankoak.category.CollectionHero;
import org.richard.frankoak.category.CollectionSortOption;

public class CollectionCategoryMapper {

    public Category map(Collection collection) {
        return Category.builder()
            .name(collection.title())
            .description(collection.description())
            .url(collection.handle())
            .position(collection.position())
            .defaultFilterGroups(collection.defaultFilterGroupPrefix())
            .hero(map(collection.hero()))
            .sortOptions(map(collection.sortOptions()))

            .build();
    }

    Map<String, String> map(CollectionHero hero) {
        if (hero == null) {
            return Map.of();
        }
        return hero.decompose();
    }

    List<SortOption> map(List<CollectionSortOption> sortOptionList) {
        if (sortOptionList == null) {
            return List.of();
        }

        return sortOptionList.stream()
            .map(s -> new SortOption(s.id(), s.label()))
            .toList();
    }
}
