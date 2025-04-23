package com.mindcoin.valetparking.Model.login

data class LoginResponse(
    val mssg: String,
    val content: List<User>,
    val status: Int
)

data class User(
    val uuid: String,
    val name: String,
    val actionId: Int,
    val address: String,
    val mobileNumber: String,
    val companyId: String,
    val email: String,
    val roleId: Int,
    val password: String,
    val designation: String,
    val status: Int,
    val createdDate: String,
    val createdBy: String,
    val token: String
)
