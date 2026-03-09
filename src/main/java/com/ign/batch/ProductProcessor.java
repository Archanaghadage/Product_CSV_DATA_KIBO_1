package com.ign.batch;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.ign.dto.ProductBatchWrapper;
import com.ign.dto.ProductCsvDto;
import com.ign.mapper.ProductMapper;
import com.kibocommerce.sdk.catalogadministration.models.CatalogAdminsProduct;

@Component
public class ProductProcessor
        implements ItemProcessor<ProductCsvDto, ProductBatchWrapper> {

    private final ProductMapper mapper;

    public ProductProcessor(ProductMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public ProductBatchWrapper process(ProductCsvDto item) {

        if (item.getProductCode() == null || item.getProductCode().isBlank()) {
            return null;
        }

        CatalogAdminsProduct product = mapper.map(item);

        String operation = item.getOperation();
        if (operation != null) {
            operation = operation.trim().toUpperCase();
        }

        return new ProductBatchWrapper(operation, product);
    }
}