package com.ign.batch;

import java.util.List;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.ign.service.AttributeCache;
import com.ign.service.AttributeService;
import com.kibocommerce.sdk.catalogadministration.models.CatalogAdminsAttribute;

@Component
public class AttributeWriter implements ItemWriter<List<CatalogAdminsAttribute>> {

	private final AttributeService attributeService;
	private final AttributeCache attributeCache;

	public AttributeWriter(AttributeService attributeService, AttributeCache attributeCache) {
		this.attributeService = attributeService;
		this.attributeCache = attributeCache;
	}

	@Override
	public void write(Chunk<? extends List<CatalogAdminsAttribute>> chunk) {

		for (List<CatalogAdminsAttribute> list : chunk) {
			for (CatalogAdminsAttribute attribute : list) {
				// System.out.println("Attribute --> "+attribute);
				attributeService.createIfNotExists(attribute);
				attributeCache.put(attribute);

			}
		}
	}
}