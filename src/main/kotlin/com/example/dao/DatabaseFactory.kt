package com.example.dao

import com.example.utils.dotenv
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.*
import kotlinx.coroutines.Dispatchers
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction


object DatabaseFactory {
    fun init(config: ApplicationConfig) {
        val driverClassName = config.property("ktor.database.driverClassName").getString()
        val jdbcURL = config.property("ktor.database.jdbcURL").getString()
        val maxPoolSize = config.property("ktor.database.maxPoolSize").getString()
        val autoCommit = config.property("ktor.database.autoCommit").getString()
        val username = config.property("ktor.database.user").getString()
        val password = config.property("ktor.database.password").getString()
        val defaultDatabase = config.property("ktor.database.database").getString()
        val host: String = dotenv["HOST"] ?: "localhost"
        val port: String = dotenv["PORT"] ?: "5432"
        val connectionPool = createHikariDataSource(
            url = "$jdbcURL/$defaultDatabase?user=$username&password=$password",
            driver = driverClassName,
            maxPoolSize.toInt(),
            autoCommit.toBoolean()
        )

        Database.connect(connectionPool)

        // настраиваем Flyway
        val flyway = Flyway.configure()
            .dataSource("jdbc:postgresql://$host:$port/$defaultDatabase", "$username", "$password")
            .baselineOnMigrate(true)
            .locations("db/migration") // указываем папку с миграциями
            .load()
        //Обновление истории схем
        flyway.repair()
        // запускаем миграции
        flyway.migrate()
    }

    private fun createHikariDataSource(
        url: String,
        driver: String,
        maxPoolSize: Int,
        autoCommit: Boolean
    ) = HikariDataSource(HikariConfig().apply {
        driverClassName = driver
        jdbcUrl = url
        maximumPoolSize = maxPoolSize
        isAutoCommit = autoCommit
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    })

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}