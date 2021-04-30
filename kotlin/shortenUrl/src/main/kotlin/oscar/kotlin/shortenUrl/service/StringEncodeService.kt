package oscar.kotlin.shortenUrl.service

import io.seruco.encoding.base62.Base62
import org.springframework.stereotype.Service
import java.util.*
import javax.annotation.PostConstruct

@Service
class StringEncodeService {

    private lateinit var base62: Base62
    private lateinit var random: Random

    @PostConstruct
    fun init() {
        base62 = Base62.createInstance()
        random = Random()
    }

    fun encode(plain: String): String {
        return String(base62.encode((plain + "/" + getFixedRandomSalt()).encodeToByteArray()))
    }

    fun decode(encode: String): String {
        return String(base62.decode(encode.toByteArray()))
    }

    private fun getFixedRandomSalt(): String {
        return (1000 + random.nextInt(9000)).toString()
    }
}