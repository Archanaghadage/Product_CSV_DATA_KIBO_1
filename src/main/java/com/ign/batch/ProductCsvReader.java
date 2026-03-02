package com.ign.batch;

import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.ign.dto.ProductCsvDto;

@Configuration
public class ProductCsvReader {

	// READER
	@Bean
	public FlatFileItemReader<ProductCsvDto> reader() {

		FlatFileItemReader<ProductCsvDto> reader = new FlatFileItemReader<>();
		reader.setResource(new ClassPathResource("configurable_product.csv"));
		reader.setLinesToSkip(1);

		DefaultLineMapper<ProductCsvDto> lineMapper = new DefaultLineMapper<>();

		DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
		tokenizer.setDelimiter(",");
//		tokenizer.setNames("operation", "attributeLabel", "administrationName", "attributeCode", "inputType",
//				"dataType", "isOption", "isProperty", "values", "searchableInStorefront", "productTypeName",
//				"supportedUsageTypes", "productCode", "parentProductCode", "productName" , "upc", "productShortDescription",
//				"productFullDescription", "localeCode", "isActive", "price", "salePrice", "isoCurrencyCode",
//				"productUsage", "masterCatalogId", "catalogId", "categoryId", "manageStock", "outOfStockBehavior",
//				"sizeOption", "cost", "packageWidth", "packageLength", "packageWeight", "heightUnit", "widthUnit",
//				"lengthUnit", "weightUnit", "hasConfigurableOptions", "hasStandAloneOptions", "fulfillmentTypes",
//				"rating", "seoUrl", "metaTitle", "metaDescription", "publishedState");

		tokenizer.setNames("operation", "attributeLabel", "administrationName", "attributeCode", "inputType",
				"dataType", "isOption", "isProperty", "values", "searchableInStorefront", "productTypeName",
				"supportedUsageTypes", "productCode", "parentProductCode", "productName", "upc",
				"productShortDescription", "productFullDescription", "localeCode", "isActive", "price", "salePrice",
				"isoCurrencyCode", "productUsage", "masterCatalogId", "catalogId", "categoryId", "manageStock",
				"outOfStockBehavior", "sizeoption", "coloroptions", "cost", "packageWidth", "packageLength", "packageHeight",
				"packageWeight", "heightUnit", "widthUnit", "lengthUnit", "weightUnit", "hasConfigurableOptions",
				"hasStandAloneOptions", "fulfillmentTypes", "rating", "seoUrl", "metaTitle", "metaDescription",
				"publishedState");

		BeanWrapperFieldSetMapper<ProductCsvDto> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
		fieldSetMapper.setTargetType(ProductCsvDto.class);

		lineMapper.setLineTokenizer(tokenizer);
		lineMapper.setFieldSetMapper(fieldSetMapper);

		reader.setLineMapper(lineMapper);

		System.err.println("Reader CSV 1");
		System.out.println(reader);
		return reader;
	}
}