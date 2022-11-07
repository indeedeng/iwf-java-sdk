package io.github.cadenceoss.iwf.spring;

import org.springframework.boot.SpringApplication;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestWorker {
    
    ExecutorService executor = Executors.newSingleThreadExecutor();

    public void start() throws ExecutionException, InterruptedException {
        executor.submit(() -> {
            SpringApplication.run(SpringMainApplication.class);
        }).get();
    }

    public void stop() {
        executor.shutdown();
    }
}
