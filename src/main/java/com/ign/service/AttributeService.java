package com.ign.service;

import org.springframework.stereotype.Service;

import com.ign.config.KiboConfig;
import com.kibocommerce.sdk.catalogadministration.api.ProductAttributesApi;
import com.kibocommerce.sdk.catalogadministration.models.CatalogAdminsAttribute;
import com.kibocommerce.sdk.catalogadministration.models.CatalogAdminsAttributeCollection;
import com.kibocommerce.sdk.common.ApiException;

import jakarta.annotation.PostConstruct;

@Service
public class AttributeService {

	private final ProductAttributesApi attributesApi;
	private final AttributeCache cache;

	// Create API once (better performance)
	public AttributeService(KiboConfig kiboConfig, AttributeCache cache) {
		this.attributesApi = ProductAttributesApi.builder().withConfig(kiboConfig.getConfiguration()).build();
		this.cache = cache;
	}

	@PostConstruct
	public void loadAttributesToCache() {
		try {
			CatalogAdminsAttributeCollection collection = attributesApi.getAttributes(0, 200, null, null, null);
			if (collection.getItems() != null) {
				for (CatalogAdminsAttribute attr : collection.getItems()) {
					cache.put(attr);
				}
				System.out.println("Attribute cache loaded: " + cache.getAll().size());
			}
		} catch (ApiException e) {
			throw new RuntimeException(e);
		}
	}

	public void createIfNotExists(CatalogAdminsAttribute attribute) {
		try {
			attributesApi.getAttribute("tenant~" + attribute.getAttributeFQN(), null);
			System.out.println("Attribute already exists: " + attribute.getAttributeFQN());
		} catch (ApiException e) {
			if (e.getCode() == 404) {
				try {
					attributesApi.addAttribute(attribute);
					System.out.println("Attribute created: " + attribute.getAttributeFQN());
				} catch (ApiException ex) {
					System.err.println("Create error: " + ex.getResponseBody());
				}
			} else {
				System.err.println("Check error: " + e.getResponseBody());
			}
		}
	}

//     Get attribute by FQN
	public CatalogAdminsAttribute getAttribute(String attributeFQN, String responseGroups) throws ApiException {
		return attributesApi.getAttribute(attributeFQN, responseGroups);
	}
}