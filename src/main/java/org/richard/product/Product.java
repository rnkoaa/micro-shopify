package org.richard.product;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.richard.utils.Strings;

public record Product(
    int id,
    String title,
    String price,
    String link,
    String vendor,
    String coverImage,
    String type,
    String htmlDescription,
    boolean available,
    List<String> tags,
    List<Image> images,
    List<Variant> variants,
    Set<ProductOption> options,
    SwatchColor swatchColor,
    Instant createdAt,
    Instant updatedAt
) implements Cloneable {

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

    public Product clone() throws CloneNotSupportedException {
        super.clone();
        return toBuilder().build();
    }

    public Product withImages(List<Image> images) {
        return toBuilder()
            .images(images)
            .build();
    }

    public Product withVariants(List<Variant> savedVariants) {
        return toBuilder().variants(savedVariants)
            .build();
    }

    public Product mergeWith(Product product) {
        var builder = toBuilder();
        if (!Objects.equals(this.link, product.link) && !Strings.isNullOrEmpty(product.link)) {
            builder.link(product.link);
        }
        if (!Objects.equals(this.title, product.title) && !Strings.isNullOrEmpty(product.title)) {
            builder.title(product.title);
        }
        if (!Objects.equals(this.price, product.price) && !Strings.isNullOrEmpty(product.price)) {
            builder.price(product.price);
        }
        if (!Objects.equals(this.htmlDescription, product.htmlDescription) && !Strings.isNullOrEmpty(
            product.htmlDescription)) {
            builder.htmlDescription(product.htmlDescription);
        }
        if (!Objects.equals(this.type, product.type) && !Strings.isNullOrEmpty(product.type)) {
            builder.type(product.type);
        }
        if (!Objects.equals(this.coverImage, product.coverImage) && !Strings.isNullOrEmpty(product.coverImage)) {
            builder.coverImage(product.coverImage);
        }
        if (!Objects.equals(this.swatchColor, product.swatchColor) && (product.swatchColor != null && product.swatchColor.valid())) {
            builder.swatchColor(product.swatchColor);
        }

        builder.images(mergeImages(this.images, product.images));
        builder.variants(mergeVariants(this.variants, product.variants));

        if (product.available && !this.available) {
            builder.available(true);
        }
        return builder.build();
    }

    private List<Variant> mergeVariants(List<Variant> variants, List<Variant> toMerge) {
        if (toMerge == null || toMerge.isEmpty()) {
            return variants;
        }
        Map<String, Variant> variantMap = toMerge
            .stream()
            .collect(Collectors.toMap(Variant::title, it -> it));

        return variants.stream()
            .map(variant -> variant.mergeWith(variantMap.get(variant.title())))
            .distinct()
            .toList();
    }

    private List<Image> mergeImages(List<Image> images, List<Image> toMerge) {
        if (toMerge == null || toMerge.isEmpty()) {
            return images;
        }
        Map<String, Image> imageMap = toMerge
            .stream()
            .collect(Collectors.toMap(Image::src, it -> it));

        return images.stream()
            .map(image -> image.mergeWith(imageMap.get(image.src())))
            .distinct()
            .toList();
    }

    public static class Builder {

        private int id;
        private String title;
        private String link;
        private String price;
        private String coverImage;
        private boolean available;
        private String type;
        private String htmlDescription;
        private List<String> tags;
        private List<Image> images;
        private List<Variant> variants;
        private String vendor;
        private Set<ProductOption> options;
        private SwatchColor swatchColor;
        private Instant createdAt;
        private Instant updatedAt;

        public Builder() {}

        public Builder(Product product) {
            this.id = product.id;
            this.title = product.title;
            this.link = product.link;
            this.price = product.price;
            this.coverImage = product.coverImage;
            this.available = product.available;
            this.swatchColor = product.swatchColor;
            this.type = product.type;
            this.htmlDescription = product.htmlDescription;
            this.tags = product.tags;
            this.images = product.images;
            this.variants = product.variants;
            this.options = product.options;
            this.createdAt = product.createdAt;
            this.updatedAt = product.updatedAt;
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

        public Builder htmlDescription(String htmlDescription) {
            this.htmlDescription = htmlDescription;
            return this;
        }

        public Builder vendor(String vendor) {
            this.vendor = vendor;
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

        public Builder options(Set<ProductOption> options) {
            this.options = options;
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

        public Builder swatchColor(SwatchColor swatchColor) {
            this.swatchColor = swatchColor;
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

        public Product build() {
            return new Product(id, title, price, link, vendor,
                coverImage, type,
                htmlDescription,
                available,
                tags, images, variants, options, swatchColor,
                createdAt, updatedAt);
        }

    }
}
