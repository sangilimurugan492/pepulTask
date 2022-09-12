package com.task.pepultask.data.repository

import com.task.pepultask.data.api.ApiHelper
import com.task.pepultask.data.model.request.DeleteRequest
import com.task.pepultask.data.model.request.SelectRequest
import com.task.pepultask.data.model.request.UploadRequest
import okhttp3.MultipartBody

class MainRepository(private val apiHelper: ApiHelper) {

    suspend fun getDetails(selectRequest: SelectRequest) = apiHelper.getDetails(selectRequest)

    suspend fun deleteDetails(deleteRequest: DeleteRequest) = apiHelper.deleteDetails(deleteRequest)

    suspend fun uploadDetails(multipartBody: MultipartBody.Part, uploadRequest: UploadRequest) = apiHelper.uploadDetails(multipartBody, uploadRequest)
}