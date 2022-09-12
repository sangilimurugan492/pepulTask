package com.task.pepultask.data.model.response

import com.google.gson.annotations.SerializedName

data class FileUploadSuccess (
    @SerializedName("result") var result : String?,
    @SerializedName("file_type") var file_type : Int?,
    @SerializedName("message") var message : String?,
    @SerializedName("statusCode") var statusCode : Int?
)