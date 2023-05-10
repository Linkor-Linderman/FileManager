package com.example.filemanager.data.dataSource

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.filemanager.domain.model.FileEntity

@Database(
    entities = [FileEntity::class],
    version = 1
)
abstract class FileDatabase: RoomDatabase() {
    abstract val fileDao: FileDao

    companion object {
        const val DATABASE_NAME = "files_db"
    }
}