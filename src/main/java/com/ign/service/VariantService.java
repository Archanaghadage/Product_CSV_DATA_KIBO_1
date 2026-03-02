package com.ign.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ign.config.KiboConfig;
import com.kibocommerce.sdk.catalogadministration.api.ProductVariationsApi;
import com.kibocommerce.sdk.catalogadministration.models.ProductVariation;
import com.kibocommerce.sdk.catalogadministration.models.ProductVariationCollection;
import com.kibocommerce.sdk.catalogadministration.models.ProductVariationPagedCollection;
import com.kibocommerce.sdk.common.ApiException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class VariantService {

	private final KiboConfig kiboConfig;

	public ProductVariationPagedCollection getAllProductVariations(String productCode) throws ApiException {
		ProductVariationsApi api = ProductVariationsApi.builder().withConfig(kiboConfig.getConfiguration()).build();
		return api.getProductVariations(productCode, null, null, null, null);
	}

	public ProductVariationCollection updateProductVariations(String productCode, ProductVariationCollection collection)
			throws ApiException {
		ProductVariationsApi api = ProductVariationsApi.builder().withConfig(kiboConfig.getConfiguration()).build();
		System.out.println("Variations enabled for: " + productCode);
		return api.updateProductVariations(productCode, collection);
		
	}
}