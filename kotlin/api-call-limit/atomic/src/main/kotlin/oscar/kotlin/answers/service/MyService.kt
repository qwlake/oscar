package oscar.kotlin.answers.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.lang.Thread.sleep
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection

@Service
class MyService {

    val logger = LoggerFactory.getLogger(MyService::class.java)

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
