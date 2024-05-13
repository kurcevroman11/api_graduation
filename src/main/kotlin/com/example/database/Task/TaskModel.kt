package com.example.db.Task

import com.example.database.Dependence.DependenceModel.getAllDependences
import com.example.database.Dependence.DependenceModel.getDependences
import com.example.database.Person.PersonDTO
import com.example.database.Task.TaskByID
import com.example.database.Task.TaskDependenceOn
import com.example.database.file.FileModel
import com.example.database.man_hours.ManHoursDTO
import com.example.database.man_hours.ManHoursModel.fetchById
import com.example.db.Description.DescriptionModel
import com.example.db.UserRoleProject.UserRoleProjectModel
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.jodatime.datetime
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

object TaskModel : Table("task") {

    val id = TaskModel.integer("id").autoIncrement()
    private val name = TaskModel.varchar("name", 64)
    private val status = TaskModel.integer("status").nullable()
    private val start_date = TaskModel.datetime("start_data")
    private val scope = TaskModel.integer("score").nullable()
    private val description = TaskModel.integer("descriptionid").nullable()
    val parent = TaskModel.integer("parent").nullable()
    val generation = TaskModel.integer("generation").nullable()
    private val typeofactivityid = TaskModel.integer("typeofactivityid").nullable()
    private val position = TaskModel.integer("position").nullable()
    private val content = TaskModel.text("content").nullable()

    // Полчение не/выполненных задач
    fun getDownTask(projectId: Int, statusID: Int): List<TaskDTO> {
        return try {
            transaction {
                TaskModel.select { TaskModel.parent.eq(projectId) and (TaskModel.status eq statusID) }.map {
                    TaskDTO(
                        it[TaskModel.id],
                        it[name],
                        it[status],
                        dateTimeToString(it[start_date]?.toDateTime()!!),
                        it[scope],
                        it[description],
                        it[parent],
                        null,
                        it[generation],
                        it[content],
                        it[typeofactivityid],
                        it[position],
                    )
                }
            }
        } catch (e: Exception) {
            ArrayList<TaskDTO>()
        }
    }

    // Полчение всех задач
    fun getDownTask(projectId: Int): List<TaskDTO> {
        return try {
            transaction {
                TaskModel.select { TaskModel.parent.eq(projectId) }.map {
                    TaskDTO(
                        it[TaskModel.id],
                        it[name],
                        it[status],
                        dateTimeToString(it[start_date]?.toDateTime()!!),
                        it[scope],
                        it[description],
                        it[parent],
                        null,
                        it[generation],
                        it[content],
                        it[typeofactivityid],
                        it[position],
                    )
                }
            }
        } catch (e: Exception) {
            ArrayList<TaskDTO>()
        }
    }

    fun countUser(projectId: Int): Long{
        return try {
            transaction {
                UserRoleProjectModel.select {
                    UserRoleProjectModel.current_task eq projectId
                }.count()
            }
        } catch (e: Exception) {
            0
        }
    }

    fun addUserCount(projectId: Int, statusID: Int): List<TaskDTO> {
        val list = getDownTask(projectId, statusID)
        if(list.isNotEmpty()){
            list.forEach {
                val amountUser = it.id?.let { countUser(it).toInt() }
                it.userCount = amountUser
            }
            return list
        } else {
            return list
        }
    }

    fun getTaskAll(): List<TaskDTO> = try {
        transaction {
            TaskModel.selectAll().map {
                TaskDTO(
                    it[TaskModel.id],
                    it[name],
                    it[status],
                    dateTimeToString(it[start_date]?.toDateTime()!!),
                    it[scope],
                    it[description],
                    it[parent],
                    null,
                    it[generation],
                    it[content],
                    it[typeofactivityid],
                    it[position],
                )
            }
        }
    } catch (e: Exception) {
        emptyList()
    }

