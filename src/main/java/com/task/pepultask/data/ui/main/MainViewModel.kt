package com.task.pepultask.data.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.task.pepultask.data.model.request.DeleteRequest
import com.task.pepultask.data.model.request.SelectRequest
import com.task.pepultask.data.model.request.UploadRequest
import com.task.pepultask.data.repository.MainRepository
import com.task.pepultask.utils.Resource
import kotlinx.coroutines.Dispatchers
import okhttp3.MultipartBody

class MainViewModel(private val mainRepository: MainRepository) : ViewModel() {

    fun getDetails(selectRequest: SelectRequest) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getDetails(selectRequest)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun deleteDetails(deleteRequest: DeleteRequest) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.deleteDetails(deleteRequest)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun uploadDetails(multipartBody: MultipartBody.Part, uploadRequest: UploadRequest) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.uploadDetails(multipartBody, uploadRequest)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
}