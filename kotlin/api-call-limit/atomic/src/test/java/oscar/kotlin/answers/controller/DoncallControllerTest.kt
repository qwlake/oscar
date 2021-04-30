package oscar.kotlin.answers.controller

import org.assertj.core.api.AssertionsForClassTypes
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.ResponseEntity
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

@SpringBootTest
class DoncallControllerTest @Autowired constructor(private val doncallController: DoncallController) {

    private val callLimit: Int = doncallController.getCallLimit()
    private val targetCallCount: Int = (callLimit * 1.2).toInt()
    private val logger: Logger = LoggerFactory.getLogger(DoncallControllerTest::class.java)
    private var testBeforeTime: Long = 0

    @BeforeEach
    fun setStartTime() {
        testBeforeTime = System.currentTimeMillis()
    }

    @AfterEach
    fun getRunningTime() {
        val testAfterTime = System.currentTimeMillis()
        logger.info("testCallCounts: " + (testAfterTime - testBeforeTime) + " ms")
    }

    /**
     * DoncallControllerV2의 donCallOneHundredPerMinute 함수가 call limit을 제대로 수행하는지 테스트한다.
     * 1분당 100회의 요청까지만 허용하는 함수에 120회의 요청을 보낸다.
     * response http status가 200번대인 것의 개수를 count한다.
     * count==100 -> test success, count>100 -> test fail
     * @throws Exception
     */
    @Test
    @Throws(Exception::class)
    fun testCallCounts() {
        val executor = Executors.newFixedThreadPool(3)
        val futures: MutableList<Future<ResponseEntity<*>>> = ArrayList()
        for (i in 0 until targetCallCount) futures.add(executor.submit(Callable { doncallController.donCallOneHundredPerMinute() }))

//        for (Future<ResponseEntity<?>> future : futures)
//            logger.info(String.valueOf(future.get()));
        val successCallCount = futures.stream()
            .filter { future: Future<ResponseEntity<*>> ->
                try {
                    return@filter future.get().statusCode.is2xxSuccessful
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                false
            }
            .count().toInt()
        executor.awaitTermination(2, TimeUnit.SECONDS)
        executor.shutdown()
        AssertionsForClassTypes.assertThat(callLimit).isEqualTo(successCallCount)
    }
}