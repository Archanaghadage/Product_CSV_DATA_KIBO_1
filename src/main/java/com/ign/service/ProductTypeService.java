package com.ign.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.ign.config.KiboConfig;
import com.kibocommerce.sdk.catalogadministration.api.ProductTypesApi;
import com.kibocommerce.sdk.catalogadministration.models.AttributeInProductType;
import com.kibocommerce.sdk.catalogadministration.models.ProductType;
import com.kibocommerce.sdk.catalogadministration.models.ProductTypeCollection;
import com.kibocommerce.sdk.common.ApiException;

@Service
public class ProductTypeService {

	private final ProductTypesApi api;

	// Thread-safe cache
	private final Map<String, Integer> productTypeCache = new ConcurrentHashMap<>();

	public ProductTypeService(KiboConfig kiboConfig) {
		this.api = ProductTypesApi.builder().withConfig(kiboConfig.getConfiguration()).build();
	}

	// MAIN METHOD (String version)
	public Integer getOrCreateProductTypeId(String typeName) {
		if (typeName == null || typeName.isBlank()) {
			throw new RuntimeException("ProductType name is missing");
		}
		typeName = typeName.trim();

		// 1️⃣ Check cache
		Integer cachedId = productTypeCache.get(typeName);
		System.err.println("TypeName1: " + typeName);
		if (cachedId != null) {
			return cachedId;
		}
		try {
			// 2️ Check in Kibo
			ProductType existing = getProductTypeByName(typeName);
			System.err.println("TypeName2: " + typeName);
			if (existing != null && existing.getId() != null) {
				Integer id = existing.getId();
				productTypeCache.put(typeName, id);
				return id;
			}

			// 3️ Create if not exists
			ProductType newType = new ProductType();
			System.err.println("New Type: " + newType);
			newType.setName(typeName);

			ProductType created = addProductType(newType);
			System.err.println("New Type: " + created);
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

	// OVERLOADED METHOD (ProductType version)
	public Integer getOrCreateProductTypeId(ProductType type) {

		if (type == null || type.getName() == null || type.getName().isBlank()) {
			throw new RuntimeException("ProductType name is missing");
		}

		String typeName = type.getName().trim();

		// 1️⃣ Check cache
		Integer cachedId = productTypeCache.get(typeName);
		if (cachedId != null) {
			return cachedId;
		}

		try {
			// 2️⃣ Check if already exists in Kibo
			ProductType existing = getProductTypeByName(typeName);
			Integer productTypeId;

			if (existing != null && existing.getId() != null) {
				productTypeId = existing.getId();
			} else {

				// 3️⃣ Create base ProductType (NO attributes here)
				ProductType base = new ProductType();
				base.setName(typeName);
				base.setProductUsages(type.getProductUsages());

				ProductType created = addProductType(base);

				if (created == null || created.getId() == null) {
					throw new RuntimeException("Failed to create ProductType: " + typeName);
				}

				productTypeId = created.getId();
			}

			// 4️⃣ Attach attributes properly using add APIs

			if (type.getOptions() != null) {
				for (AttributeInProductType option : type.getOptions()) {
					addOptionIfNotExists(productTypeId, option);
				}
			}

			if (type.getExtras() != null) {
				for (AttributeInProductType extra : type.getExtras()) {
					addExtraIfNotExists(productTypeId, extra);
				}
			}

			if (type.getProperties() != null) {
				for (AttributeInProductType property : type.getProperties()) {
					addPropertyIfNotExists(productTypeId, property);
				}
			}

			if (type.getVariantProperties() != null) {
				for (AttributeInProductType variant : type.getVariantProperties()) {
					addVariantPropertyIfNotExists(productTypeId, variant);
				}
			}
			// 5️⃣ Cache and return
			productTypeCache.put(typeName, productTypeId);

			return productTypeId;

		} catch (ApiException e) {
			throw new RuntimeException("Error resolving ProductType: " + typeName, e);
		}
	}

	// Existing API methods
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

	private boolean attributeExists(List<AttributeInProductType> existingList, String attributeFQN) {
		if (existingList == null) {
			return false;
		}
		return existingList.stream().anyMatch(a -> attributeFQN.equals(a.getAttributeFQN()));
	}

	public void addOptionIfNotExists(Integer productTypeId, AttributeInProductType option) {
		try {
			ProductType productType = api.getProductType(productTypeId);
			if (attributeExists(productType.getOptions(), option.getAttributeFQN())) {
				System.out.println("Option already exists: " + option.getAttributeFQN());
				return;
			}
			api.addOption(productTypeId, option);
			System.out.println("Option added: " + option.getAttributeFQN());
		} catch (ApiException e) {
			throw new RuntimeException("Error adding option " + option.getAttributeFQN(), e);
		}
	}

	public void addExtraIfNotExists(Integer productTypeId, AttributeInProductType extra) {
		try {
			ProductType productType = api.getProductType(productTypeId);
			if (attributeExists(productType.getExtras(), extra.getAttributeFQN())) {
				System.out.println("Extra already exists: " + extra.getAttributeFQN());
				return;
			}
			api.addExtra(productTypeId, extra);
			System.out.println("Extra added: " + extra.getAttributeFQN());
		} catch (ApiException e) {
			throw new RuntimeException("Error adding extra " + extra.getAttributeFQN(), e);
		}
	}

	public void addPropertyIfNotExists(Integer productTypeId, AttributeInProductType property) {
		try {
			ProductType productType = api.getProductType(productTypeId);
			if (attributeExists(productType.getProperties(), property.getAttributeFQN())) {
				System.out.println("Property already exists: " + property.getAttributeFQN());
				return;
			}
			api.addProperty(productTypeId, property);
			System.out.println("Property added: " + property.getAttributeFQN());
		} catch (ApiException e) {
			throw new RuntimeException("Error adding property " + property.getAttributeFQN(), e);
		}
	}

	public void addVariantPropertyIfNotExists(Integer productTypeId, AttributeInProductType variantProperty) {
		try {
			ProductType productType = api.getProductType(productTypeId);
			if (attributeExists(productType.getVariantProperties(), variantProperty.getAttributeFQN())) {
				System.out.println("Variant property already exists: " + variantProperty.getAttributeFQN());
				return;
			}
			api.addVariantProperty(productTypeId, variantProperty);
			System.out.println("Variant property added: " + variantProperty.getAttributeFQN());
		} catch (ApiException e) {
			throw new RuntimeException("Error adding variant property " + variantProperty.getAttributeFQN(), e);
		}
	}

}
