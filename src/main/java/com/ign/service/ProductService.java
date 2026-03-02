package com.ign.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ign.config.KiboConfig;
import com.kibocommerce.sdk.catalogadministration.api.ProductsApi;
import com.kibocommerce.sdk.catalogadministration.models.CatalogAdminsProduct;
import com.kibocommerce.sdk.catalogadministration.models.CatalogAdminsProductCollection;
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
		api.updateProduct(parentCode,parentUpdate);
	}

	
}
