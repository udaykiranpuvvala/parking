package com.elite.parking.Model.login

data class LoginRequest(
    val username: String,
    val password: String,
    val osType: Int
)
