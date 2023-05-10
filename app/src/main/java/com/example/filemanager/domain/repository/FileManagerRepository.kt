package com.example.filemanager.domain.repository


import java.io.File

interface FileManagerRepository {
    suspend fun getFilesByDirectoryName(name: String): List<File>
    suspend fun getLastChangeFiles(): List<File>
}