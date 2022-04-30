package org.richard;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.richard.config.DatabaseConfig;
import org.richard.config.ObjectMapperFactory;
import org.richard.frankoak.category.Collection;

class CategoryRepositoryTest {

    final DSLContext dslContext = new DatabaseConfig().dslContext();
    final ObjectMapper objectMapper = ObjectMapperFactory.buildObjectMapper();

    final CategoryRepository categoryRepository = new CategoryRepository(dslContext, objectMapper);

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

        categoryRepository.delete(savedCategory.id());
    }


}