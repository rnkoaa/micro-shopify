package org.richard.frankoak;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.richard.config.ObjectMapperFactory.buildObjectMapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import org.richard.frankoak.category.CollectionResponse;
import org.richard.frankoak.product.ProductDetailResponse;
import org.richard.frankoak.product.ProductItemResponse;

public class JsonDeserializationTest {

    private static String readFile(String path) {
        try {
            return Files.readString(Paths.get(path));
        } catch (IOException e) {
            System.out.println("error while reading file");
            return "";
        }
    }

    @Test
    void canDeserializeResponse() throws JsonProcessingException {
        String content = readFile("category-pages/json/women-outerwear.json");
        assertThat(content).isNotEmpty();

        ObjectMapper objectMapper = buildObjectMapper();
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
        String content = readFile("product-pages/json/walters-eco-leather-care-kit-in-multi-4160006-915.json");
        assertThat(content).isNotEmpty();

        ObjectMapper objectMapper = buildObjectMapper();
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
        assertThat(product.variants().get(0).createdAt()).isNotNull();
        assertThat(product.variants().get(0).updatedAt()).isNotNull();

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
}
