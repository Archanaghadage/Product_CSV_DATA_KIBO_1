package com.ign.dto;

import com.kibocommerce.sdk.catalogadministration.models.CatalogAdminsProduct;

public class ProductBatchWrapper {

    private String operation;
    private CatalogAdminsProduct product;
    
    public ProductBatchWrapper(String operation, CatalogAdminsProduct product) {
        this.operation = operation;
        this.product = product;
    }


    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public CatalogAdminsProduct getProduct() {
        return product;
    }

    public void setProduct(CatalogAdminsProduct product) {
        this.product = product;
    }
}