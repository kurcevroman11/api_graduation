package com.example.integrations

import com.example.configIntegrationTestApp
import com.example.database.Person.PersonDTO
import com.example.database.Role.RoleDTO
import com.example.database.Status.StatusDTO
import com.example.database.Task.TaskByID
import com.example.database.UserRoleProject.UserRoleProjectDTO
import com.example.database.activity.ActivityDTO
import com.example.database.man_hours.ManHoursDTO
import com.example.database.type_of_activity.Type_of_activityDTO
import com.example.db.Description.DescriptionDTOFileDTO
import com.example.db.Task.TaskDTO
import com.example.features.register.RegisterReceiveRemote
import com.example.features.register.RegisterResponseRemote
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

// Проработка действий пользователя
class UseCase {
    @Test
    fun useCase() = testApplication {
        val client = configIntegrationTestApp()
        val token = authenticate(client)

        // <------------------------------------ДОБАВЛЕНИЕ ПРОЕКТА------------------------------------>

        // Тестирование создания проекта
        val project = TaskDTO()
        project.name = "Test"
        var response = client.post("/task") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $token")
            setBody(project)
        }
        assertEquals(HttpStatusCode.Created, response.status)

        // Тестирование просмотров проектов
       response = client.get("/user_role_project/project") {
           header("Authorization", "Bearer $token")
       }
        var test = response.bodyAsText()
        assertTrue(test.contains("Test"))

        // Извлечение определенного проекта
        var getProjectTest = response.body<MutableList<TaskDTO>>().find { task ->
            task.name == "Test"
        }

        // <------------------------------------ДОБАВЛЕНИЕ 1-й ЗАДАЧИ------------------------------------>

