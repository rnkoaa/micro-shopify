package org.richard;

import static org.microshopify.jooq.tables.Category.CATEGORY;
import static org.richard.Strings.isNotNullOrEmpty;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.jooq.DSLContext;
import org.jooq.UpdateSetFirstStep;
import org.jooq.UpdateSetMoreStep;
import org.jooq.exception.DataAccessException;
import org.microshopify.jooq.tables.records.CategoryRecord;
import org.richard.frankoak.infra.jooq.CategoryRecordUnMapper;

public class CategoryRepository {

    private final DSLContext dsl;
    private final CategoryRecordUnMapper categoryRecordUnMapper;

    public CategoryRepository(DSLContext dsl, ObjectMapper objectMapper) {
        this.dsl = dsl;
        this.categoryRecordUnMapper = new CategoryRecordUnMapper(objectMapper);
    }

    boolean update(Category category) {
        Optional<Category> maybeCategory = findByHandle(category.url());
        return maybeCategory
            .map(foundCategory -> {
                int count = dsl.transactionResult(configuration -> {
                        var newCategory = foundCategory.mergeWith(category);
                        return dsl.update(CATEGORY)
                            .set(CATEGORY.UPDATED_AT, Instant.now().toString())
                            .set(CATEGORY.HANDLE, newCategory.url())
                            .set(CATEGORY.DESCRIPTION, newCategory.description())
                            .set(CATEGORY.POSITION, newCategory.position())
                            .set(CATEGORY.DEFAULT_FILTER_GROUPS,
                                categoryRecordUnMapper.parseJSON(newCategory.defaultFilterGroups()))
                            .set(CATEGORY.SORT_OPTIONS,
                                categoryRecordUnMapper.parseJSON(newCategory.sortOptions())
                            )
                            .set(CATEGORY.HERO,
                                categoryRecordUnMapper.parseJSON(newCategory.hero())
                            )
                            .where(CATEGORY.ID.eq(foundCategory.id()))
                            .execute();
                    }
                );
                return count > 1;
            })
            .orElse(false);
    }

    Category save(Category category) {
        return dsl.transactionResult(configuration -> {
            try {
                Integer result = dsl.insertInto(CATEGORY)
                    .set(categoryRecordUnMapper.unmap(category))
                    .returningResult(CATEGORY.ID)
                    .fetchOneInto(Integer.class);

                // insert all its children with category as

                if (result != null) {
                    return category.withId(result);
                }
                return category;
            } catch (DataAccessException ex) {
                if (ex.getMessage().contains("SQLITE_CONSTRAINT_UNIQUE")) {
                    System.out.println("Unique Constraint: " + category);
                }
            }
            return category;
        });
    }

    List<Category> save(List<Category> categories) {
        return categories.stream().map(this::save).toList();
    }

    boolean saveTree(Set<Category> categories) {
        return categories.stream()
            .map(category -> {
                var savedCategory = save(category);
                var toSave = category.withId(savedCategory.id());
                return saveChildren(toSave);
            })
            .allMatch(it -> it.id() > 0);
    }

    private Category saveChildren(Category category) {
        if (category.children() != null && category.children().size() > 0) {
            var savedCategories = category.children()
                .stream()
                .map(cat -> cat.withParent(category))
                .map(this::ensureUrlExists)
                .map(cat -> {
                    Category savedCategory = this.save(cat);
                    if (cat.children() == null || cat.children().isEmpty()) {
                        return savedCategory;
                    }
                    return saveChildren(savedCategory);
                })
                .collect(Collectors.toSet());
            if (savedCategories.size() == category.children().size()) {
                return category;
            }
        }
        return category;
    }

    private Category ensureUrlExists(Category cat) {
        if (cat.url() == null || cat.url().isEmpty()) {
            String name = cat.name().replaceAll("(\\s+&+\\s)+", "-").toLowerCase();
            if (cat.parent() != null && cat.parent().url() != null) {
                return cat.withUrl("%s/%s".formatted(cat.parent().url(), name));
            }
            return cat.withUrl("/pages/" + name);
        }

        return cat;
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

    public Optional<Category> findByHandle(String handle) {
        Category category = dsl.selectFrom(CATEGORY)
            .where(CATEGORY.HANDLE.eq(handle))
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
