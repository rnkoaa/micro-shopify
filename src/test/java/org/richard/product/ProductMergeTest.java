package org.richard.product;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.richard.FileReader.readResourceFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.richard.assertions.MyAssertions;
import org.richard.config.ObjectMapperFactory;
import org.richard.frankoak.category.ProductResponse;
import org.richard.frankoak.converter.ProductItemResponseConverter;
import org.richard.frankoak.converter.ProductResponseMapper;
import org.richard.frankoak.product.ProductDetailResponse;
import org.richard.frankoak.product.ProductItemResponse;

class ProductMergeTest {

    final ObjectMapper objectMapper = ObjectMapperFactory.buildObjectMapper();
    final ProductItemResponseConverter productResponseMapper = new ProductItemResponseConverter();
    final ProductResponseMapper summaryResponseMapper = new ProductResponseMapper();


    @Test
    void mergeTwoProducts() throws IOException {
        ProductResponse productResponse = objectMapper.readValue(
            readResourceFile("data/selvedge-slim-fit-jean-in-black.json"), ProductResponse.class);
        MyAssertions.assertThat(productResponse)
            .isAvailable()
            .hasHandle("1210451-003")
            .hasTitle("The Selvedge Slim Fit Jean in Black")
            .hasFeaturedImage("//cdn.shopify.com/s/files/1/0555/5722/6653/products/1210451-003.0205.jpg?v=1646080506")
            .hasType("Bottoms")
            .hasVariantSize(8)
            .hasImagesSize(7)
            .hasPrice("14900");

        String path = "data/1210451-003.json";
        ProductDetailResponse productDetailResponse = objectMapper.readValue(
            readResourceFile(path),
            ProductDetailResponse.class);
        assertThat(productDetailResponse).isNotNull();

        ProductItemResponse productItemResponse = productDetailResponse.product();
        assertThat(productItemResponse).isNotNull();

        MyAssertions.assertThat(productItemResponse)
            .hasHandle("1210451-003")
            .hasTitle("The Selvedge Slim Fit Jean in Black")
            .hasType("Bottoms")
            .hasVariantSize(8)
            .hasImagesSize(7)
            .hasOptionsSize(7);

        Product productDetail = productResponseMapper.convert(productItemResponse);
        Product summaryProduct = summaryResponseMapper.convert(productResponse);

        var finalProduct = summaryProduct.mergeWith(productDetail);
        MyAssertions.assertThat(finalProduct)
            .isNotNull()
            .isAvailable()
            .hasHandle("1210451-003")
            .hasTitle("The Selvedge Slim Fit Jean in Black")
            .hasType("Bottoms")
            .hasVariantSize(8)
            .hasImagesSize(7)
            .hasOptionsSize(7);

        assertThat(finalProduct.htmlDescription()).isNotEmpty();
    }
}
