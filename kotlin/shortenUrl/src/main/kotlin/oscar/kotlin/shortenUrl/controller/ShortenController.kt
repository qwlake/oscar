package oscar.kotlin.shortenUrl.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import oscar.kotlin.shortenUrl.mapper.ShortenMapper
import oscar.kotlin.shortenUrl.model.*
import oscar.kotlin.shortenUrl.service.ShortenService

@Controller
class ShortenController (
    private val shortenService: ShortenService,
    private val shortenMapper: ShortenMapper
    ) {

    @PostMapping("/shorten")
    fun postShorten(@RequestBody originalUrl: ShortenRequestModel) : ResponseEntity<ShortenResponseModel> {
        val urlDto = shortenService.saveShortenUrl(shortenMapper.toShortenRequestDto(originalUrl))
        val toShortenResponseModel = shortenMapper.toShortenResponseModel(urlDto)
        return ResponseEntity.ok(toShortenResponseModel)
    }

    @GetMapping("/redirect/{path}")
    fun getRedirect(@PathVariable path: RedirectRequestModel): ResponseEntity<Any> {
        val url: String = shortenService.getOriginalUrl(path.shortenUrl)
        return ResponseEntity
            .status(HttpStatus.PERMANENT_REDIRECT)
            .header("Location", url)
            .build()
    }

    @GetMapping("/redirect/{path}/count")
    fun getRedirectCount(@PathVariable path: RedirectCountRequestModel) : ResponseEntity<RedirectCountResponseModel> {
        val redirectCountDto = shortenService.getRedirectCount(path.shortenUrl)
        val toRedirectCountResponseModel = shortenMapper.toRedirectCountResponseModel(redirectCountDto)
        return ResponseEntity.ok(toRedirectCountResponseModel)
    }
}