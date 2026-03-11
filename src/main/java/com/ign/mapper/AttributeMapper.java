package com.ign.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ign.dto.ProductCsvDto;
import com.kibocommerce.sdk.catalogadministration.models.CatalogAdminsAttribute;
import com.kibocommerce.sdk.catalogadministration.models.CatalogAdminsAttributeLocalizedContent;
import com.kibocommerce.sdk.catalogadministration.models.CatalogAdminsAttributeVocabularyValue;

@Component
public class AttributeMapper {

	public List<CatalogAdminsAttribute> map(ProductCsvDto dto) {

		List<CatalogAdminsAttribute> attributes = new ArrayList<>();

		if (dto.getAttributeCode() == null || dto.getAttributeCode().isEmpty()) {
			return attributes;
		}

		String[] attributeCodes = dto.getAttributeCode().split("\\|");

		for (String code : attributeCodes) {

			code = code.trim();

			CatalogAdminsAttribute attribute = new CatalogAdminsAttribute();

			// Basic fields
			attribute.setAdminName(code);
			attribute.setAttributeCode(code);

			String namespace = dto.getNamespace() != null ? dto.getNamespace() : "tenant";
			attribute.setNamespace(namespace);
			attribute.setAttributeFQN(namespace + "~" + code);

			if (dto.getMasterCatalogId() != null) {
				attribute.setMasterCatalogId(Integer.parseInt(dto.getMasterCatalogId()));
			}

			attribute.setInputType(dto.getInputType());
			attribute.setDataType(dto.getDataType());

			// Required when attribute has predefined values
			attribute.setValueType("Predefined");

			/*
			 * USAGE TYPE (from CSV)
			 */
			attribute.setIsOption(Boolean.parseBoolean(dto.getIsOption()));
			attribute.setIsProperty(Boolean.parseBoolean(dto.getIsProperty()));
			attribute.setIsExtra(false); // not present in your CSV

			/*
			 * VOCABULARY VALUES
			 */
			if (dto.getValues() != null && !dto.getValues().isBlank()) {

				String[] valuesArray = dto.getValues().split(",");

				List<CatalogAdminsAttributeVocabularyValue> vocabularyList = new ArrayList<>();

				for (int i = 0; i < valuesArray.length; i++) {

					String value = valuesArray[i].trim();

					CatalogAdminsAttributeVocabularyValue vocab = new CatalogAdminsAttributeVocabularyValue();

					vocab.setValue(value);
					vocab.valueSequence(i + 1);

					vocabularyList.add(vocab);
				}

				attribute.setVocabularyValues(vocabularyList);
			}

			/*
			 * LOCALIZED CONTENT
			 */
			CatalogAdminsAttributeLocalizedContent content = new CatalogAdminsAttributeLocalizedContent();
			content.setLocaleCode(dto.getLocaleCode());
			content.setName(code);
			content.setDescription("Created via batch");

			attribute.setContent(content);

			attributes.add(attribute);
		}

		return attributes;
	}
}