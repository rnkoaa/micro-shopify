package org.richard;

import static org.microshopify.jooq.tables.Category.CATEGORY;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.richard.frankoak.infra.jooq.CategoryRecordUnMapper;

public class CategoryRepository extends JooqBaseRepository implements Repository<Category, Integer>,
    HasHandle<Category> {

    private final CategoryRecordUnMapper categoryRecordUnMapper;

    public CategoryRepository(DSLContext dsl, ObjectMapper objectMapper) {
        super(dsl, objectMapper);
        this.categoryRecordUnMapper = new CategoryRecordUnMapper(objectMapper);
    }

    @Override
    public boolean update(Category category) {
        Optional<Category> maybeCategory = findByHandle(category.url());
        return maybeCategory
            .map(foundCategory -> {
                int count = getDsl().transactionResult(configuration -> {
                        var newCategory = foundCategory.mergeWith(category);
                        return getDsl().update(CATEGORY)
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

    @Override
    public Category save(Category category) {
        return getDsl().transactionResult(configuration -> {
            try {
                Integer result = getDsl().insertInto(CATEGORY)
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

    @Override
    public List<Category> save(List<Category> categories) {
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

    @Override
    public int count() {
        return getDsl().fetchCount(CATEGORY);
    }

    @Override
    public List<Category> findAll() {
        List<Category> categories = getDsl().selectFrom(CATEGORY)
            .fetchInto(Category.class);

        return List.copyOf(categories);
    }

    @Override
    public boolean delete(Integer category) {
        int deletedCount = getDsl().delete(CATEGORY)
            .where(CATEGORY.ID.eq(category))
            .execute();
        return deletedCount == 1;
    }

    @Override
    public Optional<Category> findById(Integer categoryId) {
        Category category = getDsl().selectFrom(CATEGORY)
            .where(CATEGORY.ID.eq(categoryId))
            .fetchOneInto(Category.class);

        return Optional.ofNullable(category);
    }

    @Override
    public Optional<Category> findByHandle(String handle) {
        Category category = getDsl().selectFrom(CATEGORY)
            .where(CATEGORY.HANDLE.eq(handle))
            .fetchOneInto(Category.class);

        return Optional.ofNullable(category);
    }

    @Override
    public boolean deleteAll(boolean testing) {
        if (testing) {
            getDsl()
                .delete(CATEGORY)
                .execute();
            return true;
        }
        return false;
    }
}
