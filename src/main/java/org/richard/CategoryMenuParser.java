package org.richard;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CategoryMenuParser {
    static void printCategories(Set<Category> categories, int tabs) {
        String tabChars = IntStream.range(0, tabs)
            .mapToObj(it -> "\t")
            .collect(Collectors.joining());

        int nextTabs = tabs + 1;
        for (Category category : categories) {
            System.out.println(tabChars + category.toSimpleString());
            if (category.children().size() > 0) {
                printCategories(category.children(), nextTabs);
            }
        }
    }

    static Set<Category> parseMenu() throws IOException {
        Set<Category> categories = new HashSet<>();
        String content = Files.readString(Paths.get("docs/header-nav.html"));
        Document document = Jsoup.parse(content);
        Elements elementsByClass = document.getElementsByClass("header-nav__items");
        if (elementsByClass.size() > 0) {
            Elements children = elementsByClass.get(0).children();
            if (children.size() > 0) {
                for (Element child : children) {
                    Element first = child.getElementsByClass("header-nav__item__link").first();
                    if (first != null) {
                        String link = first.attr("href");
                        String text = first.text();
                        Set<Category> childrenCategories = new HashSet<>();

                        var parentCategory = new Category(text, link, "", "", childrenCategories);

                        // find the next children
                        Element subHeaderNav = child.getElementsByClass("header-nav__subnav__items").first();
                        if (subHeaderNav != null) {
                            Elements directSubNavItems = subHeaderNav.getElementsByClass("header-nav__subnav__item");
                            for (Element directSubNavItem : directSubNavItems) {
                                Element childTitleElement = directSubNavItem.getElementsByClass(
                                        "header-nav__subnav__item__title")
                                    .first();
                                Category childCategory = null;
                                if (childTitleElement != null) {
                                    childCategory = new Category(childTitleElement.text(), "",
                                        parentCategory.parentName(), parentCategory.url(), Set.of());
                                    childrenCategories.add(childCategory);
                                }

                                Element directSubNavItemLinks = directSubNavItem.getElementsByClass(
                                    "header-nav__subnav__item__links").first();
                                if (directSubNavItemLinks != null) {
                                    Set<Category> grandChildren = new HashSet<>();
                                    assert childCategory != null;

                                    childCategory = childCategory.withChildren(grandChildren);
                                    for (Element linkElement : directSubNavItemLinks.getElementsByTag("a")) {
                                        String subNavText = linkElement.text();
                                        String subNavHref = linkElement.attr("href");

                                        Category grandChild = new Category(subNavText, subNavHref,
                                            childCategory.parentName(),
                                            childCategory.url(), Set.of());
                                        grandChildren.add(grandChild);
                                    }
                                    parentCategory = parentCategory.addCategory(childCategory);
                                }
                            }
                        }
                        categories.add(parentCategory);
                    }
                }
            }
        }
        return categories;
    }

}
