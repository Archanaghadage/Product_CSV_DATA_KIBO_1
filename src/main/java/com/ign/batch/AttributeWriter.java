package com.ign.batch;

import java.util.List;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ign.service.AttributeService;
import com.kibocommerce.sdk.catalogadministration.models.CatalogAdminsAttribute;

@Component
public class AttributeWriter implements ItemWriter<List<CatalogAdminsAttribute>> {

    @Autowired
    private AttributeService attributeService;

    @Override
    public void write(Chunk<? extends List<CatalogAdminsAttribute>> chunk) {

        for (List<CatalogAdminsAttribute> list : chunk) {
            for (CatalogAdminsAttribute attribute : list) {
            	//System.out.println("Attribute --> "+attribute);
                attributeService.createIfNotExists(attribute);
            }
        }
    }
}