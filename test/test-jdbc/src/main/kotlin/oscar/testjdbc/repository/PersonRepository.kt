package oscar.testjdbc.repository

import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import oscar.testjdbc.dao.Person


@Repository
interface PersonRepository : CrudRepository<Person, Long> {
    fun findByFirstName(firstName: String): List<Person>

    @Modifying
    @Query("UPDATE person SET first_name = :name WHERE id = :id")
    fun updateByFirstName(@Param("id") id: Long, @Param("name") name: String): Boolean
}