package com.ign.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ign.dto.ProductCsvDto;
import com.kibocommerce.sdk.catalogadministration.models.AttributeSearchSettings;
import com.kibocommerce.sdk.catalogadministration.models.CatalogAdminsAttribute;
import com.kibocommerce.sdk.catalogadministration.models.CatalogAdminsAttributeVocabularyValue;
import com.kibocommerce.sdk.catalogadministration.models.CatalogAdminsAttributeLocalizedContent;

@Component
public class AttributeMapper {

    public CatalogAdminsAttribute map(ProductCsvDto dto) {

        CatalogAdminsAttribute attribute = new CatalogAdminsAttribute();

        // ================= BASIC =================
        attribute.setAdminName(dto.getAdministrationName());
        attribute.setAttributeCode(dto.getAttributeCode());

        String namespace = dto.getNamespace() != null ?
                dto.getNamespace() : "tenant";

        attribute.setNamespace(namespace);
        attribute.setAttributeFQN(namespace + "~" + dto.getAttributeCode());

        if (dto.getMasterCatalogId() != null) {
            attribute.setMasterCatalogId(
                    Integer.parseInt(dto.getMasterCatalogId()));
        }

        attribute.setInputType(dto.getInputType());
        attribute.setDataType(dto.getDataType());

        attribute.setValueType(
                dto.getValueType() != null ?
                        dto.getValueType() : "Predefined"
        );

        Boolean isOption = parseBoolean(dto.getIsOption());
        Boolean isExtra = parseBoolean(dto.getIsExtra());
        Boolean isProperty = parseBoolean(dto.getIsProperty());

        attribute.setIsOption(isOption != null ? isOption : false);
        attribute.setIsExtra(isExtra != null ? isExtra : false);
        attribute.setIsProperty(isProperty != null ? isProperty : false);

        // ================= CONTENT =================
        CatalogAdminsAttributeLocalizedContent content =
                new CatalogAdminsAttributeLocalizedContent();

        content.setLocaleCode(dto.getLocaleCode());
        content.setName(dto.getAttributeLabel());
        content.setDescription("Created via batch");

        attribute.setContent(content);
        

        // ================= VOCABULARY VALUES =================
        if (dto.getValues() != null && !dto.getValues().isEmpty()) {

            String[] valuesArray = dto.getValues().split(",");
            List<CatalogAdminsAttributeVocabularyValue> vocabularyList =
                    new ArrayList<>();

            for (int i = 0; i < valuesArray.length; i++) {

                String value = valuesArray[i].trim();

                CatalogAdminsAttributeVocabularyValue vocab =
                        new CatalogAdminsAttributeVocabularyValue();

                vocab.setValueSequence(i + 1);
                vocab.setValue(value);
                vocab.setDisplayOrder(i + 1);

                vocabularyList.add(vocab);
            }

            attribute.setVocabularyValues(vocabularyList);
        }
        
        // ================= SEARCH SETTINGS =================
        AttributeSearchSettings searchSettings =
                new AttributeSearchSettings();

        searchSettings.setSearchableInStorefront(
                parseBoolean(dto.getSearchableInStorefront()));

        searchSettings.setSearchableInAdmin(
                parseBoolean(dto.getSearchableInAdmin()));

        searchSettings.setSearchDisplayValue(
                parseBoolean(dto.getSearchDisplayValue()));

        searchSettings.setAllowFilteringAndSortingInStorefront(
                parseBoolean(dto.getAllowFilteringAndSortingInStorefront()));

        attribute.setSearchSettings(searchSettings);

        return attribute;
    }

    private Boolean parseBoolean(String value) {
        return value != null && Boolean.parseBoolean(value);
    }
}