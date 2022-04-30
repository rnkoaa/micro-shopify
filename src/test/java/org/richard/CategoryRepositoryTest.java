package org.richard;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.richard.config.DatabaseConfig;
import org.richard.config.ObjectMapperFactory;
import org.richard.frankoak.category.Collection;

class CategoryRepositoryTest {

    String url= "jdbc:sqlite:src/test/resources/db/micro-shopify.db";
    final DSLContext dslContext = new DatabaseConfig().dslContext(url);
    final ObjectMapper objectMapper = ObjectMapperFactory.buildObjectMapper();

    final CategoryRepository categoryRepository = new CategoryRepository(dslContext, objectMapper);

    @BeforeEach
    void cleanupBeforeTest() {
        assertThat(categoryRepository.deleteAll(true)).isTrue();
    }

    @Test
    void readAndSaveCategory() throws IOException {
        InputStream resourceAsStream = CategoryRepositoryTest.class.getClassLoader()
            .getResourceAsStream("data/men_category_shop_all.json");
        assertThat(resourceAsStream).isNotNull();

        Collection collection = objectMapper.readValue(resourceAsStream, Collection.class);
        assertThat(collection).isNotNull();
        System.out.println(collection);

        Category category = new CollectionCategoryMapper().map(collection);
        assertThat(category).isNotNull();
        System.out.println(category);

        var savedCategory = categoryRepository.save(category);
        assertThat(savedCategory).isNotNull();
        assertThat(savedCategory.id()).isGreaterThan(0);

        assertThat(categoryRepository.delete(savedCategory.id())).isTrue();
    }

    @Test
    void recordCanBeRead() throws IOException {
        InputStream resourceAsStream = CategoryRepositoryTest.class.getClassLoader()
            .getResourceAsStream("data/men_category_shop_all.json");
        assertThat(resourceAsStream).isNotNull();

        Collection collection = objectMapper.readValue(resourceAsStream, Collection.class);
        assertThat(collection).isNotNull();

        Category category = new CollectionCategoryMapper().map(collection);

        var savedCategory = categoryRepository.save(category);
        assertThat(savedCategory).isNotNull();
        assertThat(savedCategory.id()).isGreaterThan(0);

        Optional<Category> maybeCategory = categoryRepository.findById(savedCategory.id());
        assertThat(maybeCategory).isPresent();

        Category foundCategory = maybeCategory.get();
        assertThat(foundCategory).isEqualTo(savedCategory);

        assertThat(categoryRepository.delete(savedCategory.id())).isTrue();
    }

    @Test
    void recordsNeedCanBeCounted() throws IOException {
        InputStream resourceAsStream = CategoryRepositoryTest.class.getClassLoader()
            .getResourceAsStream("data/men_category_shop_all.json");
        assertThat(resourceAsStream).isNotNull();

        Collection collection = objectMapper.readValue(resourceAsStream, Collection.class);
        assertThat(collection).isNotNull();

        Category category = new CollectionCategoryMapper().map(collection);

        var savedCategory = categoryRepository.save(category);
        assertThat(savedCategory).isNotNull();
        assertThat(savedCategory.id()).isGreaterThan(0);

        int count = categoryRepository.count();
        assertThat(count).isEqualTo(1);

        assertThat(categoryRepository.delete(savedCategory.id())).isTrue();
    }


}