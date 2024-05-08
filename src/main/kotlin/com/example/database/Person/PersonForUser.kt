package com.example.database.Person

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction

object PersonForUser: IdTable<Long>("person") {
    override val id: Column<EntityID<Long>> = PersonForUser.long("id").autoIncrement().entityId()

    private val surname = PersonForUser.varchar("surname", 64)
    private val name = PersonForUser.varchar("name", 64)
    private val patronymic = PersonForUser.varchar("patronymic", 64).nullable()
    private val role = PersonForUser.text("role")

    override val primaryKey: PrimaryKey = PrimaryKey(id)

    fun insertAndGetId(personDTO: PersonDTO): Long{
        var newPersonId: Long = 0
        transaction {
            addLogger(StdOutSqlLogger)

            newPersonId  = PersonForUser.insertAndGetId {
                it[surname] = personDTO.surname
                it[name] = personDTO.name
                it[patronymic] = personDTO.patronymic
                it[role] = personDTO.role!!
            }.value
        }
        return newPersonId
    }


}