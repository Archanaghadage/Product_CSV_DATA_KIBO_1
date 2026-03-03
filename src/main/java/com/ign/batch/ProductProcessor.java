package com.ign.batch;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.ign.dto.ProductCsvDto;
import com.ign.mapper.ProductMapper;
import com.kibocommerce.sdk.catalogadministration.models.CatalogAdminsProduct;

@Component
public class ProductProcessor
        implements ItemProcessor<ProductCsvDto, CatalogAdminsProduct> {

	private final ProductMapper mapper;

	public ProductProcessor(ProductMapper mapper) {
	    this.mapper = mapper;
	}

	@Override
	public CatalogAdminsProduct process(ProductCsvDto item) {
	    if (item.getProductCode() == null || item.getProductCode().isBlank()) {
	        return null;
	    }
	    
	    return mapper.map(item);
	}
}