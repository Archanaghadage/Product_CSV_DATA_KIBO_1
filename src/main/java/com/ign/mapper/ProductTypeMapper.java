package com.ign.mapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ign.dto.ProductCsvDto;
import com.kibocommerce.sdk.catalogadministration.models.ProductType;
import com.kibocommerce.sdk.catalogadministration.models.AttributeInProductType;

@Component
public class ProductTypeMapper {

	private static final String DEFAULT_NAMESPACE = "tenant";

	public ProductType map(ProductCsvDto dto) {

		ProductType productType = new ProductType();

		productType.setName(dto.getProductTypeName());
		// Supported Usage Types
		if (dto.getSupportedUsageTypes() != null && !dto.getSupportedUsageTypes().isBlank()) {
		List<String> usages = Arrays.asList(dto.getSupportedUsageTypes().split("|"));
		productType.setProductUsages(usages);
		}

		// OPTIONS
		if (dto.getOptions() != null && !dto.getOptions().isBlank()) {
			productType.setOptions(buildAttributeList(dto.getOptions()));
		}

		// EXTRAS
		if (dto.getExtras() != null && !dto.getExtras().isBlank()) {
			productType.setExtras(buildAttributeList(dto.getExtras()));
		}

		// PROPERTIES
		if (dto.getProperties() != null && !dto.getProperties().isBlank()) {
			productType.setProperties(buildAttributeList(dto.getProperties()));
		}

		// VARIANT PROPERTIES
		if (dto.getVariantProperties() != null && !dto.getVariantProperties().isBlank()) {
			productType.setVariantProperties(buildAttributeList(dto.getVariantProperties()));
		}

		return productType;
	}

	
	// Convert CSV attribute codes into AttributeInProductType
	private List<AttributeInProductType> buildAttributeList(String columnValue) {
		String[] attributeCodes = columnValue.split("|");
		List<AttributeInProductType> list = new ArrayList<>();
		int displayOrder = 1;
		for (String attributeCode : attributeCodes) {
			if (attributeCode == null || attributeCode.isBlank()) {
				continue;
			}

			AttributeInProductType attribute = new AttributeInProductType();
			attribute.setAttributeFQN(DEFAULT_NAMESPACE + "~" + attributeCode.trim());
			attribute.setOrder(displayOrder++);
			list.add(attribute);
		}

		return list;
	}
}