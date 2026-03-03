package com.ign.service;

import org.springframework.stereotype.Service;

import com.ign.config.KiboConfig;
import com.kibocommerce.sdk.catalogadministration.api.ProductAttributesApi;
import com.kibocommerce.sdk.catalogadministration.models.CatalogAdminsAttribute;
import com.kibocommerce.sdk.common.ApiException;

@Service
public class AttributeService {

	private final ProductAttributesApi attributesApi;

	// Create API once (better performance)
	public AttributeService(KiboConfig kiboConfig) {
		this.attributesApi = ProductAttributesApi.builder().withConfig(kiboConfig.getConfiguration()).build();
	}

	public void createIfNotExists(CatalogAdminsAttribute attribute) {

		try {
			attributesApi.getAttribute(attribute.getAttributeCode(), null);
			System.out.println("Attribute already exists: " + attribute.getAttributeCode());

		} catch (ApiException e) {

			if (e.getCode() == 404) {
				try {
					attributesApi.addAttribute(attribute);
					System.out.println("Attribute created: " + attribute.getAttributeCode());
				} catch (ApiException ex) {
					System.err.println("Create error: " + ex.getResponseBody());
				}
			} else {
				System.err.println("Check error: " + e.getResponseBody());
			}
		}
	}

//     Get attribute by FQN
	public CatalogAdminsAttribute get(String attributeFQN, String responseGroups) throws ApiException {

		return attributesApi.getAttribute(attributeFQN, responseGroups);
	}
}