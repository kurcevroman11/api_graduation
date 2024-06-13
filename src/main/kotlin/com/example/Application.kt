package com.example

import com.example.dao.DatabaseFactory
import com.example.database.Dependence.DependenceController
import com.example.database.Person.PersonContriller
import com.example.database.Status.StatusContriller
import com.example.database.activity.ActivityController
import com.example.database.man_hours.ManHoursController
import com.example.database.type_of_activity.Type_of_activityContriller
import com.example.database.user.UserContriller
import com.example.db.Description.DescriptionContriller
import com.example.db.Task.TaskContriller
import com.example.db.UserRoleProject.UserRoleProjectController
import com.example.features.register.configureRegisterRouting
import com.example.plugins.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    configureAuthentication(environment.config)
    DatabaseFactory.init(environment.config)
    configureSerialization()
    configureRouting(environment.config)
    configureRegisterRouting()
    TaskContriller()
    DependenceController()
    UserContriller()
    UserRoleProjectController()
    PersonContriller()
    Type_of_activityContriller()
    StatusContriller()
    DescriptionContriller()
    ManHoursController()
    ActivityController()
    configureSwagger()
}

