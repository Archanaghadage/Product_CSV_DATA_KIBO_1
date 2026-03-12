package com.ign.batch;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.ign.dto.ProductCsvDto;

@Configuration
public class ProductCsvReader {

	@Bean
	@StepScope
	public SynchronizedItemStreamReader<ProductCsvDto> reader(@Value("#{jobParameters['filePath']}") String filePath) {

		System.out.println("Reading file: " + filePath);

		FlatFileItemReader<ProductCsvDto> flatReader = new FlatFileItemReader<>();
		flatReader.setResource(new FileSystemResource(filePath));
		flatReader.setLinesToSkip(1);

		DefaultLineMapper<ProductCsvDto> lineMapper = new DefaultLineMapper<>();

		DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
		tokenizer.setDelimiter(",");

//		tokenizer.setNames("operation", "productTypeName", "productUsage", "productCode", "parentProductCode",
//				"productName", "localeCode", "isActive", "price", "salePrice", "isoCurrencyCode", "masterCatalogId",
//				"catalogId", "categoryId", "manageStock", "outOfStockBehavior", "sizeoption", "coloroptions", "cost",
//				"packageWidth", "packageLength", "packageHeight", "packageWeight", "heightUnit", "widthUnit",
//				"lengthUnit", "weightUnit", "hasConfigurableOptions", "hasStandAloneOptions", "fulfillmentTypes",
//				"rating", "seoUrl", "publishedState");
		
		tokenizer.setNames("operation", "attributeLabel", "administrationName", "attributeCode", "inputType",
		"dataType", "isOption", "isProperty", "values", "searchableInStorefront", "productTypeName",
		"supportedUsageTypes", "productCode", "parentProductCode", "productName", "upc",
		"productShortDescription", "productFullDescription", "localeCode", "isActive", "price", "salePrice",
		"isoCurrencyCode", "productUsage", "masterCatalogId", "catalogId", "categoryId", "manageStock",
		"outOfStockBehavior", "sizeoption", "coloroptions", "cost", "packageWidth", "packageLength", "packageHeight",
		"packageWeight", "heightUnit", "widthUnit", "lengthUnit", "weightUnit", "hasConfigurableOptions",
		"hasStandAloneOptions", "fulfillmentTypes", "rating", "giftwrap", "seoUrl", "metaTitle", "metaDescription",
		"publishedState");

		BeanWrapperFieldSetMapper<ProductCsvDto> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
		fieldSetMapper.setTargetType(ProductCsvDto.class);

		lineMapper.setLineTokenizer(tokenizer);
		lineMapper.setFieldSetMapper(fieldSetMapper);

		flatReader.setLineMapper(lineMapper);

		SynchronizedItemStreamReader<ProductCsvDto> syncReader = new SynchronizedItemStreamReader<>();

		syncReader.setDelegate(flatReader);

		return syncReader;
	}
}