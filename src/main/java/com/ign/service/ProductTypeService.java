package com.ign.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ign.config.KiboConfig;
import com.kibocommerce.sdk.catalogadministration.api.ProductTypesApi;
import com.kibocommerce.sdk.catalogadministration.models.AttributeInProductType;
import com.kibocommerce.sdk.catalogadministration.models.ProductType;
import com.kibocommerce.sdk.catalogadministration.models.ProductTypeCollection;
import com.kibocommerce.sdk.common.ApiException;

import jakarta.annotation.PostConstruct;

@Service
public class ProductTypeService {

	private final ProductTypesApi api;
	private final ProductTypeCache productTypeCache;

	public ProductTypeService(KiboConfig kiboConfig, ProductTypeCache productTypeCache) {
		this.api = ProductTypesApi.builder().withConfig(kiboConfig.getConfiguration()).build();
		this.productTypeCache = productTypeCache;
	}

	// LOAD CACHE AT APPLICATION START
	@PostConstruct
	public void loadProductTypesToCache() {
		try {
			ProductTypeCollection collection = api.getProductTypes(0, 200, null, null, null);
			if (collection != null && collection.getItems() != null) {
				for (ProductType type : collection.getItems()) {
					if (type.getName() != null && type.getId() != null) {
						productTypeCache.put(type.getName(), type.getId());
					}
				}
				System.out.println("ProductType cache loaded: " + collection.getItems().size());
			}
		} catch (ApiException e) {
			throw new RuntimeException("Failed to load product types into cache", e);
		}
	}

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
			// 2️⃣ Check if exists in Kibo
			ProductType existing = getProductTypeByName(typeName);
			Integer productTypeId;
			if (existing != null && existing.getId() != null) {
				productTypeId = existing.getId();
			} else {
				// 3️⃣ Create ProductType
				ProductType base = new ProductType();
				base.setName(typeName);
				base.setProductUsages(type.getProductUsages());
				ProductType created = api.addProductType(base);
				if (created == null || created.getId() == null) {
					throw new RuntimeException("Failed to create ProductType: " + typeName);
				}
				productTypeId = created.getId();
			}
			// 4️⃣ Attach attributes
			attachAttributes(productTypeId, type);
			// 5️⃣ Update cache
			productTypeCache.put(typeName, productTypeId);
			return productTypeId;
		} catch (ApiException e) {
			throw new RuntimeException("Error resolving ProductType: " + typeName, e);
		}
	}

	private void attachAttributes(Integer productTypeId, ProductType type) {
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
	}

	public ProductType getProductTypeByName(String name) throws ApiException {
		String filter = "name eq '" + name + "'";
		ProductTypeCollection collection = api.getProductTypes(0, 200, null, filter, null);
		if (collection != null && collection.getItems() != null && !collection.getItems().isEmpty()) {
			return collection.getItems().get(0);
		}
		return null;
	}

	private boolean attributeExists(List<AttributeInProductType> list, String fqn) {
		if (list == null)
			return false;
		return list.stream().anyMatch(a -> fqn.equalsIgnoreCase(a.getAttributeFQN()));
	}

	// OPTION
	public void addOptionIfNotExists(Integer productTypeId, AttributeInProductType attribute) {
		try {
			ProductType productType = api.getProductType(productTypeId);
			List<AttributeInProductType> options = productType.getOptions();
			if (options == null)
				options = new ArrayList<>();
			if (attributeExists(options, attribute.getAttributeFQN())) {
				System.out.println("Option already exists: " + attribute.getAttributeFQN());
				return;
			}
			int nextOrder = options.size() + 1;
			attribute.setOrder(nextOrder);
			options.add(attribute);
			productType.setOptions(options);
			api.updateProductType(productTypeId, productType);
			System.out.println("Option added: " + attribute.getAttributeFQN());
		} catch (ApiException e) {
			throw new RuntimeException("Error adding option " + attribute.getAttributeFQN(), e);
		}
	}

	// EXTRA
	public void addExtraIfNotExists(Integer productTypeId, AttributeInProductType extra) {
		try {
			ProductType productType = api.getProductType(productTypeId);
			List<AttributeInProductType> extras = productType.getExtras();
			if (extras == null)
				extras = new ArrayList<>();
			if (attributeExists(extras, extra.getAttributeFQN())) {
				System.out.println("Extra already exists: " + extra.getAttributeFQN());
				return;
			}
			int nextOrder = extras.size() + 1;
			extra.setOrder(nextOrder);
			api.addExtra(productTypeId, extra);
			System.out.println("Extra added: " + extra.getAttributeFQN());
		} catch (ApiException e) {
			throw new RuntimeException("Error adding extra " + extra.getAttributeFQN(), e);
		}
	}

	// PROPERTY

	public void addPropertyIfNotExists(Integer productTypeId, AttributeInProductType property) {
		try {
			ProductType productType = api.getProductType(productTypeId);
			List<AttributeInProductType> properties = productType.getProperties();
			if (properties == null)
				properties = new ArrayList<>();
			if (attributeExists(properties, property.getAttributeFQN())) {
				System.out.println("Property already exists: " + property.getAttributeFQN());
				return;
			}
			int nextOrder = properties.size() + 1;
			property.setOrder(nextOrder);
			api.addProperty(productTypeId, property);
			System.out.println("Property added: " + property.getAttributeFQN());
		} catch (ApiException e) {
			throw new RuntimeException("Error adding property " + property.getAttributeFQN(), e);
		}
	}

	// VARIANT PROPERTY
	public void addVariantPropertyIfNotExists(Integer productTypeId, AttributeInProductType variant) {
		try {
			ProductType productType = api.getProductType(productTypeId);
			List<AttributeInProductType> variants = productType.getVariantProperties();
			if (variants == null)
				variants = new ArrayList<>();
			if (attributeExists(variants, variant.getAttributeFQN())) {
				System.out.println("Variant already exists: " + variant.getAttributeFQN());
				return;
			}
			int nextOrder = variants.size() + 1;
			variant.setOrder(nextOrder);
			api.addVariantProperty(productTypeId, variant);
			System.out.println("Variant property added: " + variant.getAttributeFQN());
		} catch (ApiException e) {
			throw new RuntimeException("Error adding variant property " + variant.getAttributeFQN(), e);
		}
	}
}