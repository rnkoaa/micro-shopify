package org.richard.assertions;

import org.richard.frankoak.category.ProductResponse;

public class ProductAssertions {

    public static ProductResponseAssert assertThat(ProductResponse actual) {
        return new ProductResponseAssert(actual);
    }
}