    fun getTaskWithChildsInternal(parent_id: Int?, list: MutableList<TaskDTO>) {
        try {
            if (parent_id != null) {
                val all_list: MutableList<TaskDTO> = mutableListOf()
                transaction {
                    TaskModel.select { TaskModel.parent eq parent_id!! }.map {
                        val taskDTO = TaskDTO(
                            it[TaskModel.id],
                            it[name],
                            it[status],
                            dateTimeToString(it[start_date]?.toDateTime()!!),
                            it[scope],
                            it[description],
                            it[parent],
                            null,
                            it[generation],
                            it[content],
                            it[typeofactivityid],
                            it[position],
                        )
                        all_list.add(taskDTO)
                    }
                }

                list.addAll(all_list)
                for (task in all_list) {
                    getTaskWithChildsInternal(task.id, list)
                }
            }
        } catch (e: Exception) {
        }
    }

    fun getTaskWithChilds(parent_id: Int): List<TaskDTO> {
        val all_list: MutableList<TaskDTO> = mutableListOf()
        var task = getTask(parent_id)
        if (task != null)
            all_list.add(task)
        getTaskWithChildsInternal(parent_id, all_list)
        all_list.sortByDescending { it.generation }
        return all_list
    }

    fun getTask(id: Int): TaskDTO? {
        return try {
            transaction {
                val taskModle = TaskModel.select { TaskModel.id.eq(id) }.single()
                TaskDTO(
                    id = taskModle[TaskModel.id],
                    name = taskModle[name],
                    status = taskModle[status],
                    start_date = dateTimeToString(taskModle[start_date]?.toDateTime()!!),
                    scope = taskModle[scope],
                    description = taskModle[description],
                    parent = taskModle[parent],
                    userCount = null,
                    generation = taskModle[generation],
                    typeofactivityid = taskModle[typeofactivityid],
                    position = taskModle[position],
                    content = taskModle[content]
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    // Запрос для пользователя который привязан к задаче
    fun getTaskById(id: Int, userId: Int): TaskByID? {
        return transaction {
            addLogger(StdOutSqlLogger)
            exec(
                "SELECT task.id, " +
                        "task.name, " +
                        "task.status, " +
                        "to_char(task.start_data, 'YYYY-MM-DD HH24:MI:SS') as start_date, " +
                        "task.score, " +
                        "task.typeofactivityid, " +
                        "usersroleproject.score as spentTime, " +
                        "(SELECT COUNT(userid) FROM usersroleproject " +
                        "WHERE current_task_id=${id}) as user_count FROM usersroleproject " +
                        "INNER JOIN task ON task.id = ${id} WHERE task.id = ${id} AND " +
                        "usersroleproject.current_task_id = ${id} and usersroleproject.userid = ${userId};"
            ) { rs ->
                var task = TaskByID()
                while (rs.next()) {
                    runBlocking {
                        // Извлечение трудозатрат по определенной задаче
                        val manHours = fetchById(id)

                        // Извелелчение id задачи от которой есть зависимость
                        val taskIdDependence = getDependences(id)

                        var taskDependenceOn: TaskDTO? = null
                        if(taskIdDependence != null) {
                            taskDependenceOn = getTask(taskIdDependence.dependsOn)
                        }

                        task = TaskByID(
                            id = rs.getInt("id"),
                            name = rs.getString("name"),
                            status = rs.getInt("status"),
                            start_date =  rs.getString("start_date"),
                            score =  rs.getInt("score"),
                            spentTime = rs.getInt("spentTime"),
                            userCount = rs.getInt("user_count"),
                            typeofactivityid =  rs.getInt("typeofactivityid"),
                            spentedTime = totalHoursAndMinutes(manHours),
                            canAddManHours = true,
                            projectId = getParentId(id),
                            taskDependenceOn = TaskDependenceOn(
                                id = taskDependenceOn?.id,
                                name = taskDependenceOn?.name
                            ),
                            haveNotChild = getDownTask(id).isEmpty()
                        )
                    }
                }
                return@exec task
            }
        }
    }
    // Запрос для пользователя который НЕ привязан к задаче
    fun getTaskByIdNotExecuter(id: Int): TaskByID? {
        return transaction {
            addLogger(StdOutSqlLogger)
            exec(
                "SELECT id, " +
                        "name, " +
                        "status, " +
                        "to_char(start_data, 'YYYY-MM-DD HH24:MI:SS') AS start_date, " +
                        "score, " +
                        "typeofactivityid, " +
                        "(SELECT COUNT(userid) FROM usersroleproject WHERE current_task_id = ${id}) AS user_count " +
                        "FROM task WHERE id = ${id};"
            ) { rs ->
                var task = TaskByID()
                while (rs.next()) {
                    runBlocking {
                        // Извлечение трудозатрат по определенной задаче
                        val manHours = fetchById(id)

                        // Извелелчение id задачи от которой есть зависимость
                        val taskIdDependence = getDependences(id)

                        var taskDependenceOn: TaskDTO? = null
                        if(taskIdDependence != null) {
                            taskDependenceOn = getTask(taskIdDependence.dependsOn)
                        }
                        task = TaskByID(
                            id = rs.getInt("id"),
                            name = rs.getString("name"),
                            status = rs.getInt("status"),
                            start_date =  rs.getString("start_date"),
                            score =  rs.getInt("score"),
                            spentTime = 0,
                            userCount = rs.getInt("user_count"),
                            typeofactivityid =  rs.getInt("typeofactivityid"),
                            spentedTime = totalHoursAndMinutes(manHours),
                            canAddManHours = false,
                            projectId = getParentId(id),
                            taskDependenceOn = TaskDependenceOn(
                                id = taskDependenceOn?.id,
                                name = taskDependenceOn?.name
                            ),
                            haveNotChild = getDownTask(id).isEmpty()
                        )
                    }

                }
                return@exec task
            }
        }
    }

    // Подчсчет общего количества затраченно времени по трудозатратам
    fun totalHoursAndMinutes(times:  List<ManHoursDTO>): String {
        var totalHours = 0
        var totalMinutes = 0

        times.forEach { time ->
            val parts = time.hours_spent?.split(":")
            val hours = parts?.get(0)?.toInt()  // Преобразуем часы в целое число
            val minutes = parts?.get(1)?.toInt()  // Преобразуем минуты в целое число

            if (hours != null) {
                totalHours += hours
            }
            if (minutes != null) {
                totalMinutes += minutes
            }
        }

        // Если минуты больше или равны 60, конвертируем в часы
        totalHours += totalMinutes / 60
        totalMinutes %= 60  // Оставшиеся минуты после пересчета в часы

        val hours = if(totalHours.toString().length == 1){
            "0${totalHours}"
        } else {
            totalHours
        }

        val minutes = if(totalMinutes.toString().length == 1){
            "0${totalMinutes}"
        } else {
            totalMinutes
        }

        return "${hours}:${minutes}"
    }

    fun updateTask(id: Int, taskDTO: TaskDTO): HttpStatusCode {
        transaction {
            val task = TaskModel.update({ TaskModel.id eq id }) {
                taskDTO.name?.let { name -> it[TaskModel.name] = name }
                taskDTO.status?.let { status -> it[TaskModel.status] = status }
                taskDTO.scope?.let { scope -> it[TaskModel.scope] = scope }
                taskDTO.description?.let { description -> it[TaskModel.description] = description }
                taskDTO.parent?.let { parent -> it[TaskModel.parent] = parent }
                taskDTO.generation?.let { generation -> it[TaskModel.generation] = generation }
                taskDTO.typeofactivityid?.let { typeofactivityid -> it[TaskModel.typeofactivityid] = typeofactivityid }
                taskDTO.position?.let { position -> it[TaskModel.position] = position }
                taskDTO.content?.let { content -> it[TaskModel.content] = content }
            }
            if (task > 0) {
                return@transaction HttpStatusCode.NoContent
            } else {
                return@transaction "Task with ID $id not found."
            }
        }
        return HttpStatusCode.OK
    }

    fun deleteTaskInternal(id: Int): Int {
        var m_task = getTask(id)

        UserRoleProjectModel.deleteURPByTask(id)
        val deletedRowCount = TaskModel.deleteWhere { TaskModel.id eq id }
        FileModel.deleteFile(m_task?.description!!)
        DescriptionModel.deleteDescription(m_task?.description!!)
        val tasks = getTaskAll()
        for (task in tasks) {
            var parent_id = task.parent
            if (parent_id != null && parent_id == id) {
                deleteTaskInternal(task.id!!)
            }
        }
        return deletedRowCount
    }

    fun deletTask(id: Int): HttpStatusCode {
        if (id != null) {
            transaction {
                val deletedRowCount = deleteTaskInternal(id)
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

    // Получение id проекта
    fun getParentId(taskId: Int): Int {
        return try {
            transaction {
                var task = TaskModel.select { TaskModel.id.eq(taskId) }.map {
                    TaskDTO(
                        it[TaskModel.id],
                        it[name],
                        it[status],
                        dateTimeToString(it[start_date]?.toDateTime()!!),
                        it[scope],
                        it[description],
                        it[parent],
                        null,
                        it[generation],
                        it[content],
                        it[typeofactivityid],
                        it[position],
                    )
                }.single()

                while (task.generation != 1) {
                    task = TaskModel.select { TaskModel.id.eq(task.parent!!) }.map {
                        TaskDTO(
                            it[TaskModel.id],
                            it[name],
                            it[status],
                            dateTimeToString(it[start_date]?.toDateTime()!!),
                            it[scope],
                            it[description],
                            it[parent],
                            null,
                            it[generation],
                            it[content],
                            it[typeofactivityid],
                            it[position],
                        )
                    }.single()
                }

                task.id ?: 0
            }
        } catch (e: Exception) {
            0
        }
    }

    suspend fun recalculationScoreWithDependence() {
        var depenc = getAllDependences()
        depenc = depenc.reversed()
        depenc.forEach { item ->
            val taskDepent = getTask(item.dependent)

            var taskParent = getTask(taskDepent?.parent!!)
            if(taskParent == null) {
                if(taskParent?.scope!! < taskDepent.scope!!) {
                    taskParent.scope = taskDepent.scope!!
                    updateTask(taskParent.id!!, taskParent)
                }
            } else {
                while(taskParent != null) {
                    if(taskParent?.scope!! < taskDepent.scope!!) {
                        taskParent.scope = taskDepent.scope!!
                        updateTask(taskParent.id!!, taskParent)
                    }

                    if(taskParent?.parent != null ) {
                        // Движение вверх по иерарахии
                        taskParent = getTask(taskParent?.parent!!)
                    } else {
                        taskParent = null
                    }
                }
            }
        }
    }

    suspend fun recalculationScoreWithDependenceForDelete() {
        var depenc = getAllDependences()
        depenc = depenc.reversed()
        depenc.forEach { item ->
            val taskDepent = getTask(item.dependent)
            val taskDepentOn = getTask(item.dependsOn)
            taskDepent?.scope = taskDepent?.scope!! + taskDepentOn?.scope!!

            // Обновление
            updateTask(taskDepent.id!!, taskDepent)

            val taskParent = getTask(taskDepent.parent!!)
            if(taskParent?.generation == 1) {
                if(taskParent.scope!! < taskDepent.scope!!) {
                    taskParent.scope = taskDepent.scope!!
                    updateTask(taskParent.id!!, taskParent)
                }
            } else {
                while(taskParent?.generation != 1) {
                    if(taskParent?.scope!! < taskDepent.scope!!) {
                        taskParent.scope = taskDepent.scope!!
                        updateTask(taskParent.id!!, taskParent)
                    }
                }
            }
        }
    }

    fun recalculationScore(parentId: Int, generation: Int) {
        var count = 0
        while (count < generation) {
            transaction {
                addLogger(StdOutSqlLogger)

                // Обратите внимание, что используем exec для команды, которая не возвращает данные
                exec(
                    """
            WITH RECURSIVE task_tree AS (
                SELECT id, parent, score
                FROM task
                WHERE id = $parentId
                UNION ALL
                SELECT t.id, t.parent, t.score
                FROM task t
                INNER JOIN task_tree tt ON t.parent = tt.id
            ),
            task_max_scores AS (
                SELECT 
                    tt.id,
                    COALESCE(
                        (SELECT MAX(score) FROM task_tree WHERE parent = tt.id),  -- tt относится к task_tree
                        tt.score  -- если у задачи нет дочерних узлов, используется ее собственное значение
                    ) AS max_score
                FROM task_tree tt
            )
            UPDATE task
            SET score = tms.max_score
            FROM task_max_scores tms
            WHERE task.id = tms.id;
            """
                )
            }
            count++
        }
    }

    // Рекурсивная функция для получения всех задач
    // Функция для сбора всех задач в один плоский список
    fun collectAllTasks(projectId: Int): MutableList<TaskDTO> {
        val flatTasks = mutableListOf<TaskDTO>()

        fun collectTasksRecursively(parentId: Int) {
            try {
                transaction {
                    // Получаем задачи, у которых parent равен parentId
                    val currentTasks = TaskModel.select { TaskModel.parent.eq(parentId) }.map {
                        TaskDTO(
                            it[TaskModel.id],
                            it[name],
                            it[status],
                            dateTimeToString(it[start_date]?.toDateTime()!!),
                            it[scope],
                            it[description],
                            it[parent],
                            userCount = null,
                            generation = it[generation],
                            content = it[content],
                            typeofactivityid = it[typeofactivityid],
                            position = it[position],
                        )
                    }

                    // Добавляем все найденные задачи в плоский список
                    flatTasks.addAll(currentTasks)

                    // Рекурсивно обходим каждого ребенка
                    currentTasks.forEach { task ->
                        collectTasksRecursively(task.id!!)
                    }
                }
            } catch (e: Exception) {
                // Обработка ошибок
            }
        }

        // Запускаем сбор задач, начиная с корневого идентификатора
        collectTasksRecursively(projectId)

        return flatTasks
    }
}

fun dateTimeToString(dateTime: DateTime): String {
    val pattern = "yyyy-MM-dd HH:mm:ss"
    val formatter = DateTimeFormat.forPattern(pattern)
    return formatter.print(dateTime)
}

object TaskForId: IdTable<Long>("task") {
    override val id: Column<EntityID<Long>> = TaskForId.long("id").autoIncrement().entityId()
    private val name = TaskForId.varchar("name", 64)
    private val status = TaskForId.integer("status").nullable()
    private val start_date = TaskForId.datetime("start_data").autoIncrement()
    private val scope = TaskForId.integer("score").nullable()
    private val description = TaskForId.integer("descriptionid").nullable()
    private val parent = TaskForId.integer("parent").nullable()
    private val generation = TaskForId.integer("generation").nullable()
    private val typeofactivityid = TaskForId.integer("typeofactivityid").nullable()
    private val position = TaskForId.integer("position").nullable()

    override val primaryKey: PrimaryKey = PrimaryKey(id)

    fun insertandGetIdTask(taskDTO: TaskDTO): Long {
        var newTaskId: Long = 0
        transaction {
            addLogger(StdOutSqlLogger)

            newTaskId = TaskForId.insertAndGetId {
                taskDTO.name?.let { name -> it[TaskForId.name] = name }
                taskDTO.status?.let { status -> it[TaskForId.status] = status }
                taskDTO.scope?.let { scope -> it[TaskForId.scope] = scope }
                taskDTO.description?.let { description -> it[TaskForId.description] = description }
                taskDTO.parent?.let { parent -> it[TaskForId.parent] = parent }
                taskDTO.generation?.let { generation -> it[TaskForId.generation] = generation }
                taskDTO.typeofactivityid?.let { typeofactivityid -> it[TaskForId.typeofactivityid] = typeofactivityid }
                taskDTO.position?.let { position -> it[TaskForId.position] = position }
            }.value
        }
        return newTaskId
    }
}