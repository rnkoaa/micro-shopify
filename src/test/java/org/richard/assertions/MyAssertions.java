package org.richard.assertions;

import org.richard.product.Product;
import org.richard.frankoak.category.ProductResponse;

public class MyAssertions {

    public static ProductResponseAssert assertThat(ProductResponse actual) {
        return new ProductResponseAssert(actual);
    }

    public static ProductAssert assertThat(Product actual) {
        return new ProductAssert(actual);
    }
}
