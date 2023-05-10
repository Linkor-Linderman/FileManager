package com.example.filemanager.domain.useCase

import com.example.filemanager.R
import com.example.filemanager.common.Resource
import com.example.filemanager.common.StringResourcesManager
import com.example.filemanager.domain.model.FileModel
import com.example.filemanager.domain.util.FileOrder
import com.example.filemanager.domain.util.OrderType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.*
import javax.inject.Inject

class SortFileModels @Inject constructor(
    private val stringResourcesManager: StringResourcesManager
) {
    operator fun invoke(
        fileList: List<FileModel>,
        fileOrder: FileOrder = FileOrder.Name(OrderType.Ascending)
    ): Flow<Resource<List<FileModel>>> =
        flow {
            emit(Resource.Loading())
            try {
                emit(Resource.Success(when (fileOrder.orderType) {
                    is OrderType.Ascending -> {
                        when (fileOrder) {
                            is FileOrder.DateOfCreation -> fileList.sortedBy { it.file.lastModified() }
                            is FileOrder.FileExtension -> fileList.sortedBy { it.file.extension }
                            is FileOrder.Name -> fileList.sortedBy {
                                it.file.nameWithoutExtension.lowercase(
                                    Locale.ROOT
                                )
                            }
                            is FileOrder.Size -> fileList.sortedBy { it.fileSize }
                        }
                    }
                    is OrderType.Descending -> {
                        when (fileOrder) {
                            is FileOrder.DateOfCreation -> fileList.sortedByDescending { it.file.lastModified() }
                            is FileOrder.FileExtension -> fileList.sortedByDescending { it.file.extension }
                            is FileOrder.Name -> fileList.sortedByDescending {
                                it.file.nameWithoutExtension.lowercase(
                                    Locale.ROOT
                                )
                            }
                            is FileOrder.Size -> fileList.sortedByDescending { it.fileSize }
                        }
                    }
                }))
            } catch (e: Exception) {
                emit(Resource.Error(stringResourcesManager.getStringResourceById(R.string.something_went_wrong)))
            }
        }.flowOn(Dispatchers.IO)
}