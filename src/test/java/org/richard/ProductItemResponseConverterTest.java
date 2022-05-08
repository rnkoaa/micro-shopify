package org.richard;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.richard.config.ObjectMapperFactory.buildObjectMapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Test;
import org.richard.assertions.MyAssertions;
import org.richard.frankoak.product.ProductDetailResponse;
import org.richard.frankoak.product.ProductItemResponse;
import org.richard.product.Product;

class ProductItemResponseConverterTest {

    private static final ObjectMapper objectMapper = buildObjectMapper();

    private final ProductItemResponseConverter productResponseMapper = new ProductItemResponseConverter();

    @Test
    void canDeserializeProductResponse() throws IOException {
        String path = "data/1210451-003.json";
        InputStream inputStream = FileReader.readResourceFile(path);
        ProductDetailResponse productDetailResponse = objectMapper.readValue(inputStream, ProductDetailResponse.class);
        assertThat(productDetailResponse).isNotNull();

        ProductItemResponse productItemResponse = productDetailResponse.product();
        assertThat(productItemResponse).isNotNull();

        MyAssertions.assertThat(productItemResponse)
            .hasHandle("1210451-003")
            .hasTitle("The Selvedge Slim Fit Jean in Black")
            .hasType("Bottoms")
            .hasVariantSize(8)
            .hasImagesSize(7)
            .hasOptionsSize(7)
//            .hasVendor("Frank and Oak")
        ;
//
        Product product = productResponseMapper.convert(productItemResponse);
        MyAssertions.assertThat(product)
            .isNotNull()
            .hasLink(productItemResponse.handle())
            .hasType(productItemResponse.productType())
            .hasTitle(productItemResponse.title())
            .hasHtmlDescription(productItemResponse.bodyHtml())
            .hasOptionsSize(productItemResponse.options().size())
            .hasImagesSize(productItemResponse.images().size())
            .hasVariantSize(productItemResponse.variants().size());

//        List<Variant> variants = product.variants();
    }

}