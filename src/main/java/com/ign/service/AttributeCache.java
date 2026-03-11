package com.ign.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kibocommerce.sdk.catalogadministration.models.CatalogAdminsAttribute;

import jakarta.annotation.PostConstruct;

@Component
public class AttributeCache {
	
	@PostConstruct
	public void init(){
	    System.out.println("Attribute cache initialized");
	}

	private Map<String, CatalogAdminsAttribute> cache = new HashMap<>();

    public void put(CatalogAdminsAttribute attr) {
        cache.put(attr.getAttributeFQN(), attr);
    }

    public CatalogAdminsAttribute get(String fqn) {
        return cache.get(fqn);
    }

    public boolean exists(String fqn) {
        return cache.containsKey(fqn);
    }

    public Map<String, CatalogAdminsAttribute> getAll() {
        return cache;
    }
}