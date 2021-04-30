package oscar.kotlin.shortenUrl.domain

import javax.persistence.*

@Entity(
    name = "Urls"
)
class UrlsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @Column(length = 1000, nullable = false)
    var url: String = ""
}