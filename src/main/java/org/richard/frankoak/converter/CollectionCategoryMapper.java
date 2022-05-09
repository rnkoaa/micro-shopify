package org.richard.frankoak.converter;

import java.util.List;
import java.util.Map;
import org.richard.frankoak.SortOption;
import org.richard.frankoak.category.Collection;
import org.richard.frankoak.category.CollectionHero;
import org.richard.frankoak.category.CollectionSortOption;
import org.richard.product.Category;

public class CollectionCategoryMapper implements ResponseConverter<Category, Collection> {

    public Category convert(Collection collection) {
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

    public List<Category> convert(List<Collection> collections) {
        return collections.stream()
            .map(this::convert)
            .toList();
    }

    private Map<String, String> map(CollectionHero hero) {
        if (hero == null) {
            return Map.of();
        }
        return hero.decompose();
    }

    private List<SortOption> map(List<CollectionSortOption> sortOptionList) {
        if (sortOptionList == null) {
            return List.of();
        }

        return sortOptionList.stream()
            .map(s -> new SortOption(s.id(), s.label()))
            .toList();
    }
}
