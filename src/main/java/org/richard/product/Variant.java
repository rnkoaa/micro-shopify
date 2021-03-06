package org.richard.product;

import java.time.Instant;
import java.util.Objects;
import org.richard.utils.Strings;

public record Variant(
    int id,
    String title,
    String price,
    String compareAtPrice,
    int position,
    String sku,
    String handle,
    Product product,
    Image image,
    Inventory inventory,
    boolean available,
    boolean taxable,
    String taxCode,
    String barcode,
    Weight weight,
    String fulfillmentService,
    boolean requiresShipping,
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

    public Variant withImage(Image image) {
        return new Builder(this)
            .image(image)
            .build();
    }

    public Variant mergeWith(Variant variant) {
        if(variant == null){
            return this;
        }
        var builder = new Builder(this);
        if (this.id != variant.id && variant.id > 0) {
            builder.id(variant.id);
        }
        if (this.position != variant.position && variant.position > 0) {
            builder.position(variant.position);
        }
        if (!Objects.equals(this.title, variant.title) && !Strings.isNullOrEmpty(variant.title)) {
            builder.title(variant.title);
        }
        if (!Objects.equals(this.price, variant.price) && !Strings.isNullOrEmpty(variant.price)) {
            builder.price(variant.price);
        }
        if (!Objects.equals(this.compareAtPrice, variant.compareAtPrice) && !Strings.isNullOrEmpty(
            variant.compareAtPrice)) {
            builder.compareAtPrice(variant.compareAtPrice);
        }
        if (!Objects.equals(this.sku, variant.sku) && !Strings.isNullOrEmpty(variant.sku)) {
            builder.sku(variant.sku);
        }
        if (!Objects.equals(this.barcode, variant.barcode) && !Strings.isNullOrEmpty(variant.barcode)) {
            builder.barcode(variant.barcode);
        }
        if (!Objects.equals(this.handle, variant.handle) && !Strings.isNullOrEmpty(variant.handle)) {
            builder.handle(variant.handle);
        }
        if (!Objects.equals(this.taxCode, variant.taxCode) && !Strings.isNullOrEmpty(variant.taxCode)) {
            builder.taxCode(variant.taxCode);
        }
        if (!Objects.equals(this.fulfillmentService, variant.fulfillmentService) && !Strings.isNullOrEmpty(
            variant.fulfillmentService)) {
            builder.fulfillmentService(variant.fulfillmentService);
        }
        if (product == null && variant.product != null) {
            builder.product(variant.product);
        }
        builder.taxable(taxable || variant.taxable);
        builder.available(available || variant.available);
        builder.requiresShipping(requiresShipping || variant.requiresShipping);

        if (weight != null) {
            builder.weight(weight.mergeWith(variant.weight));
        } else {
            builder.weight(variant.weight);
        }
        if (inventory != null) {
            builder.inventory(inventory.mergeWith(variant.inventory));
        } else {
            builder.inventory(variant.inventory);
        }
        if (image != null) {
            builder.image(image.mergeWith(variant.image));
        } else {
            builder.image(variant.image);
        }

        return builder.build();
    }

    public Variant withHandle(String variantHandle) {
        return new Builder(this).handle(variantHandle)
            .build();
    }

    public static class Builder {

        private String compareAtPrice;
        private String fulfillmentService;
        private String sku;
        private String barcode;
        private boolean requiresShipping;
        private String taxCode;
        private int id;
        private int position;
        private String title;
        private String price;
        private String handle;
        private Product product;
        private Image image;
        private Inventory inventory;
        private boolean available;
        private boolean taxable;
        private Instant createdAt;
        private Instant updatedAt;
        private Weight weight;

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
            this.taxable = variant.taxable;
            this.taxCode = variant.taxCode;
            this.position = variant.position;
            this.sku = variant.sku;
            this.compareAtPrice = variant.compareAtPrice;
            this.fulfillmentService = variant.fulfillmentService;
            this.requiresShipping = variant.requiresShipping;
        }

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder position(int position) {
            this.position = position;
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

        public Builder sku(String sku) {
            this.sku = sku;
            return this;
        }

        public Builder barcode(String barcode) {
            this.barcode = barcode;
            return this;
        }

        public Builder taxCode(String taxCode) {
            this.taxCode = taxCode;
            return this;
        }

        public Builder fulfillmentService(String fulfillmentService) {
            this.fulfillmentService = fulfillmentService;
            return this;
        }

        public Builder price(String price) {
            this.price = price;
            return this;
        }

        public Builder compareAtPrice(String compareAtPrice) {
            this.compareAtPrice = compareAtPrice;
            return this;
        }

        public Builder taxable(boolean taxable) {
            this.taxable = taxable;
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

        public Builder weight(Weight weight) {
            this.weight = weight;
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

        public Builder requiresShipping(boolean requiresShipping) {
            this.requiresShipping = requiresShipping;
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
                compareAtPrice,
                position,
                sku,
                handle,
                product,
                image,
                inventory,
                available,
                taxable,
                taxCode,
                barcode,
                weight,
                fulfillmentService,
                requiresShipping,
                createdAt,
                updatedAt
            );
        }

    }
}
