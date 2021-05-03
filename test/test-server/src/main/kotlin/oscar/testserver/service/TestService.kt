package oscar.testserver.service

import org.springframework.stereotype.Service
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


@Service
class TestService {

    fun bar(): String {
        val threadId = Thread.currentThread().id
        println("$threadId start")

        // 실패
//        val currentTimeMillis = System.currentTimeMillis()
//        while(System.currentTimeMillis() < currentTimeMillis + 2000) {
//            // 2초가 경과할 때까지 대기
//        }

        // 실패
//        var time = Calendar.getInstance()
//        val timeInMillis = time.timeInMillis
//        while(time.timeInMillis < timeInMillis + 2000) {
//            // 2초가 경과할 때까지 대기
//            time = Calendar.getInstance()
//        }

        Thread.sleep(2000)

//        val executorService = Executors.newSingleThreadScheduledExecutor()
//        executorService.scheduleAtFixedRate(this::dump, 0, 2, TimeUnit.SECONDS)

//        val scheduler = Executors.newSingleThreadScheduledExecutor()
//        scheduler.schedule({}, 3000, TimeUnit.MILLISECONDS)

//        var cnt = 0L
//        while (cnt < 10000000000L) {
//            cnt += 1
//        }

        println("$threadId end")

        return "bar"
    }

    fun dump() {
        println("dump")
    }
}