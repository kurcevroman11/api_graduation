package com.example.database.activity

import com.example.database.activity.ActivityModel.fetchAllActivity
import com.example.database.man_hours.ManHoursModel.fetchById
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

class ActivityController(val call: ApplicationCall) {
    suspend fun getAllActivity(){
        val activity = fetchAllActivity()
        if(activity.isNotEmpty()){
            call.respond(HttpStatusCode.OK ,activity)
        } else {
            call.respond(HttpStatusCode.BadRequest)
        }
    }
}