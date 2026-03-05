package com.ign.batch;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.ign.dto.ProductCsvDto;
import com.ign.mapper.ProductTypeMapper;
import com.kibocommerce.sdk.catalogadministration.models.ProductType;

@Component
public class ProductTypeProcessor
        implements ItemProcessor<ProductCsvDto, ProductType> {

    private final ProductTypeMapper mapper;

    public ProductTypeProcessor(ProductTypeMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public ProductType process(ProductCsvDto item) {

    	  if (item.getProductTypeName() == null ||
    	            item.getProductTypeName().trim().isEmpty()) {
    	            return null;
    	        }
    	 
    	        return mapper.map(item);
    	    
    }
}