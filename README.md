# API for graduation by Kurstev

The purpose of this application is to provide the necessary functionality for a mobile application.
All functionality API can be found in [Swagger](http://5.35.85.206:8080/swagger) the documentation

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=fractalliter_ktor-postgres-backend&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=fractalliter_ktor-postgres-backend)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=fractalliter_ktor-postgres-backend&metric=bugs)](https://sonarcloud.io/summary/new_code?id=fractalliter_ktor-postgres-backend)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=fractalliter_ktor-postgres-backend&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=fractalliter_ktor-postgres-backend)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=fractalliter_ktor-postgres-backend&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=fractalliter_ktor-postgres-backend)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=fractalliter_ktor-postgres-backend&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=fractalliter_ktor-postgres-backend)

The project comprises the following ingredients:

- [Ktor](https://ktor.io/) server
  includes [JSON serializers](https://ktor.io/docs/serialization.html), [Authentication](https://ktor.io/docs/authentication.html),
  and [Testing](https://ktor.io/docs/testing.html)
- [Netty](https://netty.io/) web server
- [Postgres](https://www.postgresql.org/) as database
- [Exposed](https://github.com/JetBrains/Exposed) as ORM
- [Hikari Connection Pool](https://github.com/brettwooldridge/HikariCP)
- [Logback](https://logback.qos.ch/) for logging purposes
- [Flyway](https://github.com/flyway/flyway): Handle database migrations.


Project is SQL DB agnostic. You are able to dynamically change Postgres to any other databases that Exposed JDBC
supports by changing a couple of variables:

- the database driver version in `gradle.properties`
- the database driver dependency in `build.gradle.kts`
- the **WEB_DB_URL** variable in `.env` file

## Run the web service
- Make sure that docker and docker-compose are installed on your machine
- Go to /docker/dev/ and and run the following command to start the database:
```bash
docker-compose --file docker-compose.yml up
```
- Open the project with Intellij (works with the version 2023.3.1), configure the application and run.
- Open a web browser for example, and go to http://0.0.0.0:8080/ and you should see "Server is running" in the page :)

## How to run tests locally

To run the tests locally, you should run docker compose with `docker-compose-test.yml` file.
It will run the tests against the test database.

```bash
docker-compose --file docker-compose-test.yml up 
```

After finishing the tests, you can clean test data nd shut the containers down with the following command:

```bash
docker-compose --file docker-compose-test.yml down -v
```