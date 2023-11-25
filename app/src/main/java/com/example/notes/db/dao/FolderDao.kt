package com.example.notes.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.notes.db.models.Folder
import kotlinx.coroutines.flow.Flow

@Dao
interface FolderDao {
    @Query("select * from folders")
    fun getAll() : Flow<List<Folder>>

    @Insert
    fun createNewFolder(folder: Folder)

    @Query("select * from folders where id = :folderId")
    fun getFolderById(folderId: Int): Folder

    @Query("select * from folders where title = :title")
    fun getFolderByTitle(title: String): Folder

    @Query("delete from folders where title = :title")
    fun deleteFolderByTitle(title: String)

//    @Query("select countNotes from folders")
//    fun getCountNotes(): Int
//
//    @Query("update folders set countNotes = :newAmount")
//    fun changeAmountOfNotes(newAmount: Int)
}