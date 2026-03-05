package com.ign.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ign.config.KiboConfig;
import com.kibocommerce.sdk.catalogadministration.api.ProductVariationsApi;
import com.kibocommerce.sdk.catalogadministration.api.ProductsApi;
import com.kibocommerce.sdk.catalogadministration.models.CatalogAdminsProduct;
import com.kibocommerce.sdk.catalogadministration.models.CatalogAdminsProductCollection;
import com.kibocommerce.sdk.catalogadministration.models.ProductVariation;
import com.kibocommerce.sdk.catalogadministration.models.ProductVariationCollection;
import com.kibocommerce.sdk.catalogadministration.models.ProductVariationPagedCollection;
import com.kibocommerce.sdk.common.ApiException;

@Service
public class ProductService {

	@Autowired
	private KiboConfig kiboConfig;

	public CatalogAdminsProductCollection getAllProducts() throws ApiException {
		ProductsApi api = ProductsApi.builder().withConfig(kiboConfig.getConfiguration()).build();
		return api.getProducts(null, null, null, null, null, null, null, null);
	}

	public CatalogAdminsProduct getProductById(String productId) throws ApiException {
		ProductsApi api = ProductsApi.builder().withConfig(kiboConfig.getConfiguration()).build();
		CatalogAdminsProduct product = api.getProduct(productId, null);
		return product;
	}

	public CatalogAdminsProduct addProduct(CatalogAdminsProduct product) throws ApiException {
		ProductsApi api = ProductsApi.builder().withConfig(kiboConfig.getConfiguration()).build();
		CatalogAdminsProduct addedProduct = api.addProduct(product);
		return addedProduct;
	}

	public void deleteProduct(String productId) throws ApiException {
		ProductsApi api = ProductsApi.builder().withConfig(kiboConfig.getConfiguration()).build();
		api.deleteProduct(productId);
	}

	public void updateProduct(String parentCode, CatalogAdminsProduct parentUpdate) throws ApiException {
		ProductsApi api = ProductsApi.builder().withConfig(kiboConfig.getConfiguration()).build();
		api.updateProduct(parentCode, parentUpdate);
	}

	public ProductVariationCollection enableProductVariations(String productCode,
			ProductVariationCollection productVariations) throws ApiException {
		ProductVariationsApi api = ProductVariationsApi.builder().withConfig(kiboConfig.getConfiguration()).build();
		// Use updateProductVariations (plural) for multiple variations
		return api.updateProductVariations(productCode, productVariations);
	}

	// Get ALL variations for a product (to discover variation keys)
	public ProductVariationPagedCollection getAllProductVariations(String productCode) throws ApiException {
		ProductVariationsApi api = ProductVariationsApi.builder().withConfig(kiboConfig.getConfiguration()).build();

		return api.getProductVariations(productCode, null, null, null, null);
	}

	public void enableAllVariations(String productCode) throws ApiException {
		ProductVariationPagedCollection paged = getAllProductVariations(productCode);
		if (paged == null || paged.getItems() == null || paged.getItems().isEmpty()) {
			return;
		}
		List<ProductVariation> variations = paged.getItems();
		for (ProductVariation variation : variations) {
			variation.setIsActive(true);
		}
		ProductVariationCollection collection = new ProductVariationCollection();
		collection.setItems(variations);
		enableProductVariations(productCode, collection);
	}

}
