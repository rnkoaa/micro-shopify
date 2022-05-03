package org.richard;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;
import org.richard.frankoak.category.Collection;

class CategoryMenuParserTest extends BaseRepositoryTest {

    @Test
    void readAndPrintCategoryMenu() throws IOException {
        Set<Category> categories = CategoryMenuParser.parseMenu();
        assertThat(categories.size()).isGreaterThan(0);

        // saves all categories and its children
        boolean saved = categoryRepository.saveTree(categories);

        assertThat(saved).isTrue();
    }

    @Test
    void findAllCategories() throws IOException {
        Set<Category> categories = CategoryMenuParser.parseMenu();
        assertThat(categories.size()).isGreaterThan(0);

        // saves all categories and its children
        boolean saved = categoryRepository.saveTree(categories);
        assertThat(saved).isTrue();

        List<Category> allCategories = categoryRepository.findAll();
        assertThat(allCategories).isNotNull()
            .hasSize(54);

        // ensure that all are saved and have valid db ids
        assertThat(allCategories).is(
            new Condition<>(categories1 -> categories1.stream().allMatch(c -> c.id() > 0), "valid id"));
    }

    @Test
    void readAndTestCollections() throws IOException {
        Set<Category> categories = CategoryMenuParser.parseMenu();
        assertThat(categories.size()).isGreaterThan(0);

        // saves all categories and its children
        boolean saved = categoryRepository.saveTree(categories);
        assertThat(saved).isTrue();
//
        Set<Collection> allCollections = CategoryMenuParser.getAllCollections(objectMapper);
        assertThat(allCollections).isNotNull().hasSize(37);
//
        var collectionCategoryMapper = new CollectionCategoryMapper();
        List<Boolean> updates = allCollections.stream()
            .map(collectionCategoryMapper::convert)
            .map(categoryRepository::update)
            .collect(Collectors.toList());
//
        assertThat(updates).hasSize(allCollections.size())
            .allMatch(it -> it);
    }

}