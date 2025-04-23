package com.mindcoin.valetparking.Model

data class LogoutRequest(
    val userId: String,
    val activityTime: String,
    val osType: Int
)
