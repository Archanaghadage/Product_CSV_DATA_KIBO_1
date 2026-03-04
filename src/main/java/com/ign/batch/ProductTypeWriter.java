package com.ign.batch;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.ign.service.ProductTypeService;
import com.kibocommerce.sdk.catalogadministration.models.ProductType;
import com.kibocommerce.sdk.catalogadministration.models.AttributeInProductType;

@Component
public class ProductTypeWriter implements ItemWriter<ProductType> {

	private final ProductTypeService service;

	public ProductTypeWriter(ProductTypeService service) {
		this.service = service;
	}

	@Override
	public void write(Chunk<? extends ProductType> chunk) throws Exception {

		for (ProductType type : chunk) {

			// 1️ Create or get ProductType ID
			Integer productTypeId = service.getOrCreateProductTypeId(type);
			System.out.println("Type==>" + type);

			// OPTIONS
			if (type.getOptions() != null) {
				for (AttributeInProductType option : type.getOptions()) {
					service.addOptionIfNotExists(productTypeId, option);
				}
			}

			// EXTRAS
			if (type.getExtras() != null) {
				for (AttributeInProductType extra : type.getExtras()) {
					service.addExtraIfNotExists(productTypeId, extra);
				}
			}

			// PROPERTIES
			if (type.getProperties() != null) {
				for (AttributeInProductType prop : type.getProperties()) {
					service.addPropertyIfNotExists(productTypeId, prop);
				}
			}

			// VARIANT PROPERTIES
			if (type.getVariantProperties() != null) {
				for (AttributeInProductType var : type.getVariantProperties()) {
					service.addVariantPropertyIfNotExists(productTypeId, var);
				}
			}

		}
	}
}