package com.task.pepultask.data.model.response

import com.google.gson.annotations.SerializedName


data class SelectSuccess (
    @SerializedName("statusCode") val statusCode : Int,
    @SerializedName("message") val message : String,
    @SerializedName("data") val data : List<Data>
)

data class Data (
    @SerializedName("id") val id : Int,
    @SerializedName("file") val file : String,
    @SerializedName("file_type") val file_type : Int
)