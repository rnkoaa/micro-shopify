package org.richard;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.richard.frankoak.product.ImageItem;
import org.richard.frankoak.product.ProductItemResponse;
import org.richard.frankoak.product.VariantItem;
import org.richard.product.Image;
import org.richard.product.ImageSize;
import org.richard.product.Inventory;
import org.richard.product.Product;
import org.richard.product.ProductOption;
import org.richard.product.Variant;
import org.richard.product.Weight;
import org.richard.utils.Strings;

public class ProductItemResponseConverter implements ResponseConverter<Product, ProductItemResponse> {

    @Override
    public Product convert(ProductItemResponse value) {
        var builder = Product.builder()
            .title(value.title())
            .htmlDescription(value.bodyHtml())
            .vendor(value.vendor())
            .type(value.productType())
            .link(value.handle());

        String tags = value.tags();
        if (!Strings.isNullOrEmpty(tags)) {
            String[] tagArray = tags.split(",");
            builder.tags(List.of(tagArray));
        }

        if (value.options() != null) {
            Set<ProductOption> productOptions = value.options()
                .stream()
                .map(optionItem ->
                    new ProductOption(optionItem.name(), optionItem.position(),
                        new HashSet<>(optionItem.values())))
                .collect(Collectors.toSet());
            builder.options(productOptions);
        }

        if (value.images() != null && value.images().size() > 0) {
            builder.images(convertImages(value.images()));
        }
        if (value.variants() != null && value.variants().size() > 0) {
            builder.variants(convertVariants(value.variants()));
        }
        if (value.image() != null) {
            builder.coverImage(value.image().src());
        }
        return builder.build();
    }

    private List<Variant> convertVariants(List<VariantItem> variants) {
        return variants.stream()
            .map(variantItem -> Variant.builder()
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
                .build()
            )
            .toList();
    }

    private List<Image> convertImages(List<ImageItem> images) {
        return images.stream()
            .map(imageItem -> Image.builder()
                .src(imageItem.src())
                .alt(imageItem.alt())
                .position(imageItem.position())
                .imageSize(new ImageSize(imageItem.width(), imageItem.height()))
                .build())
            .toList();
    }
}
