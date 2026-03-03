package com.ign.batch;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.ign.service.AttributeService;
import com.kibocommerce.sdk.catalogadministration.models.CatalogAdminsAttribute;

@Component
public class AttributeWriter implements ItemWriter<CatalogAdminsAttribute> {

	private final AttributeService service;
	
	public AttributeWriter(AttributeService service){
		this.service=service;
	}
	
	@Override
	public void write(Chunk<? extends CatalogAdminsAttribute> chunk) throws Exception {
		
		for(CatalogAdminsAttribute attribute : chunk) {
			service.createIfNotExists(attribute);
		}
	}


}
