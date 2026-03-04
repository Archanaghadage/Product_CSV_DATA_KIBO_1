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
			attribute.setValueType("Predefined");
			System.out.println("Values --> " + dto.getValues());
			attribute.setIsOption(true);
			attribute.setIsExtra(false);
			attribute.setIsProperty(false);

			if (dto.getValues() != null && !dto.getValues().isBlank()) {
				String[] valuesArray = dto.getValues().split(",");
				List<CatalogAdminsAttributeVocabularyValue> vocabularyList = new ArrayList<>();

				for (int i = 0; i < valuesArray.length; i++) {
					String value = valuesArray[i].trim();
					CatalogAdminsAttributeVocabularyValue vocab = new CatalogAdminsAttributeVocabularyValue();
					vocab.setValue(value); // REQUIRED
					vocab.valueSequence(i + 1); // CORRECT FIELD
					vocabularyList.add(vocab);
				}
				attribute.setVocabularyValues(vocabularyList);
				System.out.println("Vocabulary added -> " + vocabularyList.size());
			}

			// CONTENT
			CatalogAdminsAttributeLocalizedContent content = new CatalogAdminsAttributeLocalizedContent();
			content.setLocaleCode(dto.getLocaleCode());
			content.setName(code);
			content.setDescription("Created via batch");
			attribute.setContent(content);
			attributes.add(attribute);
		}

		return attributes;
	}

	private Boolean parseBoolean(String value) {
		return value != null && Boolean.parseBoolean(value);
	}
}