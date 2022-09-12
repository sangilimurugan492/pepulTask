package com.task.pepultask.data.api

import com.task.pepultask.data.model.request.DeleteRequest
import com.task.pepultask.data.model.request.SelectRequest
import com.task.pepultask.data.model.request.UploadRequest
import okhttp3.MultipartBody

class ApiHelper(private val apiService: ApiService) {

    suspend fun getDetails(selectRequest: SelectRequest) = apiService.getListData(selectRequest)

    suspend fun deleteDetails(deleteRequest: DeleteRequest) = apiService.deleteUser(deleteRequest)

    suspend fun uploadDetails(multipartBody: MultipartBody.Part, uploadRequest: UploadRequest) = apiService.uploadFile(multipartBody/*, uploadRequest*/)
}