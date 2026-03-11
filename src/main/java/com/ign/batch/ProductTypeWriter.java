package com.ign.batch;

import java.util.Map;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.ign.service.AttributeCache;
import com.ign.service.ProductTypeService;
import com.kibocommerce.sdk.catalogadministration.models.ProductType;

@Component
public class ProductTypeWriter implements ItemWriter<ProductType> {

	private final ProductTypeService service;
	private final AttributeCache attributeCache;

	public ProductTypeWriter(ProductTypeService service, AttributeCache attributeCache) {
		this.service = service;
		this.attributeCache = attributeCache;
	}

	@Override
	public void write(Chunk<? extends ProductType> chunk) throws Exception {

		for (ProductType type : chunk) {
			//service.getOrCreateProductTypeId(type);

			Integer productTypeId = service.getOrCreateProductTypeId(type);

//			if (type.getOptions() != null) {
//				for (AttributeInProductType option : type.getOptions()) {
//					service.addOptionIfNotExists(productTypeId, option);
//				}
//			}
//
//			if (type.getExtras() != null) {
//				for (AttributeInProductType extra : type.getExtras()) {
//					service.addExtraIfNotExists(productTypeId, extra);
//				}
//			}
//
//			if (type.getProperties() != null) {
//				for (AttributeInProductType prop : type.getProperties()) {
//					service.addPropertyIfNotExists(productTypeId, prop);
//				}
//			}
//
//			if (type.getVariantProperties() != null) {
//				for (AttributeInProductType variant : type.getVariantProperties()) {
//					service.addVariantPropertyIfNotExists(productTypeId, variant);
//				}
//			}
		}

	}
}