package org.richard.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.richard.frankoak.category.CollectionResponse;
import org.richard.product.Category;
import org.richard.product.Product;

class CategoryProductRepositoryTest extends BaseRepositoryTest {

    @Test
    void saveFullCollection() throws IOException {
        InputStream resourceAsStream = readFileAsStream("data/men-outerwear-collection-response.json");
        assertThat(resourceAsStream).isNotNull();

        CollectionResponse response = objectMapper.readValue(resourceAsStream, CollectionResponse.class);
        assertThat(response).isNotNull();
        assertThat(response.collection()).isNotNull();
        assertThat(response.products()).isNotNull().hasSize(18);

        Category category = collectionCategoryMapper.convert(response.collection());
        List<Product> products = response.products().stream()
            .map(productResponseMapper::convert)
            .toList();

        assertThat(category).isNotNull();
        assertThat(products).hasSize(response.products().size());

        var savedCategory = categoryProductRepository.save(category, products);
        assertThat(savedCategory).isNotNull();
    }


}