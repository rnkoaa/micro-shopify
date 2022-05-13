package org.richard.product;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;

class ImageMergeTest {

    @Test
    void testMergeImage() {
        String imageSrc = "//cdn.shopify.com/s/files/1/0555/5722/6653/products/1210451-003.0205.jpg?v=1646080506";
        var imageAlt = "The Selvedge Slim Fit Jean in Black";
        var firstImage = Image.builder()
            .src(imageSrc)
            .imageSize(new ImageSize(2400, 3600))
            .build();

        var secondImage = Image.builder()
            .src(imageSrc)
            .imageSize(new ImageSize(2400, 3600))
            .alt(imageAlt)
            .build();
        var finalImage = firstImage.mergeWith(secondImage);
        assertThat(finalImage).isNotNull();
        assertThat(finalImage.src()).isEqualTo(imageSrc);
        assertThat(finalImage.alt()).isEqualTo(imageAlt);
        assertThat(finalImage.size()).isEqualTo(new ImageSize(2400, 3600));

    }
}
