package com.ign.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.ign.batch.ProductCsvReader;
import com.ign.batch.ProductProcessor;
import com.ign.batch.ProductWriter;
import com.ign.dto.ProductCsvDto;
import com.kibocommerce.sdk.catalogadministration.models.CatalogAdminsProduct;

@Configuration
public class BatchConfig {

    @Bean
    public Step productStep(JobRepository jobRepository,
                            PlatformTransactionManager transactionManager,
                            ProductCsvReader reader,
                            ProductProcessor processor,
                            ProductWriter writer) {

        return new StepBuilder("productStep", jobRepository)
                .<ProductCsvDto, CatalogAdminsProduct>chunk(50, transactionManager)
                .reader(reader.reader())
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Job importJob(JobRepository jobRepository,
                         Step productStep) {

        return new JobBuilder("importJob", jobRepository)
                .start(productStep)
                .build();
    }
}