package org.richard;

import static org.richard.Strings.isNotNullOrEmpty;
import static org.richard.Strings.isNullOrEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    Set<Category> children,
    Instant createdAt,
    Instant updatedAt
) {

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Category category = (Category) o;
        return id == category.id && position == category.position && Objects.equals(name, category.name)
            && Objects.equals(url, category.url) && Objects.equals(description, category.description)
            && Objects.equals(parent, category.parent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, url, description, position, parent);
    }

    public Category withParent(Category parent) {
        return new Builder(this).parent(parent).build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public Category withUrl(String url) {
        return new Builder(this).url(url).build();
    }

    public Category mergeWith(Category category) {
        var categoryBuilder = new Builder(this);
        if (category != null) {
            if (isNotNullOrEmpty(category.url)) {
                categoryBuilder.url(category.url);
            }
            if (isNotNullOrEmpty(category.description)) {
                categoryBuilder.description(category.description);
            }

            if (category.position > 0) {
                categoryBuilder.position(category.position);
            }
            if (category.defaultFilterGroups != null) {
                categoryBuilder.defaultFilterGroups(category.defaultFilterGroups);
            }
            if (category.sortOptions != null) {
                categoryBuilder.sortOptions(category.sortOptions);
            }
            if (category.hero != null) {
                categoryBuilder.hero(category.hero);
            }
            categoryBuilder.updatedAt(Instant.now());
        }

        return categoryBuilder.build();
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {

        private int id;
        private String name;
        private String url;
        private String description;
        private int position;
        private Map<String, String> hero;
        private List<SortOption> sortOptions;

        private List<String> defaultFilterGroups;
        private Category parent;
        private Set<Category> children;
        private Instant createdAt;
        private Instant updatedAt;

        public Builder() {}

        public Builder(Category category) {
            this.id = category.id;
            this.name = category.name;
            this.url = category.url;
            this.description = category.description;
            this.position = category.position;
            this.hero = category.hero;
            this.sortOptions = category.sortOptions;
            this.defaultFilterGroups = category.defaultFilterGroups;
            this.parent = category.parent;
            this.children = category.children;
            this.createdAt = category.createdAt;
            this.updatedAt = category.updatedAt;
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
            return new Category(id, name, url,
                description,
                position,
                hero,
                sortOptions, defaultFilterGroups, parent,
                children, createdAt, updatedAt);
        }

        public Builder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }
    }

    public String toSimpleString() {
        String parentName = "";
        if (parent != null) {
            parentName = parent.name;
        }
        if (isNullOrEmpty(url)) {
            return "Category[name=" + name + ", parent=" + parentName + "]";
        }
        return "Category[name=" + name + ", " + "url=" + url + ", parent=" + parentName + "]";
    }

    public Category withId(int id) {
        return new Builder(this)
            .id(id)
            .build();
    }

}
