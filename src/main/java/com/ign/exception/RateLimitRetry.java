package com.ign.exception;

import com.kibocommerce.sdk.common.ApiException;

public class RateLimitRetry {

	
	 private static final int MAX_RETRIES = 5;
	    private static final long INITIAL_DELAY = 1000; // 1 second

	    public static <T> T execute(ApiSupplier<T> supplier) {

	        int attempt = 0;
	        long delay = INITIAL_DELAY;

	        while (true) {
	            try {
	                return supplier.get();
	            } catch (ApiException e) {

	                if (e.getCode() == 429 && attempt < MAX_RETRIES) {

	                    System.out.println("⚠ Rate limit hit. Retrying in " + delay + " ms");

	                    try {
	                        Thread.sleep(delay);
	                    } catch (InterruptedException ignored) {}

	                    delay *= 2; // exponential backoff
	                    attempt++;

	                } else {
	                    throw new RuntimeException("API failed after retries", e);
	                }
	            }
	        }
	    }

	    @FunctionalInterface
	    public interface ApiSupplier<T> {
	        T get() throws ApiException;
	    }
}
