package oscar.kotlin.shortenUrl.domain

import javax.persistence.*

@Entity(
    name = "Counts"
)
@Table(
    indexes = [Index(name = "encoded_idx", columnList = "encoded")],
)
class CountsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @Column(nullable = false, unique = true, updatable = false)
    var encoded: String = ""

    @Column(nullable = false)
    var count: Int = 0
}