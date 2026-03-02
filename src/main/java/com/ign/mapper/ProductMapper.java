package com.ign.mapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.ign.dto.ProductCsvDto;
import com.kibocommerce.sdk.catalogadministration.models.CatalogAdminsProduct;
import com.kibocommerce.sdk.catalogadministration.models.CatalogAdminsProductOption;
import com.kibocommerce.sdk.catalogadministration.models.CatalogAdminsProductPrice;
import com.kibocommerce.sdk.catalogadministration.models.CommerceRuntimeMeasurement;
import com.kibocommerce.sdk.catalogadministration.models.ProductInCatalogInfo;
import com.kibocommerce.sdk.catalogadministration.models.ProductLocalizedContent;

@Component
public class ProductMapper {

	public static CatalogAdminsProduct map(ProductCsvDto row) {

		CatalogAdminsProduct product = new CatalogAdminsProduct();

		// ================= BASIC =================
		product.setProductCode(row.getProductCode());
		product.setProductUsage(row.getProductUsage());
		if (notEmpty(row.getProductTypeId())) {
			product.setProductTypeId(Integer.parseInt(row.getProductTypeId()));
		}
		System.err.println("ID --> " + row.getProductTypeId());
		product.setUpc(row.getUpc());
		if (row.getMasterCatalogId() != null && !row.getMasterCatalogId().isBlank()) {
			product.setMasterCatalogId(Integer.parseInt(row.getMasterCatalogId()));
		}

		boolean isVariant = notEmpty(row.getParentProductCode());

		// ================= USAGE LOGIC =================
		if ("Standard".equalsIgnoreCase(row.getProductUsage())) {

			product.setIsVariation(false);
			product.setHasConfigurableOptions(false);
			product.setHasStandAloneOptions(false);

		} else if ("Configurable".equalsIgnoreCase(row.getProductUsage())) {

			if (isVariant) {

				// ================= VARIANT =================
				product.setIsVariation(true);
				product.setBaseProductCode(row.getParentProductCode());
				product.setHasConfigurableOptions(false);
				product.setHasStandAloneOptions(false);

				//mapVariantOptions(product, row);

			} else {

				// ================= CONFIGURABLE PARENT =================
				product.setIsVariation(false);
				product.setHasConfigurableOptions(true);
				product.setHasStandAloneOptions(false);

				mapParentOptions(product, row);
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
			price.setSalePrice(notEmpty(row.getSalePrice()) ? Double.parseDouble(row.getSalePrice()) : null);
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

		return product;
	}

	// =====================================================
	// PARENT OPTIONS (CatalogAdminsProductOption)
	// =====================================================
	private static void mapParentOptions(CatalogAdminsProduct product, ProductCsvDto row) {

		if (!notEmpty(row.getAttributeCode()) || !notEmpty(row.getValues()))
			return;

		String[] attributeCodes = row.getAttributeCode().split("\\|");
		String[] attributeValues = row.getValues().split("\\|");

		List<CatalogAdminsProductOption> options = new ArrayList<>();

		for (int i = 0; i < attributeCodes.length; i++) {

			String attrCode = attributeCodes[i].trim();
			String valuesPart = i < attributeValues.length ? attributeValues[i] : "";

			CatalogAdminsProductOption option = new CatalogAdminsProductOption();
			option.setAttributeFQN("tenant~" + attrCode);
			option.setIsProductImageGroupSelector(false);

			System.err.println("option --> "+option);
			// Split values dynamically
			if (notEmpty(valuesPart)) {

				List<String> values = Arrays.stream(valuesPart.split(",")).map(String::trim).toList();

				List<com.kibocommerce.sdk.catalogadministration.models.CatalogAdminsProductOptionValue> optionValues = values
						.stream().map(val -> {
							var v = new com.kibocommerce.sdk.catalogadministration.models.CatalogAdminsProductOptionValue();
							v.setValue(val);
							return v;
						}).toList();

				option.setValues(optionValues);
			}

			options.add(option);
		}

		product.setOptions(options);
		
		System.out.println("options in Mapper ==> "+options);
	}
//	// =====================================================
//	// VARIANT OPTIONS (ProductVariationOption)
//	// =====================================================
//	private static void mapVariantOptions(CatalogAdminsProduct product, ProductCsvDto row) {
//
//		Map<String, String> attributes = row.getVariantAttributes();
//
//		if (attributes == null || attributes.isEmpty())
//			return;
//
//		List<ProductVariationOption> variationOptions = new ArrayList<>();
//		StringBuilder variationKey = new StringBuilder();
//
//		attributes.forEach((key, value) -> {
//
//			if (!notEmpty(value))
//				return;
//
//			String attributeFQN = "tenant~" + key.trim();
//
//			ProductVariationOption option = new ProductVariationOption();
//			option.setAttributeFQN(attributeFQN);
//			option.setValue(value.trim());
//
//			variationOptions.add(option);
//
//			if (variationKey.length() > 0)
//				variationKey.append("|");
//
//			variationKey.append(attributeFQN).append(":").append(value.trim());
//		});
//
//		product.setVariationOptions(variationOptions);
//		product.setVariationKey(variationKey.toString());
//		System.out.println("variationOptions in Mapper ==> "+variationOptions);
//	}

	// =====================================================
	// HELPERS
	// =====================================================
	private static CommerceRuntimeMeasurement parseMeasurement(String value, String unit) {

		if (!notEmpty(value))
			return null;

		CommerceRuntimeMeasurement measurement = new CommerceRuntimeMeasurement();
		measurement.setValue(Double.parseDouble(value));

		if (notEmpty(unit)) {
			measurement.setUnit(unit);
		}

		return measurement;
	}

	private static boolean notEmpty(String value) {
		return value != null && !value.trim().isEmpty();
	}
}