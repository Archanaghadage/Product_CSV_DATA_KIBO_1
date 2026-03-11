package com.ign.mapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ign.dto.ProductCsvDto;
import com.kibocommerce.sdk.catalogadministration.models.ProductType;
import com.kibocommerce.sdk.catalogadministration.models.AttributeInProductType;

@Component
public class ProductTypeMapper {

	private static final String DEFAULT_NAMESPACE = "tenant";

	public ProductType map(ProductCsvDto dto) {

		ProductType productType = new ProductType();

		// Name
		productType.setName(dto.getProductTypeName());

		// Supported Usage Types
		if (dto.getSupportedUsageTypes() != null && !dto.getSupportedUsageTypes().isBlank()) {
			List<String> usages = Arrays.stream(dto.getSupportedUsageTypes().split("\\|")).map(String::trim)
					.collect(Collectors.toList());
			productType.setProductUsages(usages);
		}

//		// Options
//		productType.setOptions(buildAttributeList(dto.getOptions()));
//
//		// Extras
//		productType.setExtras(buildAttributeList(dto.getExtras()));
//
//		// Properties
//		productType.setProperties(buildAttributeList(dto.getProperties()));
//
//		// Variant Properties
//		productType.setVariantProperties(buildAttributeList(dto.getVariantProperties()));

		return productType;
	}

	private List<AttributeInProductType> buildAttributeList(String columnValue) {

		if (columnValue == null || columnValue.isBlank()) {
			return null;
		}

		String[] attributeCodes = columnValue.split("\\|");

		List<AttributeInProductType> list = new ArrayList<>();
		int displayOrder = 1;

		for (String code : attributeCodes) {

			if (code == null || code.isBlank()) {
				continue;
			}

			AttributeInProductType attribute = new AttributeInProductType();
			attribute.setAttributeFQN(DEFAULT_NAMESPACE + "~" + code.trim());
			attribute.setOrder(displayOrder++);
			attribute.setIsRequiredByAdmin(true);

			list.add(attribute);
		}

		return list.isEmpty() ? null : list;
	}
}