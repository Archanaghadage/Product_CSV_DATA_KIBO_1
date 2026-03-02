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
        this.attributesApi = ProductAttributesApi.builder()
                .withConfig(kiboConfig.getConfiguration())
                .build();
    }

//      Check if attribute exists in Kibo
    public boolean exists(String attributeFQN) {
        try {
            attributesApi.getAttribute(attributeFQN, null);
            return true;
        } catch (ApiException e) {
            if (e.getCode() == 404) {
                return false;
            }
            throw new RuntimeException("Error checking attribute: " + attributeFQN, e);
        }
    }

    
//      Create attribute only if not exists
    
    public CatalogAdminsAttribute createIfNotExists(CatalogAdminsAttribute attribute)
            throws ApiException {

        if (!exists(attribute.getAttributeFQN())) {
            return attributesApi.addAttribute(attribute);
        }

        // Already exists → just return existing
        System.err.println("Attribute  Exists --> "+attribute.getAttributeFQN());
        return attributesApi.getAttribute(attribute.getAttributeFQN(), null);
    }

    
//      Direct create (force create)
    public CatalogAdminsAttribute create(CatalogAdminsAttribute attribute)
            throws ApiException {
    	System.err.println(" Attribute Created Successfully --> "+attribute);
        return attributesApi.addAttribute(attribute);
    }

   
//     Get attribute by FQN
    public CatalogAdminsAttribute get(String attributeFQN, String responseGroups)
            throws ApiException {

        return attributesApi.getAttribute(attributeFQN, responseGroups);
    }
}