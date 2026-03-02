package com.ign.dto;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class ProductCsvDto {

	private String rowType;
	
    private String operation;
    private String attributeLabel;
    private String administrationName;
    private String attributeCode;
    private Map<String, String> variantAttributes = new HashMap<>();
    private String inputType;
    private String dataType;
    private String isOption;
    private String isProperty;
    private String values;
    private String SearchableInStorefront;
    
 // Attribute controls
    private String namespace;
    private String valueType;
    private String isExtra;
    private String availableForOrderRouting;
    
 // Validation controls
    private String minStringLength;
    private String maxStringLength;

    // Search settings
    private String searchableInAdmin;
    private String searchDisplayValue;
    private String allowFilteringAndSortingInStorefront;

    private String productTypeName;
    private String supportedUsageTypes;
    private String name;
    private String goodsType;
    private String options;
    private String extras;
    private String properties;
    private String variantProperties;

    private String productTypeId;
    private String productCode;
    private String productName;
    private String upc;
    private String productShortDescription;
    private String productFullDescription;
    private String localeCode;
    private String isActive;

    private String price;
    private String salePrice;
    private String isoCurrencyCode;

    private String productUsage;
    private String masterCatalogId;
    private String catalogId;
    private String categoryId;

    private String manageStock;
    private String outOfStockBehavior;
    private String isTaxable;
    private String isRecurring;

    private String packageHeight;
    private String packageWidth;
    private String packageLength;
    private String packageWeight;

    private String heightUnit;
    private String widthUnit;
    private String lengthUnit;
    private String weightUnit;

    private String hasConfigurableOptions;
    private String hasStandAloneOptions;

    private String fulfillmentTypes;
    private String rating;
    private String availability;

    private String parentProductCode;
    private String sizeoption;
    private String coloroptions;
    private String cost;

    private String enable;
    private String seoUrl;
    private String metaTitle;
    private String metaDescription;
    private String publishedState;

    private String giftWrapEnabled;
    private String giftWrapDeltaPrice;
    private String giftWrapCurrency;
}