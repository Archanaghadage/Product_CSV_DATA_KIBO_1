package com.ign.batch;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.ign.dto.ProductCsvDto;
import com.ign.mapper.AttributeMapper;
import com.kibocommerce.sdk.catalogadministration.models.CatalogAdminsAttribute;

@Component
public class AttributeProcessor
        implements ItemProcessor<ProductCsvDto, List<CatalogAdminsAttribute>> {

    private final AttributeMapper mapper;

    private List<CatalogAdminsAttribute> buffer = new ArrayList<>();
    private int index = 0;

    public AttributeProcessor(AttributeMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<CatalogAdminsAttribute> process(ProductCsvDto dto) {

        List<CatalogAdminsAttribute> attributes = new ArrayList<>();

        String[] codes = dto.getAttributeCode() != null
                ? dto.getAttributeCode().split("\\|")
                : new String[0];

        String[] labels = dto.getAttributeLabel() != null
                ? dto.getAttributeLabel().split("\\|")
                : new String[0];

        String[] valuesGroups = dto.getValues() != null
                ? dto.getValues().split("\\|")
                : new String[0];

        for (int i = 0; i < codes.length; i++) {

            ProductCsvDto newDto = new ProductCsvDto();
            BeanUtils.copyProperties(dto, newDto);

            newDto.setAttributeCode(codes[i].trim());
            newDto.setAttributeLabel(labels.length > i ? labels[i].trim() : codes[i].trim());
            newDto.setValues(valuesGroups.length > i ? valuesGroups[i].trim() : null);

            attributes.addAll(mapper.map(newDto));
        }

        return attributes;
    }
}
