package oscar.kotlin.shortenUrl.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import oscar.kotlin.shortenUrl.domain.CountsEntity
import oscar.kotlin.shortenUrl.domain.CountsRepository
import oscar.kotlin.shortenUrl.model.*
import oscar.kotlin.shortenUrl.service.ShortenService
import kotlin.random.Random


@SpringBootTest
@Transactional
internal class ShortenControllerTest (
    @Autowired val controller: ShortenController,
    @Autowired val shortenService: ShortenService,
    @Autowired val countsRepository: CountsRepository
    ) {

    private val TEST_URL = "https://www.naver.com"
    private val logger = LoggerFactory.getLogger(ShortenControllerTest::class.java)

    @Test
    fun postShorten() {
        val shortenResponse: ShortenResponseModel? = controller.postShorten(ShortenRequestModel(TEST_URL)).body
        assertThat(shortenResponse != null)
        val encoded = shortenResponse!!.url
        logger.info(encoded)
        val originalUrl = shortenService.getOriginalUrl(encoded)
        assertThat(originalUrl == TEST_URL)
    }

    @Test
    fun getRedirect() {
        val encoded: String = controller.postShorten(ShortenRequestModel(TEST_URL)).body!!.url
        val responseEntity: ResponseEntity<Any> = controller.getRedirect(RedirectRequestModel(encoded))
        val headers: Map<*,*> = responseEntity.headers.toMap()
        assertThat(responseEntity.statusCode == HttpStatus.PERMANENT_REDIRECT)
        assertThat(headers.keys.contains("Location"))
        assertThat(headers["Location"] == TEST_URL)
    }

    @Test
    fun getRedirectCount() {
        val countsEntity = CountsEntity()
        val randomInt = Random.nextInt(1000)
        countsEntity.encoded = "test1"
        countsEntity.count = randomInt
        countsRepository.save(CountsEntity())
        val body: RedirectCountResponseModel? =
            controller.getRedirectCount(RedirectCountRequestModel("test1")).body
        assertThat(body != null)
        assertThat(body!!.count == randomInt)
    }
}