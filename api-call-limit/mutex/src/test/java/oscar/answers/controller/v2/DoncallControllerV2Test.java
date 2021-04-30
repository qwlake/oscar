package oscar.answers.controller.v2;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import oscar.answers.controller.BaseDoncallControllerTest;

@SpringBootTest
public class DoncallControllerV2Test extends BaseDoncallControllerTest {

    private DoncallControllerV2 doncallControllerV2;
    private int callLimit;
    private int targetCallCount;
    private Logger logger;

    @Autowired
    public DoncallControllerV2Test(DoncallControllerV2 doncallControllerV2) {
        super(doncallControllerV2);
        this.doncallControllerV2 = doncallControllerV2;
        callLimit = this.doncallControllerV2.getCallLimit();
        targetCallCount = (int) (callLimit * 1.2);
        logger = LoggerFactory.getLogger(DoncallControllerV2Test.class);
    }

    @Test
    public void testCallCounts() throws Exception {
        super.testCallCounts(callLimit, targetCallCount, "testCallCounts V2", doncallControllerV2);
    }
}