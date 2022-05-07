package org.richard.repository;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.richard.ProductResponseMapper;
import org.richard.assertions.MyAssertions;
import org.richard.frankoak.category.ProductResponse;
import org.richard.product.Product;

class ProductRepositoryTest extends BaseRepositoryTest {

    final ProductResponseMapper productResponseMapper = new ProductResponseMapper();

    @Test
    void productCanBePersisted() throws IOException {
        String path = "data/selvedge-slim-fit-jean-in-black.json";
        InputStream inputStream = readFileAsStream(path);
        ProductResponse productResponse = objectMapper.readValue(inputStream, ProductResponse.class);
        assertThat(productResponse).isNotNull();

        assertThat(productResponse).isNotNull();

        Product product = productResponseMapper.convert(productResponse);
        assertThat(product).isNotNull();

        Product savedProduct = productRepository.save(product);
        MyAssertions.assertThat(savedProduct).isNotNull()
            .hasId(1)
            .hasVariantSize(product.variants().size())
            .hasTitle(product.title());
    }

    @Test
    void savedProductCanBeFound() throws IOException {
        String path = "data/selvedge-slim-fit-jean-in-black.json";
        InputStream inputStream = readFileAsStream(path);
        ProductResponse productResponse = objectMapper.readValue(inputStream, ProductResponse.class);
        assertThat(productResponse).isNotNull();
        assertThat(productResponse).isNotNull();

        Product product = productResponseMapper.convert(productResponse);
        assertThat(product).isNotNull();

        Product savedProduct = productRepository.save(product);
        MyAssertions.assertThat(savedProduct).isNotNull()
            .hasId(1);

        Optional<Product> productById = productRepository.findById(savedProduct.id());
        assertThat(productById).isPresent();

        Product foundProduct = productById.get();
        MyAssertions.assertThat(foundProduct)
            .hasSameIdAs(savedProduct)
            .hasTitle(savedProduct.title())
            .hasPrice(product.price());

        System.out.println(objectMapper.writeValueAsString(foundProduct));
    }

    @Test
    void allSavedProductCanBeFound() throws IOException {
        String path = "data/selvedge-slim-fit-jean-in-black.json";
        InputStream inputStream = readFileAsStream(path);
        ProductResponse productResponse = objectMapper.readValue(inputStream, ProductResponse.class);
        assertThat(productResponse).isNotNull();

        assertThat(productResponse).isNotNull();

        Product product = productResponseMapper.convert(productResponse);
        assertThat(product).isNotNull();

        Product savedProduct = productRepository.save(product);
        MyAssertions.assertThat(savedProduct).isNotNull()
            .hasId(1);

        List<Product> allProducts = productRepository.findAll();
        assertThat(allProducts).isNotNull().hasSize(1);

        Product foundProduct = allProducts.get(0);
        MyAssertions.assertThat(foundProduct)
            .hasTitle(productResponse.title())
            .hasPrice(productResponse.price());
    }
    @Test
    void findSavedProductByHandle() throws IOException {
        String path = "data/selvedge-slim-fit-jean-in-black.json";
        InputStream inputStream = readFileAsStream(path);
        ProductResponse productResponse = objectMapper.readValue(inputStream, ProductResponse.class);
        assertThat(productResponse).isNotNull();

        assertThat(productResponse).isNotNull();

        Product product = productResponseMapper.convert(productResponse);
        assertThat(product).isNotNull();

        Product savedProduct = productRepository.save(product);
        MyAssertions.assertThat(savedProduct).isNotNull()
            .hasId(1);

        Optional<Product> maybeProduct = productRepository.findByHandle(product.link());
        assertThat(maybeProduct).isPresent();

        Product foundProduct = maybeProduct.get();
        MyAssertions.assertThat(foundProduct)
            .hasTitle(productResponse.title())
            .hasPrice(productResponse.price());
    }

}