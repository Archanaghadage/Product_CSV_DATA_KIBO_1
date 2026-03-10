package com.ign.listener;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import jakarta.annotation.PostConstruct;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FileWatchService {

	private static final String INPUT_DIR = "data/import/";
	private static final String PROCESSING_DIR = "data/processing/";

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private Job importJob;

	@PostConstruct
	public void watchFolder() {

		new Thread(() -> {
			try {

				WatchService watchService = FileSystems.getDefault().newWatchService();
				Path inputPath = Paths.get(INPUT_DIR);
				inputPath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
				System.out.println("Watching folder: " + INPUT_DIR);

				while (true) {

					WatchKey key = watchService.take();

					for (WatchEvent<?> event : key.pollEvents()) {

						Path newFile = inputPath.resolve((Path) event.context());

						System.out.println("New file detected: " + newFile);

						// Wait until file copy completes
						Thread.sleep(2000);
						Path processingPath = Paths.get(PROCESSING_DIR + newFile.getFileName());
						// Move file to processing
						Files.move(newFile, processingPath, StandardCopyOption.REPLACE_EXISTING);
						System.out.println("Moved to processing: " + processingPath);
						// Start batch job
						JobParameters params = new JobParametersBuilder()
								.addString("filePath", processingPath.toString())
								.addString("fileName", processingPath.getFileName().toString())
								.addLong("time", System.currentTimeMillis()).toJobParameters();

						jobLauncher.run(importJob, params);
					}

					key.reset();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}).start();
	}
}