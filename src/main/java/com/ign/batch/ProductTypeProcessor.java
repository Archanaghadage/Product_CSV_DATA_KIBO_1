package com.ign.batch;

import java.util.HashSet;
import java.util.Set;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.ign.dto.ProductCsvDto;
import com.ign.mapper.ProductTypeMapper;
import com.kibocommerce.sdk.catalogadministration.models.ProductType;

@Component
public class ProductTypeProcessor
        implements ItemProcessor<ProductCsvDto, ProductType> {

    private final ProductTypeMapper mapper;

    // Prevent duplicate ProductTypes in same batch run
    private final Set<String> processedTypes = new HashSet<>();

    public ProductTypeProcessor(ProductTypeMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public ProductType process(ProductCsvDto item) {

        if (item == null) {
            return null;
        }

        String typeName = item.getProductTypeName();

        if (typeName == null || typeName.isBlank()) {
            return null;
        }

        typeName = typeName.trim();

        // Skip duplicates inside same CSV
        if (processedTypes.contains(typeName)) {
            return null;
        }

        processedTypes.add(typeName);

        return mapper.map(item);
    }
}