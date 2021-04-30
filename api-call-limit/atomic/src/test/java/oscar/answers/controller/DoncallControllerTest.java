package oscar.answers.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class DoncallControllerTest {

    private DoncallController doncallController;
    private int callLimit;
    private int targetCallCount;
    private Logger logger;
    private long testBeforeTime;

    @Autowired
    public DoncallControllerTest(DoncallController doncallController) {
        this.doncallController = doncallController;
        callLimit = this.doncallController.getCallLimit();
        targetCallCount = (int) (callLimit * 1.2);
        logger = LoggerFactory.getLogger(DoncallControllerTest.class);
    }

    @BeforeEach
    public void setStartTime() {
        testBeforeTime = System.currentTimeMillis();
    }

    @AfterEach
    public void getRunningTime() {
        long testAfterTime = System.currentTimeMillis();
        logger.info("testCallCounts: " + (testAfterTime - testBeforeTime) + " ms");
    }

    /**
     * DoncallControllerV2의 donCallOneHundredPerMinute 함수가 call limit을 제대로 수행하는지 테스트한다.
     * 1분당 100회의 요청까지만 허용하는 함수에 120회의 요청을 보낸다.
     * response http status가 200번대인 것의 개수를 count한다.
     * count==100 -> test success, count>100 -> test fail
     * @throws Exception
     */
    @Test
    public void testCallCounts() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        List<Future<ResponseEntity<?>>> futures = new ArrayList<>();

        for (int i = 0; i < targetCallCount; i++)
            futures.add(executor.submit(doncallController::donCallOneHundredPerMinute));

//        for (Future<ResponseEntity<?>> future : futures)
//            logger.info(String.valueOf(future.get()));

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
}