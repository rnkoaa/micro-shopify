package org.richard.assertions;

import org.richard.frankoak.product.ProductItemResponse;
import org.richard.product.Product;
import org.richard.frankoak.category.ProductResponse;

public class MyAssertions {

    public static ProductResponseAssert assertThat(ProductResponse actual) {
        return new ProductResponseAssert(actual);
    }

    public static ProductItemResponseAssert assertThat(ProductItemResponse actual) {
        return new ProductItemResponseAssert(actual);
    }

    public static ProductAssert assertThat(Product actual) {
        return new ProductAssert(actual);
    }
}
