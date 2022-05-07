package org.richard.frankoak.fs;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.richard.product.Category;
import org.richard.product.Category.Builder;
import org.richard.frankoak.category.Collection;
import org.richard.frankoak.category.CollectionResponse;

public class CategoryMenuParser {

    static void printCategories(Set<Category> categories, int tabs) {
        String tabChars = IntStream.range(0, tabs)
            .mapToObj(it -> "\t")
            .collect(Collectors.joining());

        int nextTabs = tabs + 1;
        for (Category category : categories) {
            System.out.println(tabChars + category.toSimpleString());
            if (category.children() != null && category.children().size() > 0) {
                printCategories(category.children(), nextTabs);
            }
        }
    }

    public static Set<Category> parseMenu() throws IOException {
        Set<Category> categories = new HashSet<>();
        String content = Files.readString(Paths.get("data/header-nav.html"));
        Document document = Jsoup.parse(content);
        Elements elementsByClass = document.getElementsByClass("header-nav__items");
        if (elementsByClass.size() > 0) {
            Elements children = elementsByClass.get(0).children();
            if (children.size() > 0) {
                for (Element child : children) {
                    Set<Category> childrenCategories = new HashSet<>();
                    var parentCategory = buildDirectChild(child);
                    if (parentCategory != null) {
//                        // find the next children
                        Element subHeaderNav = child.getElementsByClass("header-nav__subnav__items").first();
                        if (subHeaderNav != null) {
                            Elements directSubNavItems = subHeaderNav.getElementsByClass("header-nav__subnav__item");
                            for (Element directSubNavItem : directSubNavItems) {
                                Element childTitleElement = directSubNavItem.getElementsByClass(
                                        "header-nav__subnav__item__title")
                                    .first();
                                Category childCategory = null;
                                if (childTitleElement != null) {
                                    childCategory = Category.builder()
                                        .name(childTitleElement.text())
                                        .parent(parentCategory)
                                        .build();
                                }

                                Element directSubNavItemLinks = directSubNavItem.getElementsByClass(
                                    "header-nav__subnav__item__links").first();
                                if (directSubNavItemLinks != null) {
                                    Set<Category> grandChildren = new HashSet<>();
                                    assert childCategory != null;
//
                                    for (Element linkElement : directSubNavItemLinks.getElementsByTag("a")) {
                                        String subNavText = linkElement.text();
                                        String subNavHref = linkElement.attr("href");

                                        Category grandChild = Category.builder()
                                            .name(subNavText)
                                            .url(subNavHref)
                                            .parent(childCategory)
                                            .build();
                                        grandChildren.add(grandChild);
                                    }
                                    childCategory = new Builder(childCategory).children(grandChildren).build();
                                }
                                childrenCategories.add(childCategory);
                            }
                            parentCategory = new Category.Builder(parentCategory)
                                .children(childrenCategories).build();
                            categories.add(parentCategory);
                        }
                    }

                }
            }
        }
        return categories;
    }

    public static Set<CollectionResponse> readCategoryFiles(ObjectMapper objectMapper) {
        String path = "category-pages/json";
        File file = new File(path);
        File[] files = file.listFiles();
        if (files != null && files.length > 0) {
            return Arrays.stream(files)
                .map(File::getPath)
                .filter(fPath -> fPath.endsWith(".json"))
                .map(f -> {
                    try {
                        String fileContent = Files.readString(Paths.get(f));
                        return objectMapper.readValue(fileContent, CollectionResponse.class);
                    } catch (IOException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        }

        return Set.of();
    }

    public static Set<Collection> getAllCollections(ObjectMapper objectMapper) {
        return readCategoryFiles(objectMapper)
            .stream()
            .map(CollectionResponse::collection)
            .collect(Collectors.toSet());
    }

    static void flatten(List<Category> seed, Category parent, Set<Category> categories) {
        categories.forEach(category -> {
            if (parent != null) {
                seed.add(
                    Category.builder()
                        .name(category.name())
                        .url(category.url())
                        .parent(parent)
                        .build()
                );
            } else {
                seed.add(
                    Category.builder()
                        .name(category.name())
                        .url(category.url())
                        .parent(category.parent())
                        .build()
                );
            }
            if (category.children().size() > 0) {
                flatten(seed, category, category.children());
            }
        });
    }

    static Category buildDirectChild(Element child) {
        Element first = child.getElementsByClass("header-nav__item__link").first();
        if (first != null) {
            String link = first.attr("href");
            String text = first.text();

            return Category.builder()
                .name(text)
                .url(link)
                .build();
        }
        return null;
    }

}
