package org.richard.assertions;

import java.util.Objects;
import org.assertj.core.api.AbstractAssert;
import org.richard.product.Product;

public class ProductAssert extends AbstractAssert<ProductAssert, Product> {

    protected ProductAssert(Product actual) {
        super(actual, ProductAssert.class);
    }

    public ProductAssert hasTitle(String title) {
        isNotNull();
        if (!actual.title().equals(title)) {
            failWithMessage("Expected product to have title %s but was %s", title, actual.title());
        }
        return this;
    }

    public ProductAssert hasId(int id) {
        isNotNull();
        if (actual.id() != id) {
            failWithMessage("Expected product to have id %d but was %d", id, actual.id());
        }
        return this;
    }

    public ProductAssert hasType(String type) {
        isNotNull();
        if (!actual.type().equals(type)) {
            failWithMessage("Expected product to have type %s but was %s", type, actual.type());
        }
        return this;
    }

    public ProductAssert hasHandle(String handle) {
        isNotNull();
        if (!actual.link().equals(handle)) {
            failWithMessage("Expected product to have handle %s but was %s", handle, actual.link());
        }
        return this;
    }

    public ProductAssert hasPrice(String price) {
        isNotNull();
        if (!actual.price().equals(price)) {
            failWithMessage("Expected product to have price %s but was %s", price, actual.price());
        }
        return this;
    }

    public ProductAssert hasCoverImage(String coverImage) {
        isNotNull();
        if (!actual.coverImage().equals(coverImage)) {
            failWithMessage("Expected product to have cover image %s but was %s", coverImage,
                actual.coverImage());
        }
        return this;
    }

    public ProductAssert hasLink(String handle) {
        isNotNull();
        if (!actual.link().equals(handle)) {
            failWithMessage("Expected product to have handle %s but was %s", handle, actual.link());
        }
        return this;
    }

    public ProductAssert isAvailable() {
        isNotNull();
        if (!actual.available()) {
            failWithMessage("Expected product to be available but was not");
        }
        return this;
    }

    public ProductAssert hasVariantSize(int size) {
        isNotNull();
        if (actual.variants() == null) {
            failWithMessage("Expected product to have variants with size %d but was null", size);
            return this;
        }

        if (actual.variants().size() != size) {
            failWithMessage("Expected product to have variants with size %d but was %d", size,
                actual.variants().size());
        }
        return this;
    }

    public ProductAssert hasImagesSize(int size) {
        isNotNull();
        if (actual.images() == null) {
            failWithMessage("Expected product to have images with size %d but was null", size);
            return this;
        }

        if (actual.images().size() != size) {
            failWithMessage("Expected product to have images with size %d but was %d", size,
                actual.images().size());
        }
        return this;
    }

    public ProductAssert hasSameIdAs(Product product) {
        isNotNull();
        if (actual.id() != product.id()) {
            failWithMessage("Expected product to have id %d but was %d", product.id(), actual.id());
            return this;
        }

        return this;
    }

    public ProductAssert hasOptionsSize(int size) {
        isNotNull();
        if (actual.options() == null) {
            failWithMessage("Expected product to have options with size %d but was null", size);
            return this;
        }

        if (actual.options().size() != size) {
            failWithMessage("Expected product to have options with size %d but was %d", size,
                actual.options().size());
        }
        return this;
    }

    public ProductAssert hasHtmlDescription(String htmDescription) {
        isNotNull();
        if (!Objects.equals(actual.htmlDescription(), htmDescription)) {
            failWithMessage("Expected product to have html description but was not equal");
            return this;
        }

        return this;
    }
}
