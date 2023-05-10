package com.example.filemanager.domain.useCase

data class FileManagerUseCases(
    val getFilesByDirectoryName: GetFilesByDirectoryName,
    val getLastChangeFiles: GetLastChangeFiles
)