package org.richard.frankoak.category;

import java.util.Map;

public record CollectionHero(boolean show,
                             String image,
                             String alt,
                             String color,
                             String title,
                             String detail
) {

    public Map<String, String> decompose() {
        return Map.of(
            "show", String.valueOf(show),
            "alt", alt,
            "color", color,
            "title", title,
            "detail", detail
        );
    }
}
