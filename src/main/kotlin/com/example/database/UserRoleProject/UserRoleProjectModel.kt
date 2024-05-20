package com.example.db.UserRoleProject

import com.example.database.Dependence.DependenceModel
import com.example.database.Dependence.DependenceModel.getDependences
import com.example.database.Dependence.DependenceModel.getDependencesInt
import com.example.database.Person.PersonDTO
import com.example.database.Person.PersonModule
import com.example.database.Person.PersonModule.getUserInProj
import com.example.database.UserRoleProject.UserRoleProjectDTO
import com.example.db.Task.TaskDTO
import com.example.db.Task.TaskModel
import com.example.db.Task.TaskModel.collectAllTasks
import com.example.db.Task.TaskModel.getParentId
import com.example.db.Task.TaskModel.getTask
import io.ktor.http.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object UserRoleProjectModel : Table("usersroleproject") {
    val id = UserRoleProjectModel.integer("id").autoIncrement()
    val users = UserRoleProjectModel.integer("userid").references(PersonModule.id).nullable()
    val task = UserRoleProjectModel.integer("projectid").nullable()
    val type_of_activity = UserRoleProjectModel.integer("type_of_activityid").nullable()
    val score = UserRoleProjectModel.integer("score").nullable()
    val current_task = UserRoleProjectModel.integer("current_task_id").nullable()
    val creater_project = UserRoleProjectModel.integer("creater_project").nullable()

    @Serializable
    data class Calendar_plan(
        val taskId: Int,
        var userScore: Int,
        val taskName: String,
        val taskParent: Int,
        val taskScore: Int,
        val taskDependence: Int?,
        var start_date: String
    )
    @Serializable
    data class CalendarPlan(
        val taskId: Int,
        val nameTask: String,
        var execution_date: MutableList<String>,
        val haveExecuter: Boolean
    )

    fun linkinUserRootTask(userRoleProjectDTO: UserRoleProjectDTO) {
        val userRoleProjectDTO: UserRoleProjectDTO? = UserRoleProjectDTO(
            userid = userRoleProjectDTO.userid,
            current_task_id = userRoleProjectDTO.current_task_id
        )

        if (userRoleProjectDTO != null) {
            insert(userRoleProjectDTO)
        }
    }

    fun insert(userRoleProjectDTO: UserRoleProjectDTO) {
        try {
            if(userRoleProjectDTO.userid.isEmpty()){
                transaction {
                    addLogger(StdOutSqlLogger)
                    UserRoleProjectModel.insert {
                        it[task] = userRoleProjectDTO.projectid
                        it[type_of_activity] = userRoleProjectDTO.type_of_activityid
                        it[score] = userRoleProjectDTO.score
                        it[current_task] = userRoleProjectDTO.current_task_id
                        it[creater_project] = userRoleProjectDTO.creater_project
                    }
                }
            } else {
                transaction {
                    addLogger(StdOutSqlLogger)
                    UserRoleProjectModel.batchInsert(userRoleProjectDTO.userid) { userid ->
                        this[users] = userid
                        this[task] = userRoleProjectDTO.projectid
                        this[type_of_activity] = userRoleProjectDTO.type_of_activityid
                        this[score] = userRoleProjectDTO.score
                        this[current_task] = userRoleProjectDTO.current_task_id
                        this[creater_project] = userRoleProjectDTO.creater_project
                    }
                }
            }
        } catch (e: Exception) {
            println("Error in insert urp ${e}")
        }
    }

    // Получение ВСЕХ записей из таблицы UserRoleProject
    fun getURPAll(): List<UserRoleProjectDTO> {
//        return transaction {
//            UserRoleProjectModel.selectAll().map {
//                UserRoleProjectDTO(
//                    it[UserRoleProjectModel.id],
//                    it[users],
//                    it[task],
//                    it[type_of_activity],
//                    it[score],
//                    it[current_task]
//                )
//            }
//        }
        return listOf()
    }

    fun getALLUserProject(id: Int): MutableList<UserRoleProjectDTO>? {
        return transaction {
            exec("SELECT * FROM usersroleproject WHERE projectid = $id;") { rs ->
                val list = mutableListOf<UserRoleProjectDTO>()
                while (rs.next()) {
                    list.add(
                        UserRoleProjectDTO(
                            rs.getInt("id"),
                            mutableListOf(rs.getInt("userid")),
                            rs.getInt("roleid"),
                            rs.getInt("projectid"),
                            rs.getInt("type_of_activityid"),
                            rs.getInt("score"),
                            rs.getInt("current_task_id")
                        )
                    )
                }
                return@exec list
            }
        }
    }

    fun getUserProject(userID: Int): MutableList<TaskDTO>? {
        return transaction {
            exec(
                "SELECT task.id, " +
                        "task.name, " +
                        "task.status, " +
                        "to_char(task.start_data, 'YYYY-MM-DD HH24:MI:SS') as start_date, " +
                        "task.score," +
                        "task.parent, " +
                        "task.generation, " +
                        "task.typeofactivityid, " +
                        "task.position, " +
                        "(SELECT COUNT(userid) FROM usersroleproject " +
                        "WHERE projectid=task.id) as user_count FROM usersroleproject " +
                        "INNER JOIN task ON task.id = projectid WHERE userid = $userID OR creater_project = $userID;"

            ) { rs ->
                val list = mutableListOf<TaskDTO>()
                while (rs.next()) {
                    val userCount = rs.getInt("user_count")
                    list.add(
                        TaskDTO(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("status"),
                            rs.getString("start_date"),
                            rs.getInt("score"),
                            rs.getInt("parent"),
                            userCount,
                            rs.getInt("generation"),
                            null,
                            rs.getInt("typeofactivityid"),
                            rs.getInt("position"),
                        )
                    )
                }
                return@exec list
            }
        }
    }

    // Извлечение пользоваелей которые не привязаны к определенному проекту
    fun fetchFreeUsers(project_id: Int): MutableList<PersonDTO>? {
        return transaction {
            addLogger(StdOutSqlLogger)
            exec(
                "SELECT id, surname, name, patronymic, role\n" +
                        "FROM person\n" +
                        "WHERE NOT EXISTS (\n" +
                        "    SELECT 1\n" +
                        "    FROM usersroleproject\n" +
                        "    WHERE usersroleproject.userid = person.id\n" +
                        "      AND usersroleproject.projectid = ${project_id}\n" +
                        ");"

            ) { rs ->
                val list = mutableListOf<PersonDTO>()
                while (rs.next()) {
                    list.add(
                        PersonDTO(
                            id = rs.getInt("id"),
                            surname = rs.getString("surname"),
                            name = rs.getString("name"),
                            patronymic = rs.getString("patronymic"),
                            role = rs.getString("role")
                        )
                    )
                }
                return@exec list
            }
        }
    }

    // Извлечение пользоваелей которые не привязаны к определенной задаче
    fun fetchFreeUsersTask(taskId: Int): List<PersonDTO>? {
        return transaction {
            addLogger(StdOutSqlLogger)
            exec(
                "SELECT id, surname, name, patronymic, role\n" +
                        "FROM person\n" +
                        "WHERE NOT EXISTS (\n" +
                        "    SELECT 1\n" +
                        "    FROM usersroleproject\n" +
                        "    WHERE usersroleproject.userid = person.id\n" +
                        "      AND usersroleproject.current_task_id = ${taskId}\n" +
                        ");"

            ) { rs ->
                val list = mutableListOf<PersonDTO>()
                while (rs.next()) {
                    list.add(
                        PersonDTO(
                            id = rs.getInt("id"),
                            surname = rs.getString("surname"),
                            name = rs.getString("name"),
                            patronymic = rs.getString("patronymic"),
                            role = rs.getString("role")
                        )
                    )
                }

                // Получаем список пользователей в проекте
                val listUserInProj = getUserInProj(getParentId(taskId))

                // Фильтруем список list, оставляя только те элементы, которые присутствуют в listUserInProj
                val filteredList = list.filter { person ->
                    listUserInProj.any { it.id == person.id }
                }


                return@exec filteredList
            }
        }
    }

    fun getTask_executors() {
        var urp_all = getURPAll()
        for (urp in urp_all) {
            urp_all = getURPAll()
            if (urp.current_task_id == null) {
                var project = urp.projectid
                if (project != null) {
                    var task_list = TaskModel.getTaskWithChilds(project)
                    if (task_list.size > 0) {
                        var needed_gen = task_list[0].generation
                        for (task in task_list) {
                            var need_continue = false
                            for (urp2 in urp_all) {
                                if (urp2.current_task_id == task.id) {
                                    need_continue = true
                                    break
                                }
                            }
                            if (need_continue || task.generation != needed_gen || task.typeofactivityid != urp.type_of_activityid)
                                continue

                            updateURP2(urp.id!!, task.id!!)
                            break
                        }
                    }
                }
            }
        }
    }


    data class DateDoing(
        val listDate: MutableList<String>,
        var haveExecuter: Boolean
    )

     fun scheduling(projectId: Int): List<UserRoleProjectModel.CalendarPlan> {
        val list = mutableListOf<Calendar_plan>()

        val treeTask = collectAllTasks(projectId)
        treeTask.forEach { item ->
            list.add(
                Calendar_plan(
                    taskId = item.id!!,
                    taskName = item.name!!,
                    taskParent = item.parent!!,
                    taskScore = item.scope!!,
                    taskDependence = getDependencesInt(item.id!!),
                    userScore = 0,
                    start_date = item.start_date!!,
                )
            )
        }

         // Добавление кол-во часов, которые может выделить пользователели на выполение задачи
        list.forEach { item ->
            transaction {
                addLogger(StdOutSqlLogger)
                val results = UserRoleProjectModel
                    .slice(UserRoleProjectModel.score, UserRoleProjectModel.current_task, UserRoleProjectModel.id)
                    .select((UserRoleProjectModel.current_task eq item.taskId) )
                    .withDistinct()

                results.map { resultRow ->
                    val urp: UserRoleProjectDTO? = UserRoleProjectDTO(
                        current_task_id = resultRow[UserRoleProjectModel.score],
                        score = resultRow[UserRoleProjectModel.score]
                    )

                    if(urp != null) {
                        // Необходимо суммировать время выполения задачи, так как могут работать
                        // несколько пользователей над одной задачей
                        item.userScore += urp.score ?: 0
                    }
                }
            }
        }

         var res: MutableList<CalendarPlan> = mutableListOf()
         // Рассчет длительности выполнения
         for (item in list) {
             res.add(calculationPerformer(item))
         }

         val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
         for (item in list) {
             if (item.taskDependence != 0) {
                 // Получение задачи от которой зависим
                 val taskFromDependence = res.find { itemFromDependence ->
                     itemFromDependence.taskId == item.taskDependence
                 }

                 if(taskFromDependence?.execution_date != null) {
                     var dateTime: LocalDate

                     val dateOnly = taskFromDependence?.execution_date?.last()?.substring(0, 10)

                     dateTime = LocalDate.parse(dateOnly, formatter)
                     item.start_date = dateTime.plusDays(1).toString()


                     val result = calculationPerformer(item)
                     // Находим индекс элемента, который нужно заменить
                     val index = res.indexOfFirst { it.taskId == result.taskId }
                     if (index != -1) {
                         // Если элемент найден, заменяем его на результат функции calculationPerformer
                         res[index] = result
                     }
                 }
             }
         }

         val filteredList = res.filter { it.haveExecuter }
         return filteredList
    }

    fun calculationPerformer(item: Calendar_plan): CalendarPlan{
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        var taskScore = 0
        val dateDoing = DateDoing(mutableListOf(), false)

        // Нужно учитывать сколько выполнятся зависимая задача
        val depentOn = getDependencesInt(item.taskId)
        if(depentOn != 0) {
            val taskParent = getTask(depentOn!!)

            if(taskParent != null) {
                taskScore = item.taskScore - taskParent.scope!!
            }
        } else {
            taskScore = item.taskScore
        }

        var localDateTime: LocalDate
        val dateOnly = item.start_date?.substring(0, 10)

        localDateTime = LocalDate.parse(dateOnly, formatter)

        if(item.userScore != 0) {
            dateDoing.haveExecuter = true

            while (taskScore - item.userScore > 0) {
                dateDoing.listDate.add(item.start_date?.substring(0, 10)!!)
                item.start_date = localDateTime.plusDays(1).toString()
                localDateTime = LocalDate.parse(item.start_date, formatter)
                taskScore -= item.userScore
            }

            if(taskScore - item.userScore <= 0) {
                dateDoing.listDate.add(item.start_date?.substring(0, 10)!!)
            }
        } else {
            dateDoing.listDate.add(item.start_date)
            dateDoing.haveExecuter = false
        }

        val res = CalendarPlan(
            taskId = item.taskId,
            nameTask = item.taskName,
            execution_date = dateDoing.listDate,
            haveExecuter = dateDoing.haveExecuter
        )
        return res
    }

    // Список пользователей, которые уже есть в проекте
    fun fetchUserInProj(ids: MutableList<Int>, idProj: Int): MutableList<UserRoleProjectDTO?> {
        val list: MutableList<UserRoleProjectDTO?> = mutableListOf()
        transaction {
            addLogger(StdOutSqlLogger)
            ids.forEach { id ->
                val results = UserRoleProjectModel
                    .slice(UserRoleProjectModel.users, UserRoleProjectModel.task)
                    .select((UserRoleProjectModel.users eq id) and (UserRoleProjectModel.task eq idProj) )
                    .withDistinct()

                list.addAll(
                    results.map { resultRow ->
                        UserRoleProjectDTO(
                            userid = mutableListOf(resultRow[UserRoleProjectModel.users] ?: 0),
                            projectid = resultRow[UserRoleProjectModel.task]
                        )
                    }
                )
            }
        }
        return list
    }

    fun updateURP2(id: Int, task_id: Int): HttpStatusCode {
        transaction {
            val urp = UserRoleProjectModel.update({ UserRoleProjectModel.id eq (id) })
            {
                it[current_task] = task_id
            }
            if (urp > 0) {
                return@transaction HttpStatusCode.NoContent
            } else {
                return@transaction "Task with ID $id not found."
            }
        }
        return HttpStatusCode.OK
    }

    fun deleteURP(id: Int): HttpStatusCode {
        if (id != null) {
            transaction {
                val deletedRowCount = UserRoleProjectModel.deleteWhere { UserRoleProjectModel.id eq id }
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

    fun deleteURPByTask(task_id: Int): HttpStatusCode {
        if (id != null) {
            transaction {
                val deletedRowCount = UserRoleProjectModel.deleteWhere { UserRoleProjectModel.task eq task_id }
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

    fun clearFieldCreateProject(project_id: Int) {
        transaction {
            UserRoleProjectModel.update({ UserRoleProjectModel.task eq project_id }) {
                it[creater_project] = null
            }
        }
    }

    // Обновление задачи
    fun updateURP(taskId: Int, userID: Int, userRoleProjectDTO: UserRoleProjectDTO): HttpStatusCode {
        transaction {
            addLogger(StdOutSqlLogger)
            UserRoleProjectModel.update({ UserRoleProjectModel.current_task eq (taskId) and (UserRoleProjectModel.users eq userID)}){
                userRoleProjectDTO.projectid?.let { projectid -> it[UserRoleProjectModel.task] = projectid }
                userRoleProjectDTO.score?.let { score -> it[UserRoleProjectModel.score] = score }
                userRoleProjectDTO.type_of_activityid?.let { type_of_activityid -> it[UserRoleProjectModel.type_of_activity] = type_of_activityid }
                userRoleProjectDTO.current_task_id?.let { current_task_id -> it[UserRoleProjectModel.current_task] = current_task_id }
                userRoleProjectDTO.creater_project?.let { creater_project -> it[UserRoleProjectModel.creater_project] = creater_project }
            }
        }
        return HttpStatusCode.OK
    }

    fun deleteUserURP(id: Int) {
        try {
            transaction {
                UserRoleProjectModel.deleteWhere { UserRoleProjectModel.users eq id }
            }
        } catch (e:Exception) {
            println("Error in deleteUserURP ${e}")
        }
    }

    fun getURP(id: Int): UserRoleProjectDTO? {
        return try {
            transaction {
                val UrpModle = UserRoleProjectModel.select { UserRoleProjectModel.id.eq(id) }.single()
                UserRoleProjectDTO(
                    id = UrpModle[UserRoleProjectModel.id],
                    projectid = UrpModle[task],
                    type_of_activityid = UrpModle[type_of_activity],
                    score = UrpModle[score],
                    current_task_id = UrpModle[current_task]
                )
            }
        } catch (e: Exception) {
            null
        }
    }
}



