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

    @Query("update folders set title = :newTitle")
    fun updateFolder(newTitle: String)

    @Query("select * from folders where id = :folderId")
    fun getFolderById(folderId: Int): Folder

    @Query("select * from folders where title = :title")
    fun getFolderByTitle(title: String): Folder

    @Query("delete from folders where title = :title")
    fun deleteFolderByTitle(title: String)

    @Query("update notes set isDeleted = :isDeleted where parentFolder = :parentFolder")
    fun deleteNotesFromFolder(parentFolder: String, isDeleted: Boolean = true)

    @Query("Update folders set title = :newTitle where id = :id")
    fun renameFolderTitle(id: Int, newTitle: String)

    @Query("Update notes set parentFolder = :newParentFolder where parentFolder = :oldParentFolder")
    fun renameNotesParentFolder(oldParentFolder: String, newParentFolder: String)
}