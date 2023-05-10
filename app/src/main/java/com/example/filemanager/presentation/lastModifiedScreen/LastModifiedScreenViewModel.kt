package com.example.filemanager.presentation.lastModifiedScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.filemanager.R
import com.example.filemanager.common.Resource
import com.example.filemanager.common.StringResourcesManager
import com.example.filemanager.domain.model.FileModel
import com.example.filemanager.domain.useCase.FileManagerUseCases
import com.example.filemanager.domain.util.FileOrder
import com.example.filemanager.domain.util.OrderType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class LastModifiedScreenViewModel @Inject constructor(
    private val useCases: FileManagerUseCases,
    private val stringResourcesManager: StringResourcesManager
) : ViewModel() {
    private val _errorMessage = MutableLiveData<String>("")
    val errorMessage: LiveData<String> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _lastModifiedFiles = MutableLiveData<List<FileModel>>(emptyList())
    val lastModifiedFiles: LiveData<List<FileModel>> = _lastModifiedFiles

    private val _fileOrder =
        MutableLiveData<FileOrder>(FileOrder.Name(orderType = OrderType.Ascending))
    val fileOrder: LiveData<FileOrder> = _fileOrder

    private val _orderType =
        MutableLiveData<OrderType>(OrderType.Ascending)
    val orderType: LiveData<OrderType> = _orderType

    init {
        getLastModifiedFiles()
    }

    private fun getLastModifiedFiles() {
        useCases.getLastChangeFiles(
            fileOrder = _fileOrder.value!!.copy(_orderType.value!!)
        ).onEach { result ->
            when (result) {
                is Resource.Error -> {
                    _isLoading.value = false
                    _errorMessage.value = result.message ?: ""
                }
                is Resource.Loading -> {
                    _isLoading.value = true
                    _errorMessage.value = ""
                }
                is Resource.Success -> {
                    _isLoading.value = false
                    _lastModifiedFiles.value = result.data ?: emptyList()
                }
            }
        }.launchIn(viewModelScope)
    }

    fun onChangeFileOrder(fileOrder: FileOrder) {
        if (_fileOrder::class == fileOrder::class) {
            return
        }
        _fileOrder.value = fileOrder
        if (!_isLoading.value!!)
            sortFiles()
    }

    fun onChangeOrderType(orderType: OrderType) {
        if (_orderType::class == orderType::class) {
            return
        }
        _orderType.value = orderType
        if (!_isLoading.value!!)
            sortFiles()
    }

    private fun sortFiles() {
        useCases.sortFileModels(
            fileList = _lastModifiedFiles.value!!,
            fileOrder = _fileOrder.value!!.copy(_orderType.value!!)
        ).onEach { result ->
            when (result) {
                is Resource.Error -> {
                    _isLoading.value = false
                    _errorMessage.value = result.message ?: ""
                }
                is Resource.Loading -> {
                    _isLoading.value = true
                    _errorMessage.value = ""
                }
                is Resource.Success -> {
                    _isLoading.value = false
                    _lastModifiedFiles.value = result.data ?: emptyList()
                }
            }
        }.launchIn(viewModelScope)
    }

    fun onError() {
        _errorMessage.value =
            stringResourcesManager.getStringResourceById(R.string.something_went_wrong)
    }
}