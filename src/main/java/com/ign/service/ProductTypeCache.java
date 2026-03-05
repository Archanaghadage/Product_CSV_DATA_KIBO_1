package com.ign.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class ProductTypeCache {

    private final Map<String, Integer> productTypeMap = new ConcurrentHashMap<>();

    public void put(String name, Integer id) {
        if (name != null && id != null) {
            productTypeMap.put(name.trim().toLowerCase(), id);
        }
    }

    public Integer get(String name) {
        if (name == null) return null;
        return productTypeMap.get(name.trim().toLowerCase());
    }

    public Map<String, Integer> getAll() {
        return productTypeMap;
    }
}