package com.ign.mapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.ign.dto.ProductCsvDto;
import com.ign.service.ProductTypeService;
import com.kibocommerce.sdk.catalogadministration.models.CatalogAdminsProduct;
import com.kibocommerce.sdk.catalogadministration.models.CatalogAdminsProductInventoryInfo;
import com.kibocommerce.sdk.catalogadministration.models.CatalogAdminsProductOption;
import com.kibocommerce.sdk.catalogadministration.models.CatalogAdminsProductOptionValue;
import com.kibocommerce.sdk.catalogadministration.models.CatalogAdminsProductPrice;
import com.kibocommerce.sdk.catalogadministration.models.CommerceRuntimeMeasurement;
import com.kibocommerce.sdk.catalogadministration.models.ProductInCatalogInfo;
import com.kibocommerce.sdk.catalogadministration.models.ProductLocalizedContent;
import com.kibocommerce.sdk.catalogadministration.models.ProductVariationOption;

@Component
public class ProductMapper {

	private final ProductTypeService productTypeService;

	public ProductMapper(ProductTypeService productTypeService) {
		this.productTypeService = productTypeService;
	}

	public CatalogAdminsProduct map(ProductCsvDto row) {

		CatalogAdminsProduct product = new CatalogAdminsProduct();

		// ================= BASIC =================
		product.setProductCode(row.getProductCode());
		product.setProductUsage(row.getProductUsage());
		if (notEmpty(row.getProductTypeName())) {
			Integer productTypeId = productTypeService.getOrCreateProductTypeId(row.getProductTypeName());
			product.setProductTypeId(productTypeId);
			System.out.println("Resolved ProductTypeId ---> " + productTypeId);
		}
		product.setUpc(row.getUpc());
		if (notEmpty(row.getMasterCatalogId())) {
			product.setMasterCatalogId(Integer.parseInt(row.getMasterCatalogId()));
		}
		boolean isVariant = notEmpty(row.getParentProductCode());
		
		// ================= Inventory =================
		CatalogAdminsProductInventoryInfo inventory = new CatalogAdminsProductInventoryInfo();
        inventory.setManageStock(parseBool(row.getManageStock()));
        inventory.setOutOfStockBehavior(row.getOutOfStockBehavior());
        product.setInventoryInfo(inventory);
 
		
		// ================= USAGE LOGIC =================
		if ("Standard".equalsIgnoreCase(row.getProductUsage())) {
			product.setIsVariation(false);
			product.setHasConfigurableOptions(false);
			product.setHasStandAloneOptions(false);
		} else if ("Configurable".equalsIgnoreCase(row.getProductUsage())) {
			if (isVariant) {
				product.setIsVariation(true);
				product.setBaseProductCode(row.getParentProductCode());
				product.setHasConfigurableOptions(false);
				product.setHasStandAloneOptions(false);
			} else {
				product.setIsVariation(false);
				product.setHasConfigurableOptions(true);
				product.setHasStandAloneOptions(false);
			}
		}

		// ================= CATALOG INFO =================
		if (notEmpty(row.getCatalogId())) {
			ProductInCatalogInfo catalogInfo = new ProductInCatalogInfo();
			catalogInfo.setCatalogId(Integer.parseInt(row.getCatalogId()));
			catalogInfo.setIsActive("TRUE".equalsIgnoreCase(row.getIsActive()));
			product.setProductInCatalogs(Collections.singletonList(catalogInfo));
		}

		// ================= CONTENT =================
		ProductLocalizedContent content = new ProductLocalizedContent();
		content.setProductName(row.getProductName());
		content.setProductShortDescription(row.getProductShortDescription());
		content.setProductFullDescription(row.getProductFullDescription());
		content.setLocaleCode(row.getLocaleCode());

		product.setContent(content);

		// ================= PRICE =================
		if (notEmpty(row.getPrice())) {
			CatalogAdminsProductPrice price = new CatalogAdminsProductPrice();
			price.setPrice(Double.parseDouble(row.getPrice()));
			if (notEmpty(row.getSalePrice())) {
				price.setSalePrice(Double.parseDouble(row.getSalePrice()));
			}
			price.setIsoCurrencyCode(row.getIsoCurrencyCode());
			product.setPrice(price);
		}

		// ================= PACKAGING =================
		product.setPackageHeight(parseMeasurement(row.getPackageHeight(), row.getHeightUnit()));
		product.setPackageWidth(parseMeasurement(row.getPackageWidth(), row.getWidthUnit()));
		product.setPackageLength(parseMeasurement(row.getPackageLength(), row.getLengthUnit()));
		product.setPackageWeight(parseMeasurement(row.getPackageWeight(), row.getWeightUnit()));

		// ================= FULFILLMENT =================
		if (notEmpty(row.getFulfillmentTypes())) {
			product.setFulfillmentTypesSupported(Arrays.asList(row.getFulfillmentTypes().split("\\|")));
		}

		// ================= BUILD VARIANT ATTRIBUTE MAP =================
		if (notEmpty(row.getParentProductCode())) {
			Map<String, String> variantMap = new LinkedHashMap<>();
			if (notEmpty(row.getSizeoption())) {
				variantMap.put("sizeoption", row.getSizeoption().trim());
			}
			if (notEmpty(row.getColoroptions())) {
				variantMap.put("coloroptions", row.getColoroptions().trim());
			}
			if (!variantMap.isEmpty()) {
				row.setVariantAttributes(variantMap);
			}
		}

		// ================= VARIATION OPTIONS =================
		if (isVariant && row.getVariantAttributes() != null && !row.getVariantAttributes().isEmpty()) {
			List<ProductVariationOption> variationOptions = new ArrayList<>();
			for (Map.Entry<String, String> entry : row.getVariantAttributes().entrySet()) {
				if (entry.getKey() == null || entry.getValue() == null)
					continue;
				ProductVariationOption option = new ProductVariationOption();
				option.setAttributeFQN("tenant~" + entry.getKey().trim());
				option.setValue(entry.getValue().trim());
				variationOptions.add(option);
			}
			product.setVariationOptions(variationOptions);
		}
		return product;
	}

	// OPTION BUILDER
	public List<CatalogAdminsProductOption> buildOptions(List<CatalogAdminsProduct> variants) {
		Map<String, Set<String>> optionMap = new LinkedHashMap<>();
		for (CatalogAdminsProduct variant : variants) {
			if (variant.getVariationOptions() == null)
				continue;
			variant.getVariationOptions().forEach(v -> {
				if (v.getValue() == null)
					return;
				String value = String.valueOf(v.getValue());
				optionMap.computeIfAbsent(v.getAttributeFQN(), k -> new LinkedHashSet<>()).add(value);
			});
		}

		List<CatalogAdminsProductOption> options = new ArrayList<>();
		optionMap.forEach((attr, values) -> {
			CatalogAdminsProductOption option = new CatalogAdminsProductOption();
			option.setAttributeFQN(attr);
			List<CatalogAdminsProductOptionValue> vals = new ArrayList<>();
			for (String v : values) {
				CatalogAdminsProductOptionValue val = new CatalogAdminsProductOptionValue();
				val.setValue(v);
				vals.add(val);
			}
			option.setValues(vals);
			options.add(option);
		});
		return options;
	}

	// VARIATION OPTIONS BUILDER
	public List<ProductVariationOption> buildVariationOptions(List<CatalogAdminsProduct> variants) {
		List<ProductVariationOption> list = new ArrayList<>();
		for (CatalogAdminsProduct variant : variants) {
			if (variant.getVariationOptions() == null)
				continue;
			list.addAll(variant.getVariationOptions());
		}
		return list;
	}

	// ================= HELPERS =================

	private CommerceRuntimeMeasurement parseMeasurement(String value, String unit) {
		if (!notEmpty(value))
			return null;
		CommerceRuntimeMeasurement m = new CommerceRuntimeMeasurement();
		m.setValue(Double.parseDouble(value));
		if (notEmpty(unit)) {
			m.setUnit(unit);
		}
		return m;
	}
	
	private Boolean parseBool(String v) {
        return v != null && v.equalsIgnoreCase("true");
    }

	private boolean notEmpty(String value) {
		return value != null && !value.trim().isEmpty();
	}
}