package com.task.pepultask.data.api

import com.task.pepultask.data.model.request.DeleteRequest
import com.task.pepultask.data.model.response.FileUploadSuccess
import com.task.pepultask.data.model.request.SelectRequest
import com.task.pepultask.data.model.request.UploadRequest
import com.task.pepultask.data.model.response.SelectSuccess
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*


interface ApiService {

    @POST("select.php")
    suspend fun getListData(@Body params : SelectRequest): SelectSuccess

    @Multipart // POST request to upload an image from storage
    @POST("uploader.php")
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun uploadFile(@Part fileToUpload: MultipartBody.Part?/*, @Body params: UploadRequest*/): Call<FileUploadSuccess>

    @POST("delete.php")
    fun deleteUser(@Body params: DeleteRequest): Call<FileUploadSuccess>


}