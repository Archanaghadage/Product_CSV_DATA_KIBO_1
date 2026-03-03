package com.ign.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.ign.config.KiboConfig;
import com.kibocommerce.sdk.catalogadministration.api.ProductTypesApi;
import com.kibocommerce.sdk.catalogadministration.models.ProductType;
import com.kibocommerce.sdk.catalogadministration.models.ProductTypeCollection;
import com.kibocommerce.sdk.common.ApiException;

@Service
public class ProductTypeService {

	private final ProductTypesApi api;

	// ✅ Thread-safe cache
	private final Map<String, Integer> productTypeCache = new ConcurrentHashMap<>();

	public ProductTypeService(KiboConfig kiboConfig) {
		this.api = ProductTypesApi.builder().withConfig(kiboConfig.getConfiguration()).build();
	}

	// =========================================================
	// ✅ MAIN METHOD (String version)
	// =========================================================
	public Integer getOrCreateProductTypeId(String typeName) {

		if (typeName == null || typeName.isBlank()) {
			throw new RuntimeException("ProductType name is missing");
		}

		typeName = typeName.trim();

		// 1️⃣ Check cache
		Integer cachedId = productTypeCache.get(typeName);
		if (cachedId != null) {
			return cachedId;
		}

		try {
			// 2️⃣ Check in Kibo
			ProductType existing = getProductTypeByName(typeName);

			if (existing != null && existing.getId() != null) {
				Integer id = existing.getId();
				productTypeCache.put(typeName, id);
				return id;
			}

			// 3️⃣ Create if not exists
			ProductType newType = new ProductType();
			newType.setName(typeName);

			ProductType created = addProductType(newType);

			if (created == null || created.getId() == null) {
				throw new RuntimeException("Failed to create ProductType: " + typeName);
			}

			Integer id = created.getId();

			// 4️⃣ Store in cache
			productTypeCache.put(typeName, id);

			return id;

		} catch (ApiException e) {
			throw new RuntimeException("Error resolving ProductType: " + typeName, e);
		}
	}

	// =========================================================
	// ✅ OVERLOADED METHOD (ProductType version)
	// =========================================================
	public Integer getOrCreateProductTypeId(ProductType type) {

		if (type == null || type.getName() == null || type.getName().isBlank()) {
			throw new RuntimeException("ProductType name is missing");
		}

		return getOrCreateProductTypeId(type.getName().trim());
	}

	// =========================================================
	// Existing API methods
	// =========================================================

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