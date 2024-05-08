package com.example.database.user

import com.example.dao.DatabaseFactory.dbQuery
import com.example.database.Person.PersonModule
import io.ktor.http.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

object UserModule: Table("usser") {
    val id = UserModule.integer("id").autoIncrement()
    val login =  UserModule.varchar("login", 50)
    val password =  UserModule.varchar("password", 50)
    val personId = UserModule.integer("personid").nullable()


    private fun resultRowToNode(row: ResultRow) = UsersDTO(
        id = row[UserModule.id],
        login = row[UserModule.login],
        password = row[UserModule.password],
        personId = row[UserModule.personId]
    )

    fun  insert (usersDTO: UsersDTO){
        transaction {
            UserModule.insert {
                it[login] = usersDTO.login
                it[password] = usersDTO.password
                it[personId] = usersDTO.personId
            }
        }
    }

    // Получение пользователия по логину
    suspend fun fetchUser(login: String): UsersDTO? = dbQuery {
        UserModule
            .select { UserModule.login.eq(login) }
            .map(::resultRowToNode).singleOrNull()
    }

    fun fetchRole(login: String): String {
        var roleResult = ""
        transaction {
            exec("SELECT person.role\n" +
                    "FROM usser\n" +
                    "JOIN person ON usser.personid = person.id\n" +
                    "WHERE usser.login = '${login}';") { rs ->
                while (rs.next()) {
                    roleResult = rs.getString("role")
                }
            }
        }
        return roleResult
    }
    fun getUserToLogin(login: String): UsersDTO? {
        return try {
            transaction {
                val user = UserModule.select { UserModule.login.eq(login) }.single()
                UsersDTO(
                    id = user[UserModule.id],
                    login = user[UserModule.login],
                    password = user[password],
                    personId = user[personId]
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    fun fetchUserID(id: Int): UsersDTO? {
        return try {
            transaction {
                val user = UserModule.select { UserModule.id.eq(id) }.single()
                UsersDTO(
                    id = user[UserModule.id],
                    login = user[UserModule.login],
                    password = user[password],
                    personId = user[personId]
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    fun updateUser(id: Int, userDTO: UsersDTO): HttpStatusCode {
        transaction {
            val user = UserModule.update( { UserModule.id eq (id) } )
            {
                it[login] = userDTO.login
                it[password] = userDTO.password
                it[personId] = userDTO.personId
            }
            if (user > 0) {
                return@transaction HttpStatusCode.NoContent
            } else {
                return@transaction "User with ID $id not found."
            }
        }
        return HttpStatusCode.OK
    }

    fun deleteUser(id: Int): HttpStatusCode {
        if (id != null) {
            transaction {
                val deletedRowCount = UserModule.deleteWhere { UserModule.personId eq id }
                if (deletedRowCount > 0) {
                    return@transaction HttpStatusCode.NoContent
                } else {
                    return@transaction HttpStatusCode.NoContent
                }
            }
        } else {
            return HttpStatusCode.BadRequest
        }
        return HttpStatusCode.OK
    }
}