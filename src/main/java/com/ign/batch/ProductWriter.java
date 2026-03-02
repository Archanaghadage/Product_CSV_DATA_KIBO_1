package com.ign.batch;

import java.util.*;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.ign.dto.ProductCsvDto;
import com.ign.mapper.ProductMapper;
import com.ign.mapper.ProductTypeMapper;
import com.ign.service.ProductService;
import com.ign.service.ProductTypeService;
import com.kibocommerce.sdk.catalogadministration.models.*;
import com.kibocommerce.sdk.common.ApiException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ProductWriter implements ItemWriter<ProductCsvDto> {

	private final ProductService productService;
	private final ProductTypeService productTypeService;
	private final ProductTypeMapper productTypeMapper;

	private final Map<String, String> productTypeCache = new HashMap<>();

	@Override
	public void write(Chunk<? extends ProductCsvDto> chunk) throws Exception {

		List<? extends ProductCsvDto> items = chunk.getItems();

		Map<String, List<ProductCsvDto>> variantGroup = new HashMap<>();
		List<ProductCsvDto> parentsAndStandalones = new ArrayList<>();

		// ===============================
		// 1️⃣ Pre-processing
		// ===============================
		for (ProductCsvDto dto : items) {

			handleProductType(dto);

			if (isVariant(dto)) {
				variantGroup.computeIfAbsent(dto.getParentProductCode(), k -> new ArrayList<>()).add(dto);
			} else {
				parentsAndStandalones.add(dto);
			}
		}

		// ===============================
		// 2️⃣ Standard Products
		// ===============================
		for (ProductCsvDto dto : parentsAndStandalones) {

			if ("Standard".equalsIgnoreCase(dto.getProductUsage())) {
				CatalogAdminsProduct product = ProductMapper.map(dto);
				productService.addProduct(product);
			}
		}

		// ===============================
		// 3️⃣ Configurable Parent (Single API Call)
		// ===============================
		for (ProductCsvDto parentDto : parentsAndStandalones) {

			if (!isConfigurableParent(parentDto))
				continue;

			List<ProductCsvDto> variants = variantGroup.get(parentDto.getProductCode());

			if (variants == null || variants.isEmpty())
				continue;

			// Ensure variants use same productType
			for (ProductCsvDto variant : variants) {
				variant.setProductTypeId(parentDto.getProductTypeId());
			}

			// 🔥 Step A: Build Options from Variants
			Map<String, Set<String>> optionMap = new LinkedHashMap<>();

			for (ProductCsvDto variant : variants) {

				Map<String, String> attrs = variant.getVariantAttributes();

				if (attrs == null)
					continue;

				attrs.forEach((key, value) -> {
					if (value != null && !value.isBlank()) {
						optionMap.computeIfAbsent(key, k -> new LinkedHashSet<>()).add(value.trim());
					}
				});
			}

			List<CatalogAdminsProductOption> options = new ArrayList<>();

			optionMap.forEach((attrCode, valuesSet) -> {

				CatalogAdminsProductOption option = new CatalogAdminsProductOption();

				option.setAttributeFQN("tenant~" + attrCode);
				option.setIsProductImageGroupSelector(false);

				List<CatalogAdminsProductOptionValue> values = new ArrayList<>();

				for (String val : valuesSet) {
					CatalogAdminsProductOptionValue v = new CatalogAdminsProductOptionValue();
					v.setValue(val);
					values.add(v);
				}

				option.setValues(values);
				options.add(option);
			});

			// 🔥 Step B: Build VariationOptions List
			List<ProductVariationOption> variationOptions = new ArrayList<>();

			for (ProductCsvDto variant : variants) {

				Map<String, String> attrs = variant.getVariantAttributes();

				if (attrs == null)
					continue;

				for (Map.Entry<String, String> entry : attrs.entrySet()) {

					String value = entry.getValue();

					if (value == null || value.isBlank())
						continue;

					ProductVariationOption variationOption = new ProductVariationOption();

					variationOption.setAttributeFQN("tenant~" + entry.getKey());
					variationOption.setValue(value.trim());
					variationOption.setContent(null);
					variationOptions.add(variationOption);
				}
			}

			// 🔥 Step C: Create Parent Product
			CatalogAdminsProduct parentProduct = ProductMapper.map(parentDto);

			parentProduct.setIsVariation(false);
			parentProduct.setHasConfigurableOptions(true);
			parentProduct.setOptions(options);
			parentProduct.setVariationOptions(variationOptions);

			// ✅ ONLY ONE API CALL
			System.out.println("ParentProduct -->" + parentProduct);
			productService.addProduct(parentProduct);
		}
	}

	// =====================================================
	// ProductType Handling
	// =====================================================
	private void handleProductType(ProductCsvDto dto) {

		if (dto.getProductTypeName() == null || dto.getProductTypeName().isBlank())
			return;

		String typeName = dto.getProductTypeName().trim();
		String typeId = productTypeCache.get(typeName);

		if (typeId == null) {

			ProductType type = null;

			try {
				type = productTypeService.getProductTypeByName(typeName);
			} catch (ApiException e) {
				e.printStackTrace();
			}

			if (type == null) {
				try {
					type = productTypeService.addProductType(productTypeMapper.map(dto));
				} catch (ApiException e) {
					e.printStackTrace();
				}
			}

			typeId = type.getId().toString();
			productTypeCache.put(typeName, typeId);
		}

		dto.setProductTypeId(typeId);
	}

	private boolean isVariant(ProductCsvDto dto) {
		return "Configurable".equalsIgnoreCase(dto.getProductUsage()) && dto.getParentProductCode() != null
				&& !dto.getParentProductCode().isBlank();
	}

	private boolean isConfigurableParent(ProductCsvDto dto) {
		return "Configurable".equalsIgnoreCase(dto.getProductUsage())
				&& (dto.getParentProductCode() == null || dto.getParentProductCode().isBlank());
	}
}