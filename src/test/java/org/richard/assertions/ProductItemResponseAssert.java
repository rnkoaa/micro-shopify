package org.richard.assertions;

import org.assertj.core.api.AbstractAssert;
import org.richard.frankoak.category.ProductResponse;
import org.richard.frankoak.product.ProductItemResponse;

public class ProductItemResponseAssert extends AbstractAssert<ProductItemResponseAssert, ProductItemResponse> {

    protected ProductItemResponseAssert(ProductItemResponse actual) {
        super(actual, ProductItemResponseAssert.class);
    }


    public ProductItemResponseAssert hasTitle(String title) {
        isNotNull();
        if (!actual.title().equals(title)) {
            failWithMessage("Expected product response to have title %s but was %s", title, actual.title());
        }
        return this;
    }

    public ProductItemResponseAssert hasType(String type) {
        isNotNull();
        if (!actual.productType().equals(type)) {
            failWithMessage("Expected product response to have type %s but was %s", type, actual.productType());
        }
        return this;
    }

//    public ProductItemResponseAssert hasPrice(String price) {
//        isNotNull();
//        if (!actual.().equals(price)) {
//            failWithMessage("Expected product response to have price %s but was %s", price, actual.price());
//        }
//        return this;
//    }

//    public ProductItemResponseAssert hasFeaturedImage(String featuredImage) {
//        isNotNull();
//        if (!actual.ima().equals(featuredImage)) {
//            failWithMessage("Expected product response to have featured image %s but was %s", featuredImage,
//                actual.featuredImage());
//        }
//        return this;
//    }

    public ProductItemResponseAssert hasHandle(String handle) {
        isNotNull();
        if (!actual.handle().equals(handle)) {
            failWithMessage("Expected product response to have handle %s but was %s", handle, actual.handle());
        }
        return this;
    }

//    public ProductItemResponseAssert isAvailable() {
//        isNotNull();
//        if (!actual.available()) {
//            failWithMessage("Expected product response to be available but was not");
//        }
//        return this;
//    }

    public ProductItemResponseAssert hasVariantSize(int size) {
        isNotNull();
        if (actual.variants() == null) {
            failWithMessage("Expected product response to have variants with size %d but was null", size);
            return this;
        }

        if (actual.variants().size() != size) {
            failWithMessage("Expected product response to have variants with size %d but was %d", size,
                actual.variants().size());
        }
        return this;
    }

    public ProductItemResponseAssert hasImagesSize(int size) {
        isNotNull();
        if (actual.images() == null) {
            failWithMessage("Expected product response to have images with size %d but was null", size);
            return this;
        }

        if (actual.images().size() != size) {
            failWithMessage("Expected product response to have images with size %d but was %d", size,
                actual.images().size());
        }
        return this;
    }
}
