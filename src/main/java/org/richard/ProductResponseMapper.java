package org.richard;

import static org.richard.IntegerUtils.safeParse;

import java.util.List;
import java.util.Objects;
import org.richard.frankoak.category.ProductImage;
import org.richard.frankoak.category.ProductResponse;
import org.richard.frankoak.category.ProductVariant;
import org.richard.frankoak.category.VariantImage;
import org.richard.product.Image;
import org.richard.product.ImageSize;
import org.richard.product.Inventory;
import org.richard.product.Product;
import org.richard.product.Variant;

public class ProductResponseMapper implements ResponseConverter<Product, ProductResponse> {

    @Override
    public Product convert(ProductResponse productResponse) {
        var builder = Product.builder()
            .title(productResponse.title())
            .coverImage(productResponse.featuredImage())
            .link(productResponse.handle())
            .tags(productResponse.tags())
            .available(productResponse.available())
            .type(productResponse.type())
            .price(productResponse.price())
            .images(convertImages(productResponse.images()))
            .variants(convertVariants(productResponse.variants()));
        return builder.build();
    }

    private List<Variant> convertVariants(List<ProductVariant> variants) {
        if (variants == null) {
            return List.of();
        }
        return variants.stream()
            .map(this::convertVariant)
            .filter(Objects::nonNull)
            .toList();
    }

    private Variant convertVariant(ProductVariant productVariant) {
        if (productVariant == null) {
            return null;
        }

        return new Variant(productVariant.title(), productVariant.price(),
            createVariantImage(productVariant.image()),
            createInventory(productVariant.inventoryQuantity()),
            productVariant.available()
        );
    }

    private Image createVariantImage(VariantImage image) {
        if (image == null) {
            return null;
        }
        return new Image(image.src(), image.alt(), null);
    }

    private Inventory createInventory(int inventoryQuantity) {
        return new Inventory(inventoryQuantity);
    }

    List<Image> convertImages(List<ProductImage> productImages) {
        return productImages.stream()
            .map(this::imageImage)
            .toList();
    }

    private Image imageImage(ProductImage productImage) {
        return new Image(productImage.src(), productImage.alt(),
            new ImageSize(
                safeParse(productImage.width()), safeParse(productImage.height())
            )
        );
    }
}
