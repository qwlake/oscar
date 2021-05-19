package oscar.testjdbc.component

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component


@Component
class DatabaseSeeder(val jdbcTemplate: JdbcTemplate) {
    fun insertData() {
        jdbcTemplate.execute("INSERT INTO Person(first_name,last_name) VALUES('Victor', 'Hugo')")
        jdbcTemplate.execute("INSERT INTO Person(first_name,last_name) VALUES('Dante', 'Alighieri')")
        jdbcTemplate.execute("INSERT INTO Person(first_name,last_name) VALUES('Stefan', 'Zweig')")
        jdbcTemplate.execute("INSERT INTO Person(first_name,last_name) VALUES('Oscar', 'Wilde')")
    }
}