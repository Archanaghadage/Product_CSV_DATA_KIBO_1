package com.ign.config;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
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
import com.kibocommerce.sdk.catalogadministration.models.CatalogAdminsAttribute;
import com.kibocommerce.sdk.catalogadministration.models.ProductType;

@Configuration
public class BatchConfig {
	
	private final TaskExecutor taskExecutor;
	
	 public BatchConfig(TaskExecutor taskExecutor) {
	        this.taskExecutor = taskExecutor;
	    }

    // STEP 1 – ATTRIBUTE STEP
    @Bean
    public Step attributeStep(JobRepository jobRepository,
                              PlatformTransactionManager transactionManager,
                              ProductCsvReader reader,   // if same CSV
                              AttributeProcessor attributeProcessor,
                              AttributeWriter attributeWriter) {

        return new StepBuilder("attributeStep", jobRepository)
                .<ProductCsvDto, List<CatalogAdminsAttribute>>chunk(50, transactionManager)
                .reader(reader.reader())
                .processor(attributeProcessor)
                .writer(attributeWriter)
                .build();
    }

    // STEP 2 – PRODUCT TYPE STEP
    @Bean
    public Step productTypeStep(JobRepository jobRepository,
                                PlatformTransactionManager transactionManager,
                                ProductCsvReader reader,
                                ProductTypeProcessor productTypeProcessor,
                                ProductTypeWriter productTypeWriter) {

        return new StepBuilder("productTypeStep", jobRepository)
                .<ProductCsvDto, ProductType>chunk(50, transactionManager)
                .reader(reader.reader())
                .processor(productTypeProcessor)
                .writer(productTypeWriter)
                .build();
    }

    // STEP 3 – PRODUCT STEP
    @Bean
    public Step productStep(JobRepository jobRepository,
                            PlatformTransactionManager transactionManager,
                            ProductCsvReader reader,
                            ProductProcessor productProcessor,
                            ProductWriter productWriter) {

        return new StepBuilder("productStep", jobRepository)
                .<ProductCsvDto, ProductBatchWrapper>chunk(200, transactionManager)
                .reader(reader.reader())
                .processor(productProcessor)
                .writer(productWriter)
                // MULTITHREADING
                .taskExecutor(taskExecutor)
                .throttleLimit(3)
                .faultTolerant()
                .retryLimit(3)
                .retry(Exception.class)
                .keyGenerator(item -> ((ProductCsvDto)item).getProductCode())
                .skipLimit(10)
                .skip(Exception.class)
                .build();
    }

    // JOB FLOW ORDER
    @Bean
    public Job importJob(JobRepository jobRepository,
                         Step attributeStep,
                         Step productTypeStep,
                         Step productStep) {

        return new JobBuilder("importJob", jobRepository)
                .start(attributeStep)      // 1️⃣ Attributes
                .next(productTypeStep)     // 2️⃣ ProductTypes
                .next(productStep)         // 3️⃣ Products
                .build();
    }
}