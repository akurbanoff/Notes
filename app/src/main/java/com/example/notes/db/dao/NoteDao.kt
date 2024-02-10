package com.example.notes.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.notes.db.models.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("Select * from notes where parentFolder = :parentFolder and isDeleted = :isDeleted and isPinned = :isPinned")
    fun getAll(parentFolder: String, isDeleted: Boolean = false, isPinned: Boolean = false): Flow<List<Note>>

    @Query("select * from notes where isDeleted = :isDeleted and isPinned = :isPinned")
    fun getAll(isDeleted: Boolean, isPinned: Boolean = false): Flow<List<Note>>

    @Query("select * from notes where isShared = :isShared")
    fun getAllShared(isShared: Boolean): Flow<List<Note>>

    @Query("INSERT INTO notes(title, date, firstLine, textBody, parentFolder, isDeleted, isShared) VALUES (:title, :date, :firstLine, :textBody, :parentFolder, :isDeleted, :isShared)")
    fun createNewNote(title: String, date: String, parentFolder: String, textBody: String, firstLine: String, isDeleted: Boolean = false, isShared: Boolean = false)

    @Query("UPDATE notes SET title = :title, firstLine = :firstLine, date = :date, textBody = :textBody WHERE id = :id AND parentFolder = :parentFolder")
    fun updateNote(id: Int, title: String, firstLine: String, date: String, textBody: String, parentFolder: String)

    @Query("update notes set isDeleted = :isDeleted where id = :id")
    fun changeNoteStatusToDeleted(id: Int, isDeleted: Boolean = true)

    @Query("Select count(id) from notes where parentFolder = :title and isDeleted = :isDeleted")
    fun getNotesAmount(title: String, isDeleted: Boolean = false): Flow<Int>

    @Query("select count(id) from notes where isDeleted = :isDeleted")
    fun getDeletedNotesAmount(isDeleted: Boolean = true): Flow<Int>

    @Query("select count(id) from notes where isShared = :isShared")
    fun getSharedNotesAmount(isShared: Boolean = true): Flow<Int>

    @Query("select count(id) from notes where isDeleted = :isDeleted")
    fun getAlliCloudNotesAmount(isDeleted: Boolean = false): Flow<Int>

    @Query("select * from notes where id = :noteId AND parentFolder = :folderTitle")
    fun getNoteById(noteId: Int, folderTitle: String): Note

    @Query("select * from notes where title = :title")
    fun getNoteByTitle(title: String): Note

    @Query("update notes set title = :title where id = :id")
    fun updateNoteTitleById(id: Int, title: String)

    @Query("update notes set textBody = :body where id = :id")
    fun updateNoteBodyById(id: Int, body: String)

    @Query("update notes set date = :date where id = :id")
    fun updateNoteDateById(id: Int, date: String)

    @Query("delete from notes where id = :id")
    fun deleteNoteById(id: Int)

    @Query("delete from notes where title = :title")
    fun deleteNoteByTitle(title: String)

    @Query("SELECT * FROM notes WHERE parentFolder = :parentFolder ORDER BY date")
    fun sortNoteByDateCreated(parentFolder: String): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE parentFolder = :parentFolder ORDER BY title")
    fun sortNoteByTitle(parentFolder: String): Flow<List<Note>>

    @Query("update notes set isDeleted = :isDeleted, parentFolder = :parentFolder where id = :id")
    fun recoverNote(id: Int, isDeleted: Boolean = false, parentFolder: String = "Notes")

    @Query("update notes set isPinned = :isPinned where id = :id")
    fun pinNote(id: Int, isPinned: Boolean = true)

    @Query("update notes set isPinned = :isPinned where id = :id")
    fun unpinNote(id: Int, isPinned: Boolean = false)
}