package com.example.database.type_of_activity

import com.google.gson.Gson
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.Type_of_activityContriller() {
    routing {
        route("/type_of_activity") {
            //Вывод всех типов деятельности
            get {
                val type_of_activityDTO = Type_of_activityModel.getType_of_activityAll()
                val gson = Gson()
                val description = gson.toJson(type_of_activityDTO)
                call.respond(description)
            }
        }
    }
}
