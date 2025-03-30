package com.elite.parking.apis

import com.elite.parking.Model.login.LoginResponse

interface ApiCallback {
    fun onSuccess(response: LoginResponse)
    fun onFailure(errorMessage: String)
}