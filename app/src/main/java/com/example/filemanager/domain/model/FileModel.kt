package com.example.filemanager.domain.model

import java.io.File

data class FileModel(
    val file: File,
    var fileSize: Long? = null
)
