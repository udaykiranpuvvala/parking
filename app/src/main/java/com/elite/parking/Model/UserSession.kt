package com.elite.parking.Model

object UserSession {
    var uuid: String? = null
    var name: String? = null
    var mobileNumber: String? = null
    var email: String? = null
    var token: String? = null
    var address: String? = null
    var companyId: String? = null
    var roleId: Int? = null
    var designation: String? = null
    var status: Int? = null

    fun clearSession() {
        uuid = null
        name = null
        mobileNumber = null
        email = null
        token = null
        address = null
        companyId = null
        roleId = null
        designation = null
        status = null
    }
}