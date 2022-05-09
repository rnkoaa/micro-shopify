package org.richard.frankoak.converter;

import java.time.Instant;
import org.richard.frankoak.category.ProductVariant;
import org.richard.frankoak.category.VariantImage;
import org.richard.product.Image;
import org.richard.product.Inventory;
import org.richard.product.Variant;

public class VariantItemSummaryResponseConverter implements ResponseConverter<Variant, ProductVariant> {

    @Override
    public Variant convert(ProductVariant productVariant) {
        if (productVariant == null) {
            return null;
        }

        return Variant.builder()
            .title(productVariant.title())
            .price(productVariant.price())
            .handle(productVariant.handle())
            .image(createVariantImage(productVariant.image()))
            .inventory(createInventory(productVariant.inventoryQuantity()))
            .available(productVariant.available())
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();
    }

    private Inventory createInventory(int inventoryQuantity) {
        return new Inventory("", inventoryQuantity);
    }

    private Image createVariantImage(VariantImage image) {
        if (image == null) {
            return null;
        }
        return new Image(0, image.src(), image.alt(), 0, null, null, Instant.now(), Instant.now());
    }

}
