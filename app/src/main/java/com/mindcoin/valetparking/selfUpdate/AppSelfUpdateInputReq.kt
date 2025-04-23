package com.mindcoin.valetparking.selfUpdate

data class AppSelfUpdateInputReq(
    var userId: String,
    var appName: String,
    var userName: String,
    var verName: String,
    var verCode: Long,
    var osType: Int,
    var deviceInfo: String,
    var deviceId: String,
    var packageName: String
)
