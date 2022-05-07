package org.richard.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import org.jooq.DSLContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.richard.config.DatabaseConfig;
import org.richard.config.ObjectMapperFactory;

public abstract class BaseRepositoryTest {

    String url = "jdbc:sqlite:src/test/resources/db/micro-shopify.db";
    final DSLContext dslContext = new DatabaseConfig().dslContext(url);
    public final ObjectMapper objectMapper = ObjectMapperFactory.buildObjectMapper();

    public final CategoryRepository categoryRepository = new CategoryRepository(dslContext, objectMapper);
    public final ProductRepository productRepository = new ProductRepository(dslContext, objectMapper);

    InputStream readFileAsStream(String path) {
        return BaseRepositoryTest.class.getClassLoader()
            .getResourceAsStream(path);
    }

    @BeforeEach
    void cleanupBeforeTest() {
        categoryRepository.deleteAll(true);
        productRepository.deleteAll(true);
    }

    @AfterEach
    void cleanupAfterTest() {
        categoryRepository.deleteAll(true);
        productRepository.deleteAll(true);
    }
}
