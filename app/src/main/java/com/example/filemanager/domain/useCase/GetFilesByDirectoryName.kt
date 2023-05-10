package com.example.filemanager.domain.useCase

import com.example.filemanager.R
import com.example.filemanager.common.Resource
import com.example.filemanager.common.StringResourcesManager
import com.example.filemanager.domain.model.FileModel
import com.example.filemanager.domain.repository.FileManagerRepository
import com.example.filemanager.domain.util.FileOrder
import com.example.filemanager.domain.util.OrderType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import java.util.*
import javax.inject.Inject

class GetFilesByDirectoryName @Inject constructor(
    private val repository: FileManagerRepository,
    private val stringResourcesManager: StringResourcesManager
) {
    operator fun invoke(
        fileOrder: FileOrder = FileOrder.Name(OrderType.Ascending),
        nameOfDirectory: String
    ): Flow<Resource<List<FileModel>>> =
        flow {
            emit(Resource.Loading())
            try {
                val listOfFiles = repository.getFilesByDirectoryName(nameOfDirectory)
                val listOfFileModel: List<FileModel> = listOfFiles.map { fromFileToFileModel(it) }
                val sortedListOfFileModels = when (fileOrder.orderType) {
                    is OrderType.Ascending -> {
                        when (fileOrder) {
                            is FileOrder.DateOfCreation -> listOfFileModel.sortedBy { it.file.lastModified() }
                            is FileOrder.FileExtension -> listOfFileModel.sortedBy { it.file.extension }
                            is FileOrder.Name -> listOfFileModel.sortedBy {
                                it.file.nameWithoutExtension.lowercase(
                                    Locale.ROOT
                                )
                            }
                            is FileOrder.Size -> listOfFileModel.sortedBy { it.fileSize }
                        }
                    }
                    is OrderType.Descending -> {
                        when (fileOrder) {
                            is FileOrder.DateOfCreation -> listOfFileModel.sortedByDescending { it.file.lastModified() }
                            is FileOrder.FileExtension -> listOfFileModel.sortedByDescending { it.file.extension }
                            is FileOrder.Name -> listOfFileModel.sortedByDescending {
                                it.file.nameWithoutExtension.lowercase(
                                    Locale.ROOT
                                )
                            }
                            is FileOrder.Size -> listOfFileModel.sortedByDescending { it.fileSize }
                        }
                    }
                }
                emit(
                    Resource.Success(sortedListOfFileModels)
                )
            } catch (e: Exception) {
                emit(Resource.Error(stringResourcesManager.getStringResourceById(R.string.something_went_wrong)))
            }
        }.flowOn(Dispatchers.IO)

    private fun fromFileToFileModel(file: File): FileModel {
        return if (file.isDirectory) {
            FileModel(file, getDirectorySize(file))
        } else {
            FileModel(file, file.length())
        }
    }

    private fun getDirectorySize(directory: File): Long {
        var size = 0L
        if (directory.isDirectory) {
            val files = directory.listFiles()
            if (files != null) {
                for (file in files) {
                    size += if (file.isDirectory) {
                        getDirectorySize(file)
                    } else {
                        file.length()
                    }
                }
            }
        } else {
            size = directory.length()
        }
        return size
    }
}