package com.ign.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.kibocommerce.sdk.catalogadministration.api.ProductAttributesApi;
import com.kibocommerce.sdk.catalogadministration.api.ProductTypesApi;
import com.kibocommerce.sdk.catalogadministration.api.ProductVariationsApi;
import com.kibocommerce.sdk.catalogadministration.api.ProductsApi;
import com.kibocommerce.sdk.common.ApiCredentials;
import com.kibocommerce.sdk.common.KiboConfiguration;

@Configuration
public class KiboConfig {

    @Bean
    public KiboConfiguration getConfiguration() {
		KiboConfiguration config = KiboConfiguration.builder().withTenantId(52669).withSiteId(77850)
				.withCredentials(ApiCredentials.builder().setClientId("IgDev.0110.1.0.0.Release")
						.setClientSecret("aaad2c5cea684e868ae5788a0a76d087").build())
				.withTenantHost("t52669.sandbox.mozu.com").withHomeHost("home.mozu.com").build();

		return config;

	}
    
    @Bean
    public ProductAttributesApi productAttributesApi(KiboConfiguration config) {
        return new ProductAttributesApi(config);
    }
    
    @Bean
    public ProductTypesApi productTypesApi(KiboConfiguration config) {
        return new ProductTypesApi(config);
    }
    
    @Bean
    public ProductsApi productsApi(KiboConfiguration config) {
        return new ProductsApi(config);
    }
    
    
    @Bean
    public ProductVariationsApi productVariationsApi(KiboConfiguration config) {
        return new ProductVariationsApi(config);
    }

}