package org.richard.frankoak.category;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.List;

@JsonDeserialize(builder = Collection.Builder.class)
public record Collection(
    int position,
    String handle,
    String title,
    String description,
    String image,
    boolean fetched,

    @JsonProperty("sortOptions")
    List<CollectionSortOption> sortOptions,
    CollectionHero hero,

    @JsonProperty("defaultFilterGroupPrefix")
    List<String> defaultFilterGroupPrefix
) {

    public static Builder builder() {
        return new Builder();
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {

        String handle;
        String title;
        String description;
        String image;
        boolean fetched;

        @JsonProperty("sortOptions")
        List<CollectionSortOption> sortOptions;
        CollectionHero hero;

        @JsonProperty("defaultFilterGroupPrefix")
        List<String> defaultFilterGroupPrefix;
        int position;

        public Builder() {}

        public Builder(Collection collection) {
            this.handle = collection.handle;
            this.title = collection.title;
            this.description = collection.description;
            this.image = collection.image;
            this.fetched = collection.fetched;
            this.sortOptions = collection.sortOptions;
            this.hero = collection.hero;
            this.defaultFilterGroupPrefix = collection.defaultFilterGroupPrefix;
            this.position = collection.position;
        }

        public Builder position(int position) {
            this.position = position;
            return this;
        }

        public Builder hero(CollectionHero hero) {
            this.hero = hero;
            return this;
        }

        public Builder sortOptions(List<CollectionSortOption> sortOptions) {
            this.sortOptions = sortOptions;
            return this;
        }

        public Builder defaultFilterGroupPrefix(List<String> defaultFilterGroupPrefix) {
            this.defaultFilterGroupPrefix = defaultFilterGroupPrefix;
            return this;
        }

        public Builder handle(String handle) {
            this.handle = handle;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder image(String image) {
            this.image = image;
            return this;
        }

        public Builder fetched(boolean fetched) {
            this.fetched = fetched;
            return this;
        }

        public Collection build() {
            return new Collection(
                position,
                handle, title, description,
                image, fetched,
                sortOptions,
                hero,
                defaultFilterGroupPrefix);
        }

    }

}
