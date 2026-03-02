package com.ign;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProductAddCsvApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductAddCsvApplication.class, args);
		System.err.println("Hello, KIBO! CSV -> Product");
	}
}
