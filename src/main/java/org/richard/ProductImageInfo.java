package org.richard;

import java.util.Set;

public record ProductImageInfo(long id, String handle, Set<String> imageUrls) {}