package org.richard.repository;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.richard.FileReader.readResourceFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.richard.assertions.MyAssertions;
import org.richard.frankoak.category.ProductResponse;
import org.richard.frankoak.converter.ProductItemResponseConverter;
import org.richard.frankoak.converter.ProductResponseMapper;
import org.richard.frankoak.product.ProductDetailResponse;
import org.richard.frankoak.product.ProductItemResponse;
import org.richard.product.Product;

class ProductRepositoryTest extends BaseRepositoryTest {

    final ProductResponseMapper productResponseMapper = new ProductResponseMapper();
    final ProductItemResponseConverter productItemResponseConverter = new ProductItemResponseConverter();
    final ProductResponseMapper summaryResponseMapper = new ProductResponseMapper();

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

        assertThat(foundProduct.images()).isNotNull().hasSize(8);
        assertThat(foundProduct.variants()).isNotNull().hasSize(8);

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

    @Test
    void updateProductAndChildren() throws IOException {
        ProductResponse productResponse = objectMapper.readValue(
            readResourceFile("data/selvedge-slim-fit-jean-in-black.json"), ProductResponse.class);
        assertThat(productResponse).isNotNull();
        Product summaryProduct = summaryResponseMapper.convert(productResponse);
        Product savedSummaryProduct = productRepository.save(summaryProduct);
        assertThat(savedSummaryProduct).isNotNull();
        assertThat(savedSummaryProduct.id()).isGreaterThan(0);

        String path = "data/1210451-003.json";
        ProductDetailResponse productDetailResponse = objectMapper.readValue(
            readResourceFile(path),
            ProductDetailResponse.class);
        assertThat(productDetailResponse).isNotNull();

        ProductItemResponse productItemResponse = productDetailResponse.product();
        assertThat(productItemResponse).isNotNull();

        Product productDetail = productItemResponseConverter.convert(productItemResponse);

        var finalProduct = savedSummaryProduct.mergeWith(productDetail);
        MyAssertions.assertThat(finalProduct)
            .isNotNull()
            .isAvailable()
            .hasHandle("1210451-003")
            .hasTitle("The Selvedge Slim Fit Jean in Black")
            .hasType("Bottoms")
            .hasVariantSize(8)
            .hasImagesSize(7)
            .hasOptionsSize(1);

        assertThat(finalProduct.htmlDescription()).isNotEmpty();

        boolean updated = productRepository.update(finalProduct);
        assertThat(updated).isTrue();

        Optional<Product> byHandle = productRepository.findById(savedSummaryProduct.id());
        assertThat(byHandle).isPresent();

        Product product = byHandle.get();
        MyAssertions.assertThat(product)
            .isNotNull()
            .isAvailable()
            .hasHandle("1210451-003")
            .hasTitle("The Selvedge Slim Fit Jean in Black")
            .hasType("Bottoms")
            .hasVariantSize(8)
            .hasImagesSize(8)
            .hasOptionsSize(1);
    }

}