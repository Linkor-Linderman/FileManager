package com.example.filemanager.presentation.permissionScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PermissionScreenViewModel : ViewModel() {

    private val _isPermissionsGranted = MutableLiveData(false)
    val isPermissionsGranted: LiveData<Boolean> = _isPermissionsGranted

    fun allowPermission() {
        _isPermissionsGranted.value = true
    }
}