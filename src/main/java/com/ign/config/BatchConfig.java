package com.ign.config;

import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.ign.batch.AttributeProcessor;
import com.ign.batch.AttributeWriter;
import com.ign.batch.ProductCsvReader;
import com.ign.batch.ProductProcessor;
import com.ign.batch.ProductTypeProcessor;
import com.ign.batch.ProductTypeWriter;
import com.ign.batch.ProductWriter;
import com.ign.dto.ProductBatchWrapper;
import com.ign.dto.ProductCsvDto;
import com.ign.listener.FileMoveListener;
import com.kibocommerce.sdk.catalogadministration.models.CatalogAdminsAttribute;
import com.kibocommerce.sdk.catalogadministration.models.ProductType;

@Configuration
public class BatchConfig {

    private final FileMoveListener fileMoveListener;

    public BatchConfig( FileMoveListener fileMoveListener) {
        this.fileMoveListener = fileMoveListener;
    }

    // STEP 1 – ATTRIBUTE
    @Bean
    public Step attributeStep(JobRepository jobRepository,
                              PlatformTransactionManager transactionManager,
                              ProductCsvReader reader,
                              AttributeProcessor attributeProcessor,
                              AttributeWriter attributeWriter) {

        return new StepBuilder("attributeStep", jobRepository)
                .<ProductCsvDto, List<CatalogAdminsAttribute>>chunk(50, transactionManager)
                .reader(reader.reader(null))
                .processor(attributeProcessor)
                .writer(attributeWriter)
                .build();
    }

    // STEP 2 – PRODUCT TYPE
    @Bean
    public Step productTypeStep(JobRepository jobRepository,
                                PlatformTransactionManager transactionManager,
                                ProductCsvReader reader,
                                ProductTypeProcessor productTypeProcessor,
                                ProductTypeWriter productTypeWriter) {

        return new StepBuilder("productTypeStep", jobRepository)
                .<ProductCsvDto, ProductType>chunk(50, transactionManager)
                .reader(reader.reader(null))
                .processor(productTypeProcessor)
                .writer(productTypeWriter)
                .build();
    }

    // STEP 3 – PRODUCT
    @Bean
    public Step productStep(JobRepository jobRepository,
                            PlatformTransactionManager transactionManager,
                            ProductCsvReader reader,
                            ProductProcessor productProcessor,
                            ProductWriter productWriter) {

        return new StepBuilder("productStep", jobRepository)
                .<ProductCsvDto, ProductBatchWrapper>chunk(200, transactionManager)
                .reader(reader.reader(null))
                .processor(productProcessor)
                .writer(productWriter)
                .faultTolerant()
                .retryLimit(3)
                .retry(Exception.class)
                .keyGenerator(item -> ((ProductCsvDto) item).getProductCode())
                .skipLimit(10)
                .skip(Exception.class)
                .listener(fileMoveListener)
                .build();
    }

    // JOB FLOW
    @Bean
    public Job importJob(JobRepository jobRepository,
                         Step attributeStep,
                         Step productTypeStep,
                         Step productStep) {

        return new JobBuilder("importJob", jobRepository)
                .start(attributeStep)
                .next(productTypeStep)
                .next(productStep)
                .build();
    }
}