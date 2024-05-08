package com.example.database.Person

import com.example.dao.DatabaseFactory.dbQuery
import com.example.database.UserRoleProject.UserRoleProjectDTO
import com.example.database.user.UsersDTO
import com.example.db.UserRoleProject.UserRoleProjectModel
import io.ktor.http.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

object PersonModule: Table("person") {
    val id = PersonModule.integer("id").autoIncrement()
    private val surname = PersonModule.varchar("surname", 64)
    private val name = PersonModule.varchar("name", 64)
    private val patronymic = PersonModule.varchar("patronymic", 64).nullable()
    val role = PersonModule.text("role").nullable()

    fun insertPerson(personDTO: PersonDTO){
        transaction {
            addLogger(StdOutSqlLogger)

            PersonModule.insert{
                it[surname] = personDTO.surname
                it[name] = personDTO.name
                it[patronymic] = personDTO.patronymic
                it[role] = personDTO.role
            }
        }
    }

    // Функция для извлечения списка людей, связанных с определенной задачей
    fun fetchPersonsByTask(taskId: Int): List<PersonDTO> {
        return transaction {
            // Соединение таблиц 'person' и 'usersroleproject'
            (PersonModule innerJoin UserRoleProjectModel)
                .slice(
                    PersonModule.id,
                    PersonModule.surname,
                    PersonModule.name,
                    PersonModule.patronymic,
                    PersonModule.role
                )
                .select { UserRoleProjectModel.current_task eq taskId }
                .map { row ->
                    PersonDTO(
                        id = row[PersonModule.id],
                        surname = row[PersonModule.surname],
                        name = row[PersonModule.name],
                        patronymic = row[PersonModule.patronymic],
                        role = row[PersonModule.role]
                    )
                }
        }
    }

    fun getUserInProj(idProj: Int): List<PersonDTO> {
        return transaction {
            // Соединение таблиц 'person' и 'usersroleproject'
            (PersonModule innerJoin UserRoleProjectModel)
                .slice(
                    PersonModule.id,
                    PersonModule.surname,
                    PersonModule.name,
                    PersonModule.patronymic,
                    PersonModule.role
                )
                .select { UserRoleProjectModel.task eq idProj }
                .map { row ->
                    PersonDTO(
                        id = row[PersonModule.id],
                        surname = row[PersonModule.surname],
                        name = row[PersonModule.name],
                        patronymic = row[PersonModule.patronymic],
                        role = row[PersonModule.role]
                    )
                }
        }
    }

    fun fetchPersonID(id:Int): PersonDTO? {
        return try {
            transaction {
                val person = PersonModule.select { PersonModule.id.eq(id) }.single()
                PersonDTO(
                    id = person[PersonModule.id],
                    surname = person[surname],
                    name = person[name],
                    patronymic = person[patronymic],
                    role = person[role]
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    // Извлечение пользоваелей которые не привязаны к определенному проекту
    fun fetchPersonBynIds(ids: MutableList<UsersDTO>): MutableList<PersonDTO> {
        val list: MutableList<PersonDTO> = mutableListOf()
        transaction {
            addLogger(StdOutSqlLogger)
            ids.forEach { it ->
                val person = PersonModule.select { PersonModule.id.eq(it.personId!!) }.map {
                    PersonDTO(
                        id = it[PersonModule.id],
                        surname = it[surname],
                        name = it[name],
                        patronymic = it[patronymic],
                        role = it[role]
                    )
                }
                list.addAll(person)
            }
        }
        return list
    }

    fun fetchAllPerson(): List<PersonDTO> {
        return try {
            transaction {
                PersonModule.selectAll().map{
                    PersonDTO(
                        it[PersonModule.id],
                        it[surname],
                        it[name],
                        it[patronymic],
                        it[role],
                    )
                }
            }
        } catch (e: Exception) {
            ArrayList<PersonDTO>()
        }

    }

    fun updatePerson(id: Int, personDTO: PersonDTO): HttpStatusCode {
        transaction {
            val person = PersonModule.update( { PersonModule.id eq (id) } )
            {
                it[surname] = personDTO.surname
                it[name] = personDTO.name
                it[patronymic] = personDTO.patronymic
                it[role] = personDTO.role
            }
            if (person > 0) {
                return@transaction HttpStatusCode.NoContent
            } else {
                return@transaction "Person with ID $id not found."
            }
        }
        return HttpStatusCode.OK
    }

    fun deletePerson(id: Int): HttpStatusCode {
        if (id != null) {
            transaction {
                val deletedRowCount = PersonModule.deleteWhere { PersonModule.id eq id }
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






