package com.ign.batch;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.ign.dto.ProductCsvDto;
import com.ign.mapper.AttributeMapper;
import com.kibocommerce.sdk.catalogadministration.models.CatalogAdminsAttribute;

@Component
public class AttributeProcessor implements ItemProcessor<ProductCsvDto, CatalogAdminsAttribute> {

	private final AttributeMapper mapper;

	public AttributeProcessor(AttributeMapper mapper) {
		this.mapper = mapper;
	}

	@Override
	public CatalogAdminsAttribute process(ProductCsvDto item) throws Exception {
		if (item.getAttributeCode() == null || item.getAttributeCode().isEmpty())
			return null;
		return mapper.map(item);
	}

}
