package iwf.spring;

public class TestSingletonWorkerService {
    private static TestWorker testWorker;

    public static void startWorkerIfNotUp() {
        if (testWorker == null) {
            testWorker = new TestWorker();
            testWorker.start();
        }
    }
}
