package com.ign.service;

import java.io.File;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BatchScheduler {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job productImportJob;

    @Scheduled(fixedDelay = 30000)
    public void runJob() throws Exception {

        File folder = new File("data/import");

        if (folder.exists() && folder.listFiles().length > 0) {

            JobParameters params = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(productImportJob, params);

            System.out.println("Batch Job Started...");
        }
    }
}