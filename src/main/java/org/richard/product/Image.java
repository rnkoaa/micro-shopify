package org.richard.product;

import java.time.Instant;
import java.util.Objects;
import org.richard.utils.Strings;

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

    public Image mergeWith(Image image) {
        if (image == null) {
            return this;
        }
        var builder = new Builder(this);
        if (this.id != image.id && image.id > 0) {
            builder.id(image.id);
        }

        if (this.position != image.position && image.position > 0) {
            builder.position(image.position);
        }

        if (!Objects.equals(this.src, image.src) && !Strings.isNullOrEmpty(image.src)) {
            builder.src(image.src);
        }
        if (!Objects.equals(this.alt, image.alt) && !Strings.isNullOrEmpty(image.alt)) {
            builder.alt(image.alt);
        }

        if (this.size != image.size && (image.size != null && image.size.valid())) {
            builder.imageSize(image.size);
        }
        if (this.product != image.product && (image.product != null)) {
            builder.product(image.product);
        }
        return builder.build();
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

        public Builder(Image image) {
            this.id = image.id;
            this.src = image.src;
            this.alt = image.alt;
            this.position = image.position;
            this.product = image.product;
            this.imageSize = image.size;
            this.createdAt = image.createdAt;
            this.updatedAt = image.updatedAt;
        }

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

        public Builder product(Product product) {
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
