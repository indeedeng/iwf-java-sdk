package io.iworkflow.spring;

import java.util.concurrent.ExecutionException;

public class TestSingletonWorkerService {
    private static TestWorker testWorker;

    public synchronized static void startWorkerIfNotUp() throws ExecutionException, InterruptedException {
        if (testWorker == null) {
            testWorker = new TestWorker();
            testWorker.start();
        }
    }
}
