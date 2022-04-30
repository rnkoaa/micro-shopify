package org.richard;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.richard.frankoak.SortOption;

@JsonDeserialize(builder = Category.Builder.class)
public record Category(
    int id,
    String name,
    String url,
    String description,
    int position,
    Map<String, String> hero,
    List<SortOption> sortOptions,
    List<String> defaultFilterGroups,
    Category parent,
    Set<Category> children
) {

    public static Builder builder() {
        return new Builder();
    }

    @JsonPOJOBuilder(withPrefix = "")
    static class Builder {

        private int id;
        private String name;
        private String url;
        private String description;
        private int position;
        private Map<String, String> hero;
        private List<SortOption> sortOptions;

        @JsonProperty("sortOptions")
        private List<SortOption> originalSortOptions;
        private List<String> defaultFilterGroups;
        private Category parent;
        private Set<Category> children;

        public Builder() {}

        public Builder(Category category) {
            this.id = category.id;
            this.name = category.name;
            this.description = category.description;
            this.position = category.position;
            this.hero = category.hero;
            this.sortOptions = category.sortOptions;
            this.defaultFilterGroups = category.defaultFilterGroups;
            this.parent = category.parent;
            this.children = category.children;
        }

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder position(int position) {
            this.position = position;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }
        public Builder title(String title) {
            this.name = title;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder parent(Category parent) {
            this.parent = parent;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder hero(Map<String, String> hero) {
            this.hero = hero;
            return this;
        }

        public Builder sortOptions(List<SortOption> sortOptions) {
            this.sortOptions = sortOptions;
            return this;
        }

        public Builder defaultFilterGroups(List<String> defaultFilterGroups) {
            this.defaultFilterGroups = defaultFilterGroups;
            return this;
        }

        public Builder children(Set<Category> children) {
            this.children = children;
            return this;
        }

        public Category build() {
            return new Category(id, name, url, description, position, hero, sortOptions, defaultFilterGroups, parent,
                children);
        }
    }

    public String toSimpleString() {
        if (url == null || url.isEmpty()) {
            return "Category[name=" + name + "]";
        }
        return "Category[name=" + name + ", " + "url=" + url + "]";
    }

    public Category withChildren(Set<Category> grandChildren) {
        return new Builder(this)
            .children(grandChildren)
            .build();
    }

    public Category withId(int id) {
        return new Builder(this)
            .id(id)
            .build();
    }

    public Category addCategory(Category childCategory) {
        var copy = new HashSet<>(children);
        copy.add(childCategory);
        return withChildren(Set.copyOf(copy));
    }
}
