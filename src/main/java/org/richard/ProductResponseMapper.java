package org.richard;

import static org.richard.utils.IntegerUtils.safeParse;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.richard.frankoak.category.ProductImage;
import org.richard.frankoak.category.ProductResponse;
import org.richard.frankoak.category.ProductVariant;
import org.richard.frankoak.category.VariantImage;
import org.richard.product.Image;
import org.richard.product.ImageSize;
import org.richard.product.Inventory;
import org.richard.product.Product;
import org.richard.product.ProductOption;
import org.richard.product.SwatchColor;
import org.richard.product.Variant;
import org.richard.utils.Strings;

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
            .vendor(productResponse.vendor())
            .images(convertImages(productResponse.images()))
            .options(createProductOptions(productResponse))
            .swatchColor(createSwatchColor(productResponse))
            .variants(convertVariants(productResponse));
        return builder.build();
    }

    private SwatchColor createSwatchColor(ProductResponse productResponse) {
        if (productResponse.swatchColorName() == null || productResponse.swatchColor() == null) {
            return null;
        }

        return new SwatchColor(productResponse.swatchColorName(), productResponse.swatchColor());
    }

    private Set<ProductOption> createProductOptions(ProductResponse productResponse) {
        Map<String, List<String>> optionsWithValues = productResponse.optionsWithValues();
        if (optionsWithValues == null) {
            return Set.of();
        }

        return optionsWithValues.entrySet()
            .stream()
            .map(entry -> new ProductOption(entry.getKey(), new HashSet<>(entry.getValue())))
            .collect(Collectors.toSet());
    }

    private List<Variant> convertVariants(ProductResponse productResponse) {
        List<ProductVariant> variants = productResponse.variants();
        if (variants == null) {
            return List.of();
        }
        return variants.stream()
            .map(variant -> convertVariant(productResponse, variant))
            .filter(Objects::nonNull)
            .toList();
    }

    private Variant convertVariant(ProductResponse productResponse, ProductVariant productVariant) {
        if (productVariant == null) {
            return null;
        }

        String variantHandle = productVariant.handle();
        if (Strings.isNullOrEmpty(variantHandle)) {
            variantHandle = createVariantHandle(
                productResponse.handle(),
                productVariant.title());
        }

        return Variant.builder()
            .title(productVariant.title())
            .price(productVariant.price())
            .handle(variantHandle)
            .image(createVariantImage(productVariant.image()))
            .inventory(createInventory(productVariant.inventoryQuantity()))
            .available(productVariant.available())
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();
    }

    private Image createVariantImage(VariantImage image) {
        if (image == null) {
            return null;
        }
        return new Image(0, image.src(), image.alt(), 0, null, null, Instant.now(), Instant.now());
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
        return new Image(0,
            productImage.src(), productImage.alt(),
            0,
            null,
            new ImageSize(
                safeParse(productImage.width()), safeParse(productImage.height())
            ),
            Instant.now(),
            Instant.now()
        );
    }

    String createVariantHandle(String productHandle, String variantName) {
        String product = productHandle.replaceAll("(\\\\s)+|(&)+", "-").toLowerCase();
        String name = variantName.replaceAll("(\\s)+|(&)+", "-").toLowerCase();
        return "%s-%s".formatted(product, name);
    }
}
