package oscar.testjdbc.dao

import org.springframework.data.annotation.Id

class Person {
    @Id
    val id: Long = 0
    val firstName: String? = null
    val lastName: String? = null
    // constructors, getters, setters
}