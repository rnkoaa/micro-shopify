package org.richard.product;

public record ImageSize(int width, int height) {

    public boolean valid() {
        return width > 0 && height > 0;
    }
}
