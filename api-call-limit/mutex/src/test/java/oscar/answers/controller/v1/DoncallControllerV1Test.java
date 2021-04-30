package oscar.answers.controller.v1;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import oscar.answers.controller.BaseDoncallControllerTest;

@SpringBootTest
public class DoncallControllerV1Test extends BaseDoncallControllerTest {

    private DoncallControllerV1 doncallControllerV1;
    private int callLimit;
    private int targetCallCount;
    private Logger logger;

    @Autowired
    public DoncallControllerV1Test(DoncallControllerV1 doncallControllerV1) {
        super(doncallControllerV1);
        this.doncallControllerV1 = doncallControllerV1;
        callLimit = this.doncallControllerV1.getCallLimit();
        targetCallCount = (int) (callLimit * 1.2);
        logger = LoggerFactory.getLogger(DoncallControllerV1Test.class);
    }

    @Test
    public void testCallCounts() throws Exception {
        super.testCallCounts(callLimit, targetCallCount, "testCallCounts V1", doncallControllerV1);
    }
}