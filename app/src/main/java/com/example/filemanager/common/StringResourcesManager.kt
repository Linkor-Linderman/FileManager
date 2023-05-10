package com.example.filemanager.common

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StringResourcesManager @Inject constructor(
    @ApplicationContext appContext: Context
) {
    private val context: Context

    init {
        context = appContext
    }

    fun getStringResourceById(id: Int): String = context.getString(id)
}
