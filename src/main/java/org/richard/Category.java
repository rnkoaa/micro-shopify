package org.richard;

import java.util.HashSet;
import java.util.Set;

public record Category(
    int id,
    String name,
    String url,
    String parentName,
    String parentUrl,
    Set<Category> children
) {

    public Category(String name,
        String url,
        String parentName,
        String parentUrl,
        Set<Category> children) {
        this(0, name, url, parentName, parentUrl, children);
    }

    public String toSimpleString() {
        if (url == null || url.isEmpty()) {
            return "Category[name=" + name + "]";
        }
        return "Category[name=" + name + ", " + "url=" + url + "]";
    }

    public Category withChildren(Set<Category> grandChildren) {
        return new Category(name, url, parentName, parentUrl, grandChildren);
    }

    public Category withId(int id) {
        return new Category(id, name, url, parentName, parentUrl, children);
    }

    public Category addCategory(Category childCategory) {
        var copy = new HashSet<>(children);
        copy.add(childCategory);
        return withChildren(Set.copyOf(copy));
    }
}
