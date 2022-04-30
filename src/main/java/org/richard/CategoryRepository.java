package org.richard;

import static org.microshopify.jooq.tables.Category.CATEGORY;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import org.jooq.DSLContext;
import org.richard.frankoak.infra.jooq.CategoryRecordUnMapper;

public class CategoryRepository {

    private final DSLContext dsl;
    private final CategoryRecordUnMapper categoryRecordUnMapper;

    public CategoryRepository(DSLContext dsl, ObjectMapper objectMapper) {
        this.dsl = dsl;
        this.categoryRecordUnMapper = new CategoryRecordUnMapper(objectMapper);
    }

    Category save(Category category) {
        return dsl.transactionResult(configuration -> {
            Integer result = dsl.insertInto(CATEGORY)
                .set(categoryRecordUnMapper.unmap(category))
                .returningResult(CATEGORY.ID)
                .fetchOneInto(Integer.class);

            // insert all its children with category as

            if (result != null) {
                return new Category.Builder(category).id(result).build();
            }
            return category;
        });
    }

    List<Category> save(List<Category> categories) {
        return categories.stream().map(this::save).toList();
    }

    int count() {
        return dsl.fetchCount(CATEGORY);
    }

    List<Category> findAll() {
        List<Category> categories = dsl.selectFrom(CATEGORY)
            .fetchInto(Category.class);

        return List.copyOf(categories);
    }

    boolean delete(int category) {
        int deletedCount = dsl.delete(CATEGORY)
            .where(CATEGORY.ID.eq(category))
            .execute();
        return deletedCount == 1;
    }

    public Optional<Category> findById(int categoryId) {
        Category category = dsl.selectFrom(CATEGORY)
            .where(CATEGORY.ID.eq(categoryId))
            .fetchOneInto(Category.class);

        return Optional.ofNullable(category);
    }

    public boolean deleteAll(boolean testing) {
        if (testing) {
            dsl.delete(CATEGORY)
                .execute();
            return true;
        }
        return false;
    }
}