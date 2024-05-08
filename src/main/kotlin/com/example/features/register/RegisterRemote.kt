package com.example.features.register
import kotlinx.serialization.Serializable

@Serializable
data class RegisterReceiveRemote(
    var login: String,
    var password: String,
    var fio: String,
    var role: String
)

@Serializable
data class RegisterResponseRemote(
    val tokenLong  : String,
    val adminOrProjectManager: Boolean? = null,
)