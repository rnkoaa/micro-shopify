package org.richard;

import org.richard.ResponseConverter;
import org.richard.frankoak.product.VariantItem;
import org.richard.product.Inventory;
import org.richard.product.Variant;
import org.richard.product.Weight;

public class VariantItemDetailResponseConverter implements ResponseConverter<Variant, VariantItem> {

    @Override
    public Variant convert(VariantItem variantItem) {
        return Variant.builder()
            .title(variantItem.title())
            .price(variantItem.price())
            .compareAtPrice(variantItem.compareAtPrice())
            .position(variantItem.position())
            .handle(variantItem.sku())
            .barcode(variantItem.barcode())
            .sku(variantItem.sku())
            .fulfillmentService(variantItem.fulfillmentService())
            .requiresShipping(variantItem.requiresShipping())
            .inventory(new Inventory(variantItem.inventoryManagement(), 0))
            .weight(new Weight(variantItem.weight(), variantItem.weightUnit()))
            .taxable(variantItem.taxable())
            .taxCode(variantItem.taxCode())
            .build();
    }
}
