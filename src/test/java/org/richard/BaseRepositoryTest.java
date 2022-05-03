package org.richard;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jooq.DSLContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.richard.config.DatabaseConfig;
import org.richard.config.ObjectMapperFactory;
import org.richard.repository.CategoryRepository;

public abstract class BaseRepositoryTest {

    String url = "jdbc:sqlite:src/test/resources/db/micro-shopify.db";
    final DSLContext dslContext = new DatabaseConfig().dslContext(url);
    final ObjectMapper objectMapper = ObjectMapperFactory.buildObjectMapper();

    final CategoryRepository categoryRepository = new CategoryRepository(dslContext, objectMapper);

    @BeforeEach
    void cleanupBeforeTest() {
        assertThat(categoryRepository.deleteAll(true)).isTrue();
    }

    @AfterEach
    void cleanupAfterTest() {
        assertThat(categoryRepository.deleteAll(true)).isTrue();
    }
}
