package com.ign.batch;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.ign.dto.ProductCsvDto;

@Component
public class ProductProcessor implements ItemProcessor<ProductCsvDto, ProductCsvDto> {

    @Override
    public ProductCsvDto process(ProductCsvDto item) {

        // Ignore empty productCode rows
        if (item.getProductCode() == null || item.getProductCode().isBlank()) {
            return null;
        }

        if ("Standard".equalsIgnoreCase(item.getProductUsage())) {

            item.setRowType("STANDARD");
        }
        else if ("Configurable".equalsIgnoreCase(item.getProductUsage())) {

            // ===============================
            // CONFIGURABLE PARENT
            // ===============================
            if (item.getParentProductCode() == null 
                    || item.getParentProductCode().isBlank()) {

                item.setRowType("CONFIG_PARENT");
            }
            // ===============================
            // VARIANT ROW
            // ===============================
            else {

                item.setRowType("VARIANT");

                Map<String, String> variantMap = new HashMap<>();

                if (item.getSizeoption() != null && !item.getSizeoption().isBlank()) {
                    variantMap.put("sizeoption", item.getSizeoption());
                }

                if (item.getColoroptions() != null && !item.getColoroptions().isBlank()) {
                    variantMap.put("coloroptions", item.getColoroptions());
                }

                item.setVariantAttributes(variantMap);
            }
        }

        return item;
    }
}