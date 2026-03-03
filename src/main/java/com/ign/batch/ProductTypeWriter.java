package com.ign.batch;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.ign.service.ProductTypeService;
import com.kibocommerce.sdk.catalogadministration.models.ProductType;

@Component
public class ProductTypeWriter 
        implements ItemWriter<ProductType> {

    private final ProductTypeService service;
    
    public ProductTypeWriter(ProductTypeService service) {
        this.service = service;
        
    }

    @Override
    public void write(Chunk<? extends ProductType> chunk)
            throws Exception {

        for (ProductType type : chunk) {
            service.getOrCreateProductTypeId(type);
        }
    }
}