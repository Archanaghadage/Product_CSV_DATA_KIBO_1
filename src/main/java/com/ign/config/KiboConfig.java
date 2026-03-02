package com.ign.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

}