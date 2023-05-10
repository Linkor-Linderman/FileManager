package com.example.filemanager

import androidx.lifecycle.ViewModel
import com.example.filemanager.common.Constants
import com.example.filemanager.common.SharedPref
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val sharedPref: SharedPref
) : ViewModel() {
    private val currentTimeMillis = System.currentTimeMillis()
    fun setCurrentLaunchTime() {
        sharedPref.setLong(Constants.LAST_TIME_LAUNCH, currentTimeMillis)
    }
}