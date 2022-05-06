package org.richard;

import org.richard.frankoak.product.ProductItemResponse;
import org.richard.product.Product;

public class ProductItemResponseConverter implements ResponseConverter<Product, ProductItemResponse> {

    @Override
    public Product convert(ProductItemResponse value) {
        return null;
    }
}
