package org.richard.product;

import java.time.Instant;

public record Variant(
    int id,
    String title,
    String price,
    String handle,
    Product product,
    Image image,
    Inventory inventory,
    boolean available,
    Instant createdAt,
    Instant updatedAt
) {

    public static Builder builder() {
        return new Builder();
    }

    public Variant withProduct(Product product) {
        return new Builder(this)
            .product(product)
            .build();
    }

    public Variant withId(Integer id) {
        return new Builder(this)
            .id(id)
            .build();
    }

    public static class Builder {

        private int id;
        private String title;
        private String price;
        private String handle;
        private Product product;
        private Image image;
        private Inventory inventory;
        private boolean available;
        private Instant createdAt;
        private Instant updatedAt;

        public Builder() {}

        public Builder(Variant variant) {
            this.id = variant.id;
            this.title = variant.title;
            this.price = variant.price;
            this.image = variant.image;
            this.product = variant.product;
            this.handle = variant.handle;
            this.inventory = variant.inventory;
            this.available = variant.available;
            this.createdAt = variant.createdAt;
            this.updatedAt = variant.updatedAt;
        }

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder handle(String handle) {
            this.handle = handle;
            return this;
        }

        public Builder price(String price) {
            this.price = price;
            return this;
        }

        public Builder image(Image image) {
            this.image = image;
            return this;
        }

        public Builder inventory(Inventory inventory) {
            this.inventory = inventory;
            return this;
        }

        public Builder product(Product product) {
            this.product = product;
            return this;
        }

        public Builder available(boolean available) {
            this.available = available;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Variant build() {
            return new Variant(
                id,
                title,
                price,
                handle,
                product,
                image,
                inventory,
                available,
                createdAt,
                updatedAt
            );
        }

    }
}
