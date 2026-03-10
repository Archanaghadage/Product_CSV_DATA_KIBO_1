package com.ign.batch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.ign.dto.ProductBatchWrapper;
import com.ign.mapper.ProductMapper;
import com.ign.service.ProductService;
import com.kibocommerce.sdk.catalogadministration.models.CatalogAdminsProduct;
import com.kibocommerce.sdk.catalogadministration.models.CatalogAdminsProductOption;
import com.kibocommerce.sdk.catalogadministration.models.ProductVariationOption;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ProductWriter implements ItemWriter<ProductBatchWrapper> {

	private final ProductService productService;
	private final ProductMapper productMapper;

	@Override
	public void write(Chunk<? extends ProductBatchWrapper> chunk) throws Exception {

		List<? extends ProductBatchWrapper> wrappers = chunk.getItems();
		System.out.println("Chunk received size: " + wrappers.size());

		Map<String, List<CatalogAdminsProduct>> variantGroup = new HashMap<>();

		List<CatalogAdminsProduct> products = new ArrayList<>();

		// Extract products from wrapper
		for (ProductBatchWrapper wrapper : wrappers) {
			products.add(wrapper.getProduct());
		}

		// 1️⃣ Group Variants
		for (CatalogAdminsProduct product : products) {
			if (Boolean.TRUE.equals(product.getIsVariation())) {
				variantGroup.computeIfAbsent(product.getBaseProductCode(), k -> new ArrayList<>()).add(product);
			}
		}

		// 2️⃣ Process Standard Products
		for (ProductBatchWrapper wrapper : wrappers) {

			CatalogAdminsProduct product = wrapper.getProduct();
			String operation = wrapper.getOperation();

			if (!Boolean.TRUE.equals(product.getIsVariation())
					&& !Boolean.TRUE.equals(product.getHasConfigurableOptions())) {

				switch (operation) {

				case "CREATE":
					productService.createProductIfNotExist(product);
					//System.out.println("STANDARD CREATED: " + product.getProductCode());
					break;

				case "UPDATE":
					productService.updateProduct(product);
					//System.out.println("STANDARD UPDATED: " + product.getProductCode());
					break;

				case "DELETE":
					productService.deleteProduct(product.getProductCode());
					//System.out.println("STANDARD DELETED: " + product.getProductCode());
					break;

				default:
					System.out.println("UNKNOWN OPERATION for " + product.getProductCode());
				}
			}
		}

		// 3️⃣ Configurable Parents
		for (ProductBatchWrapper wrapper : wrappers) {

			CatalogAdminsProduct parent = wrapper.getProduct();
			String operation = wrapper.getOperation();

			if (Boolean.TRUE.equals(parent.getHasConfigurableOptions())) {

				List<CatalogAdminsProduct> variants = variantGroup.get(parent.getProductCode());

				if (variants == null || variants.isEmpty()) {
					continue;
				}

				List<CatalogAdminsProductOption> options = productMapper.buildOptions(variants);

				List<ProductVariationOption> variationOptions = productMapper.buildVariationOptions(variants);

				parent.setOptions(options);
				parent.setVariationOptions(variationOptions);

				switch (operation) {

				case "CREATE":
					productService.createProductIfNotExist(parent);
					productService.enableAllVariations(parent.getProductCode());
					//System.out.println("CONFIGURABLE: " + parent.getProductCode());
					break;

				case "UPDATE":
					productService.updateProduct(parent);
					//System.out.println("CONFIGURABLE: " + parent.getProductCode());
					break;

				case "DELETE":
					productService.deleteProduct(parent.getProductCode());
					//System.out.println("CONFIGURABLE: " + parent.getProductCode());
					break;

				default:
					System.out.println("UNKNOWN OPERATION for " + parent.getProductCode());
				}
			}
		}
	}
}