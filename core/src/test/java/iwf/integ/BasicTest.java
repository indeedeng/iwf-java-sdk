package iwf.integ;

import iwf.spring.TestSingletonWorkerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BasicTest {

    @BeforeEach
    public void setup() {
        TestSingletonWorkerService.startWorkerIfNotUp();
    }

    @Test
    public void testBasicWorkflow() throws InterruptedException {
        Thread.sleep(60 * 1000);
    }
}
