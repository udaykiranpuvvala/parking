package com.elite.parking.Model

data class UpdatePasswordRequest(
    val password: String,
    val userId: String,
    val newPassword: String
)
