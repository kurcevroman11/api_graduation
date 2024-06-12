val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val hikaricp_version: String by project

plugins {
    application
    kotlin("jvm") version "1.8.21"
    id("io.ktor.plugin") version "2.3.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.6.10"
}

tasks {
    shadowJar {
        manifest {
            attributes(Pair("Main-Class", "com.personia.ApplicationKt"))
        }
    }
}

group = "com.example"
version = "0.0.1"
application {
    mainClass.set("com.example.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

dependencies {
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation(kotlin("stdlib-jdk8"))
    testImplementation(kotlin("test"))

    implementation("org.jetbrains.exposed:exposed-core:0.40.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.40.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.40.1")
    implementation("org.jetbrains.exposed:exposed-jodatime:0.40.1")

    implementation("com.h2database:h2:2.1.214")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.10")

    implementation("org.flywaydb:flyway-core:7.7.3")
    implementation("org.postgresql:postgresql:42.5.4")

    implementation("com.google.code.gson:gson:2.8.9")

    implementation("com.nimbusds:nimbus-jose-jwt:9.10")

    implementation("io.github.microutils:kotlin-logging:1.11.0")

    implementation("com.zaxxer:HikariCP:$hikaricp_version")

    // Библиотека для работы с .env
    implementation("io.github.cdimascio:java-dotenv:5.2.2")

    // Библиотеки для работы с сетью
    implementation("io.ktor:ktor-serialization:2.2.4")
    implementation("com.auth0:java-jwt:3.18.1")
    implementation("io.ktor:ktor-gson:1.6.4")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-gson:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.ktor:ktor-server-cors:$ktor_version")
    implementation("io.ktor:ktor-server-sessions:$ktor_version")
    implementation("io.ktor:ktor-server-caching-headers:$ktor_version")
    implementation("io.ktor:ktor-server-auth:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jwt:$ktor_version")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.12.4")

    implementation("org.apache.poi:poi:5.0.0")
    implementation("org.apache.poi:poi-ooxml:5.0.0")

    // Библиотеки для тестирования
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlin_version")
    testImplementation("io.ktor:ktor-server-tests-jvm:$ktor_version")
    testImplementation("io.ktor:ktor-server-test-host-jvm:$ktor_version")

    // Swagger
    implementation("io.ktor:ktor-server-swagger:2.0.0")
}