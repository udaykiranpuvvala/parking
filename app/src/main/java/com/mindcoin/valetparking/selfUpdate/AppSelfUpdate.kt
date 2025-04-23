package com.mindcoin.valetparking.selfUpdate

data class AppSelfUpdate(
    val mssg: String,
    val content: List<AppContent>,
    val status: Int
)
data class AppContent(
    val uuid: String,
    val appName: String,
    val packageName: String,
    val apkUrl: String,
    val verCode: Int,
    val verName: String,
    val des: String,
    val createdDate: String,
    val createdBy: String,
    val status: Int
)