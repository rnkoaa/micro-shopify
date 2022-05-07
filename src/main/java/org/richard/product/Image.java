package org.richard.product;

import java.time.Instant;

public record Image(
    int id,
    String src,
    String alt,
    int position,
    Product product,
    ImageSize size,
    Instant createdAt,
    Instant updatedAt) {

    public Image withProduct(Product product) {
        return new Image(id, src, alt, position, product, size, createdAt, updatedAt);
    }

    public Image withId(Integer id) {
        return new Image(id, src, alt, position, product, size, createdAt, updatedAt);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private int id;
        private String src;
        private String alt;
        private int position;
        private Product product;
        private ImageSize imageSize;
        private Instant createdAt;
        private Instant updatedAt;

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder position(int position) {
            this.position = position;
            return this;
        }

        public Builder src(String src) {
            this.src = src;
            return this;
        }

        public Builder alt(String alt) {
            this.alt = alt;
            return this;
        }

        public Builder imageSize(ImageSize imageSize) {
            this.imageSize = imageSize;
            return this;
        }

        public Builder imageSize(int width, int height) {
            this.imageSize = new ImageSize(width, height);
            return this;
        }

        public Builder imageSize(Product product) {
            this.product = product;
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

        public Builder() {}

        public Image build() {
            return new Image(
                id, src, alt, position, product, imageSize, createdAt, updatedAt
            );
        }
    }
}
