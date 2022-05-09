package org.richard.product;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Test;
import org.richard.frankoak.converter.VariantItemDetailResponseConverter;
import org.richard.frankoak.converter.VariantItemSummaryResponseConverter;
import org.richard.config.ObjectMapperFactory;
import org.richard.frankoak.category.ProductVariant;
import org.richard.frankoak.product.VariantItem;

class VariantMergeTest {

    final ObjectMapper objectMapper = ObjectMapperFactory.buildObjectMapper();
    final VariantItemDetailResponseConverter variantItemDetailResponseConverter = new VariantItemDetailResponseConverter();
    final VariantItemSummaryResponseConverter itemSummaryResponseConverter = new VariantItemSummaryResponseConverter();

    @Test
    void twoVariantsCanBeMerged() throws IOException {
        ProductVariant productVariant = readVariant("data/variants/summary_variant.json", ProductVariant.class);
        VariantItem variantItem = readVariant("data/variants/detail_variant.json", VariantItem.class);

        assertThat(productVariant).isNotNull();
        assertThat(variantItem).isNotNull();

        Variant variant = variantItemDetailResponseConverter.convert(variantItem);
        assertThat(variant).isNotNull();
        assertThat(variant.title()).isNotEmpty().isEqualTo("XS");
        assertThat(variant.inventory()).isNotNull();
        assertThat(variant.inventory().quantity()).isEqualTo(0);
        assertThat(variant.barcode()).isNotEmpty().isEqualTo("1110352-2ED-XS");
        assertThat(variant.sku()).isNotEmpty().isEqualTo("1110352-2ED-XS");
        assertThat(variant.handle()).isNotEmpty().isEqualTo("1110352-2ED-XS");
        assertThat(variant.compareAtPrice()).isNotEmpty().isEqualTo("69.50");
        assertThat(variant.price()).isNotEmpty().isEqualTo("79.50");

        Variant summaryVariant = itemSummaryResponseConverter.convert(productVariant);
        assertThat(summaryVariant).isNotNull();
        assertThat(summaryVariant.sku()).isNullOrEmpty();
        assertThat(summaryVariant.barcode()).isNullOrEmpty();
        assertThat(summaryVariant.inventory()).isNotNull();
        assertThat(summaryVariant.inventory().quantity()).isEqualTo(10);

        // when
        var newVariant = summaryVariant.mergeWith(variant);
        assertThat(newVariant.title()).isEqualTo("XS");
        assertThat(newVariant.inventory().quantity()).isEqualTo(10);
        assertThat(newVariant.barcode()).isNotEmpty().isEqualTo("1110352-2ED-XS");
        assertThat(newVariant.sku()).isNotEmpty().isEqualTo("1110352-2ED-XS");
        assertThat(newVariant.handle()).isNotEmpty().isEqualTo("1110352-2ED-XS");
        assertThat(newVariant.compareAtPrice()).isNotEmpty().isEqualTo("69.50");
        assertThat(newVariant.price()).isNotEmpty().isEqualTo("79.50");
    }

    <T> T readVariant(String path, Class<T> clzz) throws IOException {
        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(path);
        return objectMapper.readValue(resourceAsStream, clzz);
    }

}