        // Добавление новой задачи
        val task = TaskDTO()
        task.name = "TestTask"
        task.scope = 10
        task.typeofactivityid = 1
        response = client.post("/task/${getProjectTest?.id}") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $token")
            setBody(task)
        }
        assertEquals(HttpStatusCode.Created, response.status)

        // Получение не выполненых задач (по умолчинию все задачи не выполненные)
        response = client.get("/task/downtask/unfulfilled/${getProjectTest?.id}") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $token")
        }
        val testTask = response.bodyAsText()
        assertTrue(testTask.contains("TestTask"))

        // Извлечение определенной задачи
        var getProjectTestTask = response.body<MutableList<TaskDTO>>().find { task ->
            task.name == "TestTask"
        }
        assertEquals(task.name, getProjectTestTask?.name)
        // Проверка трудозатрат
        assertEquals(task.scope, getProjectTestTask?.scope, "Проверка трудозатрат (задача1)")
        // Проверка видов деятельности
        assertEquals(task.typeofactivityid, getProjectTestTask?.typeofactivityid, "Проверка видов деятельности (задача1)")
        // Проверка поколения
        assertEquals(2, getProjectTestTask?.generation, "Задача всегда должна иметь generation равным 2 (задача1)")
        // Сначало описание пустое
        assertTrue(getProjectTestTask?.content == null, " Сначало описание пустое (задача1)")

        // Тестирование отображения кол-во часов (трудозатрат) на выполнение проекта
        response = client.get("/user_role_project/project") {
            header("Authorization", "Bearer $token")
        }

        // Извлечение определенного проекта
        getProjectTest = response.body<MutableList<TaskDTO>>().find { task ->
            task.name == "Test"
        }

        // Должно быть равно 10
        assertEquals(getProjectTest?.scope, getProjectTestTask?.scope)


        // <------------------------------------ДОБАВЛЕНИЕ 2-й ЗАДАЧИ------------------------------------>

        // Добавление новой задачи
        val task2 = TaskDTO()
        task2.name = "TestTask2"
        task2.scope = 20
        task2.typeofactivityid = 1
        response = client.post("/task/${getProjectTest?.id}") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $token")
            setBody(task2)
        }
        assertEquals(HttpStatusCode.Created, response.status)

        // Получение не выполненых задач (по умолчинию все задачи не выполненные)
        response = client.get("/task/downtask/unfulfilled/${getProjectTest?.id}") {
            header("Authorization", "Bearer $token")
        }
        val testTask2 = response.bodyAsText()
        assertTrue(testTask2.contains("TestTask2"))

        // Извлечение определенной задачи
        var getProjectTestTask2 = response.body<MutableList<TaskDTO>>().find { task ->
            task.name == "TestTask2"
        }
        assertEquals(task2.name, getProjectTestTask2?.name)
        // Проверка трудозатрат
        assertEquals(task2.scope, getProjectTestTask2?.scope, "Проверка трудозатрат (задача2)")
        // Проверка видов деятельности
        assertEquals(task2.typeofactivityid, getProjectTestTask2?.typeofactivityid, "Проверка видов деятельности (задача2)")
        // Проверка поколения
        assertEquals(2, getProjectTestTask2?.generation, "Задача всегда должна иметь generation равным 2 (задача2)")
        // Сначало описание пустое
        assertTrue(getProjectTestTask2?.content == null, " Сначало описание пустое (задача2)")

        // Тестирование отображения кол-во на выполнение проекта
        response = client.get("/user_role_project/project") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $token")
        }

        // Извлечение тестового проекта
        getProjectTest = response.body<MutableList<TaskDTO>>().find { task ->
            task.name == "Test"
        }

        // Должно быть равно 20, так как нет зависимостей между задачами
        assertEquals(20, getProjectTest?.scope)

        // <------------------------------------ДОБАВЛЕНИЕ (ОБНОВЛЕНИЕ) ОПИСАНИЯ У 1-й ЗАДАЧИ------------------------------------>

        // Добавление (обновление) описание задачи
        task.content = "TestDescription"
        response = client.put("/task/update/${getProjectTestTask?.id}") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $token")
            setBody(task)
        }
        assertEquals(HttpStatusCode.OK, response.status)

        // Повторный запрос для получения обновленного описания к задаче
        response = client.get("/task/downtask/unfulfilled/${getProjectTest?.id}") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $token")
        }

        // Извлечение определенной задачи
        getProjectTestTask = response.body<MutableList<TaskDTO>>().find { task ->
            task.name == "TestTask"
        }
        assertEquals(task.name, getProjectTestTask?.name)
        // Проверка трудозатрат
        assertEquals(task.scope, getProjectTestTask?.scope, "Проверка трудозатрат (задача1)")
        // Проверка видов деятельности
        assertEquals(task.typeofactivityid, getProjectTestTask?.typeofactivityid, "Проверка видов деятельности (задача1)")
        // Задача всегда должна иметь generation равным 2
        assertEquals(2, getProjectTestTask?.generation, "Задача всегда должна иметь generation равным 2 (задача1)")
        // Проверка описания
        assertEquals(task.content, getProjectTestTask?.content, "Проверка описания (задача1)")

        // <-------------------------------------------ДОБАВЛЕНИЕ ФАЙЛА В ЗАДАЧУ------------------------------------>

        response = client.post("/description/upload/${getProjectTestTask?.description}") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $token")
            setBody(MultiPartFormDataContent(
                formData {
                    append("description", "Mind_Map")
                    append("image", File("./src/test/kotlin/com/example/integrations/Mind_Map.jpg")
                        .readBytes(), Headers.build {
                        append(HttpHeaders.ContentType, "image/jpg")
                        append(HttpHeaders.ContentDisposition, "filename=\"Mind_Map.jpg\"")
                    })
                },
                boundary = "WebAppBoundary"
            )
            )
            onUpload { bytesSentTotal, contentLength ->
                println("Sent $bytesSentTotal bytes from $contentLength")
            }
        }
        assertEquals(HttpStatusCode.OK, response.status)

        // <-----------------------------------ПОЛУЧЕНИЕ СПИСКОВ ФАЙЛОВ В ЗАДАЧЕ------------------------------------>

        response = client.get("/description/${getProjectTestTask?.description}") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $token")
        }
        assertEquals(HttpStatusCode.OK, response.status)

        var json = response.bodyAsText()
        val getTestFile = Json.decodeFromString<DescriptionDTOFileDTO?>(json)
        assertEquals("Mind_Map", getTestFile?.file_resources?.get(0)?.orig_filename)

        // <------------------------------------------СКАЧИВАНИЕ ФАЙЛА------------------------------------------->

        response = client.get("/description/download/${getProjectTestTask?.description}/${getTestFile?.file_resources?.get(0)?.id}") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $token")
        }
        assertEquals(HttpStatusCode.OK, response.status)

        // <---------------------------------------УДАЛЕНИЕ ОПРЕДЕЛННОГО ФАЙЛА------------------------------------>

        response = client.delete("/description/${getProjectTestTask?.description}/${getTestFile?.file_resources?.get(0)?.id}") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $token")
        }
        assertEquals(HttpStatusCode.OK, response.status)

        // <------------------------------------ОБНОВЛЕНИЕ СТАТУСА У 1-й ЗАДАЧИ------------------------------------>

        task.status = 1 // 1 - статус готово
        response = client.put("/task/update/${getProjectTestTask?.id}") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $token")
            setBody(task)
        }
        assertEquals(HttpStatusCode.OK, response.status)

        // После обновление нужно получить список выполненых задач
        response = client.get("/task/downtask/completed/${getProjectTest?.id}") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $token")
        }

        // Извлечение определенной задачи
        getProjectTestTask = response.body<MutableList<TaskDTO>>().find { task ->
            task.name == "TestTask"
        }
        assertEquals(task.name, getProjectTestTask?.name)
        // Проверка трудозатрат
        assertEquals(task.scope, getProjectTestTask?.scope, "Проверка трудозатрат (задача1)")
        // Проверка видов деятельности
        assertEquals(task.typeofactivityid, getProjectTestTask?.typeofactivityid, "Проверка видов деятельности (задача1)")
        // Задача всегда должна иметь generation равным 2
        assertEquals(2, getProjectTestTask?.generation, "Задача всегда должна иметь generation равным 2 (задача1)")
        // Проверка описания
        assertEquals(task.content, getProjectTestTask?.content, "Проверка описания (задача1)")
        // Проверка статуса
        assertEquals(task.status, getProjectTestTask?.status, "Должно быть равно 1 (статус готово)")

        // <------------------------------------ДОБАВЛЕНИЕ ПОЗАДАЧИ В 1-й ЗАДАЧЕ------------------------------------->

        // Добавление подзадачи
        val subTask = TaskDTO()
        subTask.name = "TestSubTask"
        subTask.scope = 30
        subTask.typeofactivityid = 1
        response = client.post("/task/${getProjectTestTask?.id}") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $token")
            setBody(subTask)
        }
        assertEquals(HttpStatusCode.Created, response.status)

        // Получение не выполненых задач (по умолчинию все задачи не выполненные)
        response = client.get("/task/downtask/unfulfilled/${getProjectTestTask?.id}") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $token")
        }
        val testSubTask = response.bodyAsText()
        assertTrue(testSubTask.contains("TestSubTask"))

        // Извлечение определенной задачи
        var getProjectTestSubTask = response.body<MutableList<TaskDTO>>().find { task ->
            task.name == "TestSubTask"
        }
        assertEquals(subTask.name, getProjectTestSubTask?.name)
        // Проверка трудозатрат
        assertEquals(subTask.scope, getProjectTestSubTask?.scope, "Проверка трудозатрат (подзадача1)")
        // Проверка видов деятельности
        assertEquals(subTask.typeofactivityid, getProjectTestSubTask?.typeofactivityid, "Проверка видов деятельности (подзадача1)")
        // Проверка поколения
        assertEquals(3, getProjectTestSubTask?.generation, "Подзадача всегда должна иметь generation равным 3 (подзадача1)")
        // Сначало описание пустое
        assertTrue(getProjectTestSubTask?.content == null, "Сначало описание пустое (подзадача1)")

        // Повторный вызов списка выполненых задач
        response = client.get("/task/downtask/completed/${getProjectTest?.id}") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $token")
        }

        // Извлечение определенной задачи
        getProjectTestTask = response.body<MutableList<TaskDTO>>().find { task ->
            task.name == "TestTask"
        }

        // В задаче scope должно быть равно 30
        assertEquals(getProjectTestTask?.scope, getProjectTestSubTask?.scope)

        // Тестирование отображения кол-во часов (трудозатрат) на выполнение проекта
        response = client.get("/user_role_project/project") {
            header("Authorization", "Bearer $token")
        }

        // Извлечение определенного проекта
        getProjectTest = response.body<MutableList<TaskDTO>>().find { task ->
            task.name == "Test"
        }

        // В проекте scope должно быть равно 30
        assertEquals(getProjectTest?.scope, getProjectTestSubTask?.scope)

        // <------------------------------------РЕГИСТРАЦИЯ ПОЛЬЗОВАТЕЛЯ В СИСТЕМУ------------------------------------>

        // Добавление новой задачи
        var registerUser = RegisterReceiveRemote(
            login = "Test23",
            password = "test23",
            fio = "Test Test Test",
            role = "Проект-менеджер"
        )
        response = client.post("/register") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $token")
            setBody(registerUser)
        }

        assertEquals(HttpStatusCode.Created, response.status)

        // <------------------------------------ПРОСМОТР СОЗДАННОГО ПОЛЬЗОВАТЕЛЯ------------------------------------>

        response = client.get("/person") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $token")
        }
        assertEquals(HttpStatusCode.OK, response.status)

        // Извлечение созданного пользователя
        var getTestPerson = response.body<MutableList<PersonDTO>>().find { person ->
            val fio = "${person.surname} ${person.name} ${person.patronymic}"
            fio == registerUser.fio
        }
        assertTrue(getTestPerson != null)

        // <------------------------------------ДОБАВЛЕНИЕ ПОЛЬЗОВАТЕЛЯ В ПРОЕКТ------------------------------------>

        var urp = UserRoleProjectDTO(
            userid = mutableListOf(getTestPerson.id!!, 1),
            projectid = getProjectTest?.id,
        )

        response = client.post("/user_role_project") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $token")
            setBody(urp)
        }
        assertEquals(HttpStatusCode.Created, response.status)

        // <----------------------------------------ПОВТОРНЫЙ ПРОСМОТР ПРОЕКТА------------------------------------>

        // Тестирование просмотров проектов
        response = client.get("/user_role_project/project") {
            header("Authorization", "Bearer $token")
        }
        test = response.bodyAsText()
        assertTrue(test.contains("Test"))

        // Извлечение определенного проекта
        getProjectTest = response.body<MutableList<TaskDTO>>().find { task ->
            task.name == "Test"
        }

        assertEquals(2, getProjectTest?.userCount, "UserCount должен быть равен 2")

        // <------------------------------------РЕГИСТРАЦИЯ 2-ГО ПОЛЬЗОВАТЕЛЯ В СИСТЕМУ------------------------------->

        // Добавления нового пользователя для просмотра в его в списке свободных
        val registerUser2 = RegisterReceiveRemote(
            login = "Test24test",
            password = "test24",
            fio = "Тест Тест Тест",
            role = "Проект-менеджер"
        )
        response = client.post("/register") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $token")
            setBody(registerUser2)
        }

        assertEquals(HttpStatusCode.Created, response.status)

        // <------------------------------------ПОЛУЧЕНИЕ СВОБОДНЫХ ПОЛЬЗОВАТЕЛЕЙ------------------------------------->

        // Тестирование просмотров проектов
        response = client.get("/person/freeperson/${getProjectTest!!.id}") {
            header("Authorization", "Bearer $token")
        }
        assertEquals(HttpStatusCode.OK, response.status)

        val getTestPerson2 = response.body<MutableList<PersonDTO>>().find { person ->
            val fio = "${person.surname} ${person.name} ${person.patronymic}"
            fio == registerUser2.fio
        }
        assertTrue(getTestPerson2 != null)

        // <------------------------------------ВЫВОД ОПРЕДЕЛЕННОЙ ЗАДАЧИ------------------------------------>

        // Тестирование просмотра определенной задачи
        response = client.get("/task/${getProjectTestTask?.id}") {
            header("Authorization", "Bearer $token")
        }
        assertEquals(HttpStatusCode.OK, response.status)

        // Извлечение 1-й задачи
        var getTaskById = response.body<TaskByID>()

        assertEquals(getProjectTestTask?.id, getTaskById.id)
        assertEquals(getProjectTestTask?.name, getTaskById.name)
        assertEquals(getProjectTestTask?.scope, getTaskById.score)
        assertEquals(getProjectTestTask?.userCount, getTaskById.userCount)

        // <------------------------------------ПРИВЯЗКА ПОЛЬЗОВАТЕЛЯ К ЗАДАЧЕ------------------------------------>

        val urp3 = UserRoleProjectDTO(
            userid = mutableListOf(getTestPerson.id!!, 1),
            current_task_id = getProjectTestTask?.id,
        )

        response = client.post("/user_role_project") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $token")
            setBody(urp3)
        }
        assertEquals(HttpStatusCode.Created, response.status)

        // <--------------------------УКАЗАНИЕ КОЛИЧЕСТВО ЧАСОВ ДЛЯ ОПРЕДЕЛЕНОГО ПОЛЬЗОВАТЕЛЯ---------------->

        var urp2 = UserRoleProjectDTO(
            score = 5
        )

        response = client.put("/user_role_project/${getTaskById.id}") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $token")
            setBody(urp2)
        }
        assertEquals(HttpStatusCode.OK, response.status)

        // Тестирование просмотра определенной задачи
        response = client.get("/task/${getProjectTestTask?.id}") {
            header("Authorization", "Bearer $token")
        }
        assertEquals(HttpStatusCode.OK, response.status)

        // Извлечение 1-й задачи
        getTaskById = response.body<TaskByID>()

        assertEquals(5, getTaskById.spentTime)

        // <------------------------------------------ДОБАВЛЕНИЕ ТРУДОЗАТРАТЫ-------------------------------->

        var manHours = ManHoursDTO(
            hours_spent = "00:30",
            comment = "Test",
            activityid = 3,
            taskid = getProjectTestTask?.id
        )

        response = client.post("/manhours/${getProjectTestTask?.id}") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $token")
            setBody(manHours)
        }
        assertEquals(HttpStatusCode.Created, response.status)

        // <------------------------------------------ПРОСМОТР ТРУДАЗАТРАТ-------------------------------->

        response = client.get("/manhours/${getProjectTestTask?.id}") {
            header("Authorization", "Bearer $token")
        }
        assertEquals(HttpStatusCode.OK, response.status)

        var getTestManHours = response.body<MutableList<ManHoursDTO>>().find { item ->
            item.comment == manHours.comment
        }
        assertTrue(getTestManHours != null)

        // <------------------------------------------ПРОСМОТР ТРУДАЗАТРАТ ПО ID-------------------------------->

        response = client.get("/manhours/specific/${getTestManHours.id}") {
            header("Authorization", "Bearer $token")
        }
        assertEquals(HttpStatusCode.OK, response.status)

        json = response.bodyAsText()
        var getTestManHoursById = Json.decodeFromString<ManHoursDTO?>(json)
        assertTrue(getTestManHoursById != null)

        // <------------------------------------------ОБНОВЛЕНИЕ ТРУДОЗАТРАТЫ-------------------------------->

        var manHours2 = ManHoursDTO(
            comment = "TestTest",
            activityid = 1,
        )

        response = client.put("/manhours/${getTestManHoursById.id}"){
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $token")
            setBody(manHours2)
        }
        assertEquals(HttpStatusCode.Created, response.status)

        // Просмотр обновления
        response = client.get("/manhours/specific/${getTestManHoursById.id}") {
            header("Authorization", "Bearer $token")
        }
        assertEquals(HttpStatusCode.OK, response.status)

        getTestManHoursById = response.body<ManHoursDTO>()
        assertEquals(manHours2.comment, getTestManHoursById.comment)
        assertEquals(manHours2.activityid, getTestManHoursById.activityid)

        // <------------------------------------------УДАЛЕНИЕ ТРУДАЗАТРАТЫ-------------------------------->

        response = client.delete("/manhours/${getTestManHoursById.id}") {
            header("Authorization", "Bearer $token")
        }
        assertEquals(HttpStatusCode.OK, response.status)

        // <------------------------------------ВЫВОД ТИПОВ АКТИНОВНОСТЕЙ------------------------------------>

        response = client.get("/type_of_activity") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $token")
        }
        assertEquals(HttpStatusCode.OK, response.status)

        json = response.bodyAsText()
        val getTypeActivity = Json.decodeFromString<MutableList<Type_of_activityDTO?>>(json)
        assertTrue(getTypeActivity.isNotEmpty())

        // <------------------------------------ВЫВОД АКТИНОВНОСТЕЙ------------------------------------>

        response = client.get("/activity") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $token")
        }
        assertEquals(HttpStatusCode.OK, response.status)


        json = response.bodyAsText()
        val getActivity = Json.decodeFromString<MutableList<ActivityDTO?>>(json)
        assertTrue(getActivity.isNotEmpty())

        // <---------------------------------------ВЫВОД СТАТУСОВ------------------------------------>

        // Тестирование просмотров проектов
        response = client.get("/status") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $token")
        }
        assertEquals(HttpStatusCode.OK, response.status)

        json = response.bodyAsText()
        val getStatus = Json.decodeFromString<MutableList<StatusDTO?>>(json)
        assertTrue(getStatus.isNotEmpty())

        // <---------------------------------------ВЫВОД РОЛЕЙ------------------------------------>

        // Тестирование просмотров проектов
        response = client.get("/role") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $token")
        }
        assertEquals(HttpStatusCode.OK, response.status)

        json = response.bodyAsText()
        val getRole = Json.decodeFromString<MutableList<RoleDTO?>>(json)
        assertTrue(getRole.isNotEmpty())

        // <------------------------------------УДАЛЕНИЕ ПОЛЬЗОВАТЕЛЕЙ------------------------------------>

        response = client.delete("/User/${getTestPerson.id}") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $token")
        }
        assertEquals(HttpStatusCode.OK, response.status)

        response = client.delete("/User/${getTestPerson2?.id}") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $token")
        }
        assertEquals(HttpStatusCode.OK, response.status)

        // <------------------------------------УДАЛЕНИЕ ПРОЕКТА----------------------------------------------------->

        response = client.delete("/task/${getProjectTest?.id}") {
            header("Authorization", "Bearer $token")
        }
        assertEquals(HttpStatusCode.OK, response.status)
    }

    private suspend fun authenticate(client: HttpClient): String?{
        val jsonBody = """{"login": "user1", "password": "22"}"""
        val responseLogin = client.post("/login") {
            contentType(ContentType.Application.Json)
            setBody(jsonBody)
        }
        return responseLogin.body<RegisterResponseRemote?>()?.tokenLong

    }
}