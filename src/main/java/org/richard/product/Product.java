package org.richard.product;

import java.util.ArrayList;
import java.util.List;

public record Product(
    int id,
    String title,
    String price,
    String link,
    String coverImage,
    boolean available,
    String type,
    List<String> tags,
    List<Image> images,
    List<Variant> variants
) {

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    public Product withId(Integer id) {
        return toBuilder()
            .id(id)
            .build();
    }

    public static class Builder {

        private int id;
        private String title;
        private String link;
        private String price;
        private String coverImage;
        private boolean available;
        private String type;
        private List<String> tags;
        private List<Image> images;
        private List<Variant> variants;

        public Builder() {}

        public Builder(Product product) {
            this.id = product.id;
            this.title = product.title;
            this.link = product.link;
            this.price = product.price;
            this.coverImage = product.coverImage;
            this.available = product.available;
            this.type = product.type;
            this.tags = product.tags;
            this.images = product.images;
            this.variants = product.variants;
        }

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder tags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        public Builder images(List<Image> images) {
            this.images = images;
            return this;
        }

        public Builder variants(List<Variant> variants) {
            this.variants = variants;
            return this;
        }

        public Builder addTag(String tag) {
            if (this.tags == null) {
                this.tags = new ArrayList<>();
            }
            this.tags.add(tag);
            return this;
        }

        public Builder available(boolean available) {
            this.available = available;
            return this;
        }

        public Builder price(String price) {
            this.price = price;
            return this;
        }

        public Builder coverImage(String coverImage) {
            this.coverImage = coverImage;
            return this;
        }

        public Builder link(String link) {
            this.link = link;
            return this;
        }

        public Product build() {
            return new Product(id, title, price, link, coverImage, available,
                type, tags, images, variants);
        }

    }
}
