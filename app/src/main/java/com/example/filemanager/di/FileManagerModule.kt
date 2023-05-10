package com.example.filemanager.di

import android.app.Application
import androidx.room.Room
import com.example.filemanager.common.SharedPref
import com.example.filemanager.common.StringResourcesManager
import com.example.filemanager.data.dataSource.FileDatabase
import com.example.filemanager.data.repository.FileManagerRepositoryImp
import com.example.filemanager.domain.repository.FileManagerRepository
import com.example.filemanager.domain.useCase.FileManagerUseCases
import com.example.filemanager.domain.useCase.GetFilesByDirectoryName
import com.example.filemanager.domain.useCase.GetLastChangeFiles
import com.example.filemanager.domain.useCase.SortFileModels
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FileManagerModule {
    @Provides
    @Singleton
    fun provideFileDataBase(app: Application): FileDatabase {
        return Room.databaseBuilder(
            app,
            FileDatabase::class.java,
            FileDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideRepository(
        db: FileDatabase,
        sharedPref: SharedPref
    ): FileManagerRepository {
        return FileManagerRepositoryImp(db.fileDao, sharedPref)
    }

    @Provides
    @Singleton
    fun provideFileManagerUseCases(
        repository: FileManagerRepository,
        stringResourcesManager: StringResourcesManager
    ): FileManagerUseCases {
        return FileManagerUseCases(
            getFilesByDirectoryName = GetFilesByDirectoryName(repository, stringResourcesManager),
            getLastChangeFiles = GetLastChangeFiles(repository, stringResourcesManager),
            sortFileModels = SortFileModels(stringResourcesManager)
        )
    }
}