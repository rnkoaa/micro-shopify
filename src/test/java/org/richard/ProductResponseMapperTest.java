package org.richard;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.richard.config.ObjectMapperFactory.buildObjectMapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Test;
import org.richard.assertions.MyAssertions;
import org.richard.frankoak.category.ProductResponse;
import org.richard.frankoak.converter.ProductResponseMapper;
import org.richard.product.Product;

class ProductResponseMapperTest {

    private static final ObjectMapper objectMapper = buildObjectMapper();

    private final ProductResponseMapper productResponseMapper = new ProductResponseMapper();

    @Test
    void canDeserializeProductResponse() throws IOException {
        String path = "data/selvedge-slim-fit-jean-in-black.json";
        InputStream inputStream = FileReader.readResourceFile(path);
        ProductResponse productResponse = objectMapper.readValue(inputStream, ProductResponse.class);
        assertThat(productResponse).isNotNull();

        MyAssertions.assertThat(productResponse)
            .isAvailable()
            .hasHandle("1210451-003")
            .hasTitle("The Selvedge Slim Fit Jean in Black")
            .hasFeaturedImage("//cdn.shopify.com/s/files/1/0555/5722/6653/products/1210451-003.0205.jpg?v=1646080506")
            .hasType("Bottoms")
            .hasVariantSize(8)
            .hasImagesSize(7)
            .hasPrice("14900");

        Product product = productResponseMapper.convert(productResponse);
        MyAssertions.assertThat(product)
            .isNotNull()
            .isAvailable()
            .hasCoverImage(productResponse.featuredImage())
            .hasLink(productResponse.handle())
            .hasPrice(productResponse.price())
            .hasType(productResponse.type())
            .hasTitle(productResponse.title())
            .hasImagesSize(productResponse.images().size())
            .hasVariantSize(productResponse.variants().size());
    }

}