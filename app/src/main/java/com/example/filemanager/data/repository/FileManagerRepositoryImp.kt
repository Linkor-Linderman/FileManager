package com.example.filemanager.data.repository

import android.os.Environment
import com.example.filemanager.common.Constants
import com.example.filemanager.common.SharedPref
import com.example.filemanager.data.dataSource.FileDao
import com.example.filemanager.domain.model.FileEntity
import com.example.filemanager.domain.repository.FileManagerRepository
import java.io.File
import java.security.MessageDigest
import javax.inject.Inject


class FileManagerRepositoryImp @Inject constructor(
    private val fileDao: FileDao,
    private val sharedPref: SharedPref
) : FileManagerRepository {
    override suspend fun getFilesByDirectoryName(name: String): List<File> {
        val path = Environment.getExternalStorageDirectory().toString() + "/" + name
        val directory = File(path)
        val files = directory.listFiles()
        return files?.toList() ?: emptyList()
    }

    override suspend fun getLastChangeFiles(): List<File> {
        val rootFiles = getFilesByDirectoryName("")
        val currentTimeLaunch: Long =
            sharedPref.getLong(Constants.LAST_TIME_LAUNCH) ?: System.currentTimeMillis()
        val allFiles = mutableListOf<File>()
        val currentTimeModifiedFiles = fileDao.getFileByLastAppLaunchTime(currentTimeLaunch)
        if (currentTimeModifiedFiles.isNotEmpty()) {
            return currentTimeModifiedFiles.map { File(it.path) }
        } else {

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
                        fileDao.insertFile(
                            FileEntity(
                                file.path,
                                calculateFileHash(file),
                                currentTimeLaunch
                            )
                        )
                    } else {
                        if (dbFile.lastAppLaunchTime == currentTimeLaunch) {
                            result.add(File(dbFile.path))
                        }
                    }
                } else {
                    result.add(file)
                    fileDao.insertFile(
                        FileEntity(
                            file.path,
                            calculateFileHash(file),
                            currentTimeLaunch
                        )
                    )
                }
            }
            return result.toList()
        }
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