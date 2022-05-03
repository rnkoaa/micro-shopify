package org.richard.frankoak;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.richard.config.ObjectMapperFactory.buildObjectMapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;
import org.richard.FileReader;
import org.richard.assertions.ProductAssertions;
import org.richard.frankoak.category.CollectionResponse;
import org.richard.frankoak.category.ProductResponse;
import org.richard.frankoak.product.ProductDetailResponse;
import org.richard.frankoak.product.ProductItemResponse;

public class JsonDeserializationTest {

    private static final ObjectMapper objectMapper = buildObjectMapper();

    @Test
    void canDeserializeCollectionResponse() throws JsonProcessingException {
        String content = FileReader.readFile("category-pages/json/women-outerwear.json");
        assertThat(content).isNotEmpty();

        CollectionResponse response = objectMapper.readValue(content, CollectionResponse.class);
        assertThat(response).isNotNull();
        assertThat(response.collection()).isNotNull();
        assertThat(response.products()).isNotNull().hasSize(16);

//        System.out.println(response.collection());
//        System.out.println("----------------------------------------------");
//        for (ProductResponse product : response.products()) {
//            System.out.println("Product: " + product.title());
//            System.out.println("Images: " + product.images().size());
//            System.out.println("Variants: " + product.variants().size());
//        }
//
//
//
//        Set<String> productHandles = response.products()
//            .stream()
//            .map(ProductResponse::handle)
//            .collect(Collectors.toSet());
//
//        System.out.println(productHandles.size());
    }

    @Test
    void canDeserializeProductDetailResponse() throws JsonProcessingException {
        String content = FileReader.readFile(
            "product-pages/json/walters-eco-leather-care-kit-in-multi-4160006-915.json");
        assertThat(content).isNotEmpty();

        ProductDetailResponse response = objectMapper.readValue(content, ProductDetailResponse.class);
        assertThat(response).isNotNull();
        ProductItemResponse product = response.product();
        assertThat(product).isNotNull();

        assertThat(product.id()).isEqualTo(6872669847709L);
        assertThat(product.title()).isEqualTo("Walter's Eco Leather Care Kit in Multi");
        assertThat(product.bodyHtml()).isNotEmpty();
        assertThat(product.vendor()).isNotEmpty();
        assertThat(product.productType()).isEqualTo("Lifestyle");
        assertThat(product.images()).hasSize(2);
        assertThat(product.variants()).hasSize(1);
        assertThat(product.options()).hasSize(1);
        assertThat(product.image()).isNotNull();

        assertThat(product.variants().get(0).compareAtPrice()).isEqualTo("24.99");
        assertThat(product.variants().get(0).fulfillmentService()).isEqualTo("manual");
        assertThat(product.variants().get(0).inventoryManagement()).isEqualTo("shopify");
        assertThat(product.variants().get(0).requiresShipping()).isTrue();
//        assertThat(product.variants().get(0).createdAt()).isNotNull();
//        assertThat(product.variants().get(0).updatedAt()).isNotNull();

//        System.out.println(response.collection());
//        System.out.println("----------------------------------------------");
//        for (ProductResponse product : response.products()) {
//            System.out.println("Product: " + product.title());
//            System.out.println("Images: " + product.images().size());
//            System.out.println("Variants: " + product.variants().size());
//        }
//
//
//
//        Set<String> productHandles = response.products()
//            .stream()
//            .map(ProductResponse::handle)
//            .collect(Collectors.toSet());
//
//        System.out.println(productHandles.size());
    }

    @Test
    void canDeserializeProductResponse() throws IOException {
        String path = "data/selvedge-slim-fit-jean-in-black.json";
        InputStream inputStream = FileReader.readResourceFile(path);
        ProductResponse productResponse = objectMapper.readValue(inputStream, ProductResponse.class);
        assertThat(productResponse).isNotNull();

        ProductAssertions.assertThat(productResponse)
            .isAvailable()
            .hasHandle("1210451-003")
            .hasTitle("The Selvedge Slim Fit Jean in Black")
            .hasFeaturedImage("//cdn.shopify.com/s/files/1/0555/5722/6653/products/1210451-003.0205.jpg?v=1646080506")
            .hasType("Bottoms")
            .hasVariantSize(8)
            .hasImagesSize(7)
            .hasPrice("14900");
    }
}
