package oscar.answers.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class BaseDoncallControllerTest {

    private BaseDoncallController baseDoncallController;
    private static Logger logger;
    private long testBeforeTime;
    private String testName;

    public BaseDoncallControllerTest(BaseDoncallController baseDoncallController) {
        this.baseDoncallController = baseDoncallController;
        logger = LoggerFactory.getLogger(BaseDoncallControllerTest.class);
    }

    @BeforeEach
    public void setStartTime() {
        testBeforeTime = System.currentTimeMillis();
    }

    @AfterEach
    public void getRunningTime() {
        long testAfterTime = System.currentTimeMillis();
        logger.info(testName + ": " + (testAfterTime - testBeforeTime) + " ms");
        baseDoncallController.clearCounts();
    }

    /**
     * DoncallControllerV2의 donCallOneHundredPerMinute 함수가 call limit을 제대로 수행하는지 테스트한다.
     * 1분당 100회의 요청까지만 허용하는 함수에 120회의 요청을 보낸다.
     * response http status가 200번대인 것의 개수를 count한다.
     * count==100 -> test success, count>100 -> test fail
     * @throws Exception
     */
    public void testCallCounts(int callLimit, int targetCallCount, String testName, BaseDoncallController baseDoncallController) throws Exception {
        setTestName(testName);
        ExecutorService executor = Executors.newFixedThreadPool(3);
        List<Future<ResponseEntity<?>>> futures = new ArrayList<>();

        for (int i = 0; i < targetCallCount; i++)
            futures.add(executor.submit(baseDoncallController::donCallOneHundredPerMinute));

        /*
        for (Future<ResponseEntity<?>> future : futures)
            logger.debug(String.valueOf(future.get()));
        */

        int successCallCount = (int) futures.stream()
                .filter(future -> {
                    try {
                        return future.get().getStatusCode().is2xxSuccessful();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return false;
                })
                .count();

        executor.awaitTermination(2, TimeUnit.SECONDS);
        executor.shutdown();

        assertThat(callLimit).isEqualTo(successCallCount);
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }
}
