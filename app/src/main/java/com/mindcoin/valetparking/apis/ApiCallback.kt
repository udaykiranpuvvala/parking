package com.mindcoin.valetparking.apis

import com.mindcoin.valetparking.Model.login.LoginResponse

interface ApiCallback {
    fun onSuccess(response: LoginResponse)
    fun onFailure(errorMessage: String)
}