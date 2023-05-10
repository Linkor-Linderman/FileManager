package com.example.filemanager.data.repository

import android.content.Context
import android.os.Environment
import com.example.filemanager.data.dataSource.FileDao
import com.example.filemanager.domain.model.FileEntity
import com.example.filemanager.domain.repository.FileManagerRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.security.MessageDigest
import javax.inject.Inject


class FileManagerRepositoryImp @Inject constructor(
    @ApplicationContext context: Context,
    private val fileDao: FileDao
) : FileManagerRepository {
    override suspend fun getFilesByDirectoryName(name: String): List<File> {
        val path = Environment.getExternalStorageDirectory().toString() + "/" + name
        val directory = File(path)
        val files = directory.listFiles()
        return files?.toList() ?: emptyList()
    }

    override suspend fun getLastChangeFiles(): List<File> {
        val rootFiles = getFilesByDirectoryName("")
        val allFiles = mutableListOf<File>()
        rootFiles.forEach {
            if (it.isDirectory) {
                allFiles.addAll(getAllFiles(it))
            } else {
                allFiles.add(it)
            }
        }
        val result: MutableList<File> = mutableListOf()
        for (file in allFiles) {
            val dbFile = fileDao.getFileByPath(file.path)
            if (dbFile != null) {
                if (calculateFileHash(file) != dbFile.hash) {
                    result.add(file)
                    fileDao.insertFile(FileEntity(file.path, calculateFileHash(file)))
                }
            } else {
                result.add(file)
                fileDao.insertFile(FileEntity(file.path, calculateFileHash(file)))
            }
        }
        return result.toList()
    }

    private fun getAllFiles(directory: File): List<File> {
        val files = mutableListOf<File>()
        directory.listFiles()?.forEach {
            if (it.isDirectory) {
                files.addAll(getAllFiles(it))
            } else {
                files.add(it)
            }
        }
        return files
    }

    private fun calculateFileHash(file: File): String {
        val bytes = file.readBytes()
        val digest = MessageDigest.getInstance("SHA-256").digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
}