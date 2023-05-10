package com.example.filemanager.data.dataSource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.filemanager.domain.model.FileEntity

@Dao
interface FileDao {
    @Query("SELECT * FROM files WHERE path = :path")
    fun getFileByPath(path: String): FileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFile(file: FileEntity)

    @Query("DELETE FROM files")
    fun deleteAllFiles()

    @Query("SELECT * FROM files")
    fun getAllFiles(): List<FileEntity>
}