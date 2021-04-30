package oscar.kotlin.shortenUrl.mapper

import org.mapstruct.Mapper
import oscar.kotlin.shortenUrl.domain.CountsEntity
import oscar.kotlin.shortenUrl.domain.UrlsEntity
import oscar.kotlin.shortenUrl.dto.CountDto
import oscar.kotlin.shortenUrl.dto.RedirectCountDto
import oscar.kotlin.shortenUrl.dto.UrlDto
import oscar.kotlin.shortenUrl.model.RedirectCountResponseModel
import oscar.kotlin.shortenUrl.model.ShortenRequestModel
import oscar.kotlin.shortenUrl.model.ShortenResponseModel

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE
)
interface ShortenMapper {

    fun toShortenRequestDto(originalUrl: ShortenRequestModel): UrlDto
    fun toUrlsEntity(urlDto: UrlDto): UrlsEntity
    fun toShortenResponseModel(urlDto: UrlDto): ShortenResponseModel
    fun toRedirectCountResponseModel(redirectCountDto: RedirectCountDto): RedirectCountResponseModel
    fun toCountsEntity(dto: CountDto): CountsEntity
}