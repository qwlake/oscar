package oscar.testcoroutine.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.net.URL

@Service
class TestService {

    val logger = LoggerFactory.getLogger(TestService::class.java)

    suspend fun foo(): String {
        val url = URL("http://localhost:8080/index")
        val name = url.readText()

//        val name = withContext(Dispatchers.Default) {
//            url.readText()
//        }

//        logger.info(Thread.currentThread().id.toString())
//        Thread.sleep(2000)

        logger.info(name)
        return "name"
    }
}