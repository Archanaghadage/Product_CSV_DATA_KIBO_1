package com.ign.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kibocommerce.sdk.catalogadministration.api.ProductVariationsApi;
import com.kibocommerce.sdk.catalogadministration.api.ProductsApi;
import com.kibocommerce.sdk.catalogadministration.models.CatalogAdminsProduct;
import com.kibocommerce.sdk.catalogadministration.models.ProductVariation;
import com.kibocommerce.sdk.catalogadministration.models.ProductVariationCollection;
import com.kibocommerce.sdk.catalogadministration.models.ProductVariationPagedCollection;
import com.kibocommerce.sdk.common.ApiException;

@Service
public class ProductService {

	private final ProductsApi productsApi;
	private final ProductVariationsApi productVariationsApi;

	public ProductService(ProductsApi productsApi, ProductVariationsApi productVariationsApi) {
		this.productsApi = productsApi;
		this.productVariationsApi = productVariationsApi;
	}

	// 🔹 Create Product
	public void createProductIfNotExist(CatalogAdminsProduct product) {
		try {
			productsApi.getProduct(product.getProductCode(), null);
			System.out.println("Already exists: " + product.getProductCode());
		} catch (ApiException e) {
			if (e.getCode() == 404) {
				try {
					productsApi.addProduct(product);
					System.out.println("Created: " + product.getProductCode());
				} catch (ApiException ex) {
					System.out.println("Create failed: " + ex.getMessage());
				}
			}
		}
	}

	// 🔹 Update Product (Better)
	public void updateProduct(CatalogAdminsProduct product) {
		try {
			productsApi.updateProduct(product.getProductCode(), product);
			System.out.println("Updated: " + product.getProductCode());
		} catch (ApiException e) {
			if (e.getCode() == 404) {
				System.out.println("Product not found for update: " + product.getProductCode());
			} else {
				System.out.println("Update failed: " + e.getResponseBody());
			}
		}
	}

	// 🔹 Delete Product
	public void deleteProduct(String productCode) {
		try {
			productsApi.deleteProduct(productCode);
			System.out.println("Deleted: " + productCode);
		} catch (ApiException e) {
			if (e.getCode() == 404) {
				System.out.println("Product not found: " + productCode);
			} else {
				System.out.println("Delete failed: " + e.getMessage());
			}
		}
	}

	// 🔹 Get all variations
	public ProductVariationPagedCollection getAllProductVariations(String productCode) throws ApiException {
		return productVariationsApi.getProductVariations(productCode, null, null, null, null);
	}

	// 🔹 Enable all variations
	public void enableAllVariations(String productCode) throws ApiException {
		ProductVariationPagedCollection paged = getAllProductVariations(productCode);
		if (paged == null || paged.getItems() == null || paged.getItems().isEmpty()) {
			System.out.println("No variations found for: " + productCode);
			return;
		}
		List<ProductVariation> updatedList = new ArrayList<>();
		for (ProductVariation existing : paged.getItems()) {

			ProductVariation updated = new ProductVariation();
			updated.setVariationkey(existing.getVariationkey());
			updated.setVariationProductCode(existing.getVariationProductCode());
			updated.setIsActive(true);

			updatedList.add(updated);
		}
		ProductVariationCollection collection = new ProductVariationCollection();
		collection.setItems(updatedList);
		productVariationsApi.updateProductVariations(productCode, collection);
		System.out.println("All variations enabled for: " + productCode);
	}
}
