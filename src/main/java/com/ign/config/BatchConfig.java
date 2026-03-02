package com.ign.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.ign.batch.ProductCsvReader;
import com.ign.batch.ProductProcessor;
import com.ign.batch.ProductWriter;
import com.ign.dto.ProductCsvDto;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

	@Bean
	public Job importJob(JobRepository jobRepository, Step Steps) {

		return new JobBuilder("importJob")
				.repository(jobRepository)
				.start(Steps).build();
	}

	@Bean
	public Step Steps(JobRepository jobRepository, PlatformTransactionManager transactionManager,
			ProductCsvReader reader, ProductProcessor processor, ProductWriter writer) {

		return new StepBuilder("Steps")
				.repository(jobRepository)
				.<ProductCsvDto, ProductCsvDto>chunk(10)
				.reader(reader.reader())
				.processor(processor)
				.writer(writer)
				.transactionManager(transactionManager)
				.build();
	}

}