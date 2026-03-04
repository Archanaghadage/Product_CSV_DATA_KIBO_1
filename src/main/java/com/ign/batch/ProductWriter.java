package com.ign.batch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.ign.mapper.ProductMapper;
import com.ign.service.ProductService;
import com.kibocommerce.sdk.catalogadministration.models.CatalogAdminsProduct;
import com.kibocommerce.sdk.catalogadministration.models.CatalogAdminsProductOption;
import com.kibocommerce.sdk.catalogadministration.models.ProductVariationOption;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ProductWriter implements ItemWriter<CatalogAdminsProduct> {

	private final ProductService productService;
	private final ProductMapper productMapper;

	private int apiHitCount = 0; // 🔥 API counter

	@Override
	public void write(Chunk<? extends CatalogAdminsProduct> chunk) throws Exception {
		List<? extends CatalogAdminsProduct> items = chunk.getItems();
		System.out.println("Chunk received size: " + items.size());
		Map<String, List<CatalogAdminsProduct>> variantGroup = new HashMap<>();
		// 1️⃣ Group Variants
		for (CatalogAdminsProduct product : items) {
			if (Boolean.TRUE.equals(product.getIsVariation())) {
				variantGroup.computeIfAbsent(product.getBaseProductCode(), k -> new ArrayList<>()).add(product);
			}
		}
		// 2️⃣ Standard Products
		for (CatalogAdminsProduct product : items) {
			if (!Boolean.TRUE.equals(product.getIsVariation())
					&& !Boolean.TRUE.equals(product.getHasConfigurableOptions())) {
				long start = System.currentTimeMillis();
				productService.addProduct(product); // 🔥 API CALL
				long end = System.currentTimeMillis();
				apiHitCount++;
				System.out.println("STANDARD CREATED: " + product.getProductCode() + " | Time: " + (end - start) + " ms"
						+ " | Total API Hits: " + apiHitCount);
			}
		}

		// 3️ Configurable Parents
		for (CatalogAdminsProduct parent : items) {
			if (Boolean.TRUE.equals(parent.getHasConfigurableOptions())) {
				List<CatalogAdminsProduct> variants = variantGroup.get(parent.getProductCode());
				if (variants == null || variants.isEmpty())
					continue;
				List<CatalogAdminsProductOption> options = productMapper.buildOptions(variants);
				List<ProductVariationOption> variationOptions = productMapper.buildVariationOptions(variants);
				parent.setOptions(options);
				parent.setVariationOptions(variationOptions);
				long start = System.currentTimeMillis();
				productService.addProduct(parent); 
				productService.enableAllVariations(parent.getProductCode());
				long end = System.currentTimeMillis();
				apiHitCount++;
				System.out.println("CONFIGURABLE CREATED: " + parent.getProductCode() + " | Time: " + (end - start)
						+ " ms" + " | Total API Hits: " + apiHitCount);
			}
		}

		System.out.println("Chunk Completed. Total API Hits So Far: " + apiHitCount);
	}
}