package com.ign.service;

import org.springframework.stereotype.Service;

import com.ign.config.KiboConfig;
import com.kibocommerce.sdk.catalogadministration.api.ProductTypesApi;
import com.kibocommerce.sdk.catalogadministration.models.ProductType;
import com.kibocommerce.sdk.catalogadministration.models.ProductTypeCollection;
import com.kibocommerce.sdk.common.ApiException;

@Service
public class ProductTypeService {

	private final ProductTypesApi api;

	public ProductTypeService(KiboConfig kiboConfig) {
		this.api = ProductTypesApi.builder().withConfig(kiboConfig.getConfiguration()).build();
	}

	public ProductType addProductType(ProductType productType) throws ApiException {
		return api.addProductType(productType);
	}

	public ProductType getProductTypeById(Integer productTypeId) throws ApiException {
		return api.getProductType(productTypeId);
	}

	public ProductTypeCollection getAllProductTypes(Integer startIndex, Integer pageSize, String sortBy, String filter,
			String responseGroups) throws ApiException {
		return api.getProductTypes(startIndex, pageSize, sortBy, filter, responseGroups);
	}

	public ProductType getProductTypeByName(String name) throws ApiException {
		String filter = "name eq '" + name + "'";
		ProductTypeCollection collection = getAllProductTypes(0, 100, null, filter, null);
		if (collection != null && collection.getItems() != null && !collection.getItems().isEmpty()) {
			return collection.getItems().get(0);
		}
		return null;
	}
}