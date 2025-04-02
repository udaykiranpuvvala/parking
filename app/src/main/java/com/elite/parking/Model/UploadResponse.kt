package com.elite.parking.Model

data class UploadResponse(
    val mssg: String,
    val content: UploadContent,
    val status: Int
)

data class UploadContent(
    val url: String
)
