package com.example.notes.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.example.notes.db.models.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("Select * from notes where parentFolder = :parentFolder and isDeleted = :isDeleted")
    fun getAll(parentFolder: String, isDeleted: Boolean = false): Flow<List<Note>>

    @Query("select * from notes where isDeleted = :isDeleted")
    fun getAll(isDeleted: Boolean): Flow<List<Note>>

    @Query("select * from notes where isShared = :isShared")
    fun getAllShared(isShared: Boolean): Flow<List<Note>>

    @Query("INSERT INTO notes(title, date, firstLine, textBody, parentFolder, isDeleted, isShared) VALUES (:title, :date, :firstLine, :textBody, :parentFolder, :isDeleted, :isShared)")
    fun createNewNote(title: String, date: String, parentFolder: String, textBody: String, firstLine: String, isDeleted: Boolean = false, isShared: Boolean = false)

    @Query("UPDATE notes SET title = :title, firstLine = :firstLine, date = :date, textBody = :textBody WHERE id = :id AND parentFolder = :parentFolder")
    fun updateNote(id: Int, title: String, firstLine: String, date: String, textBody: String, parentFolder: String)

    @Query("update notes set isDeleted = :isDeleted where id = :id")
    fun changeNoteStatusToDeleted(id: Int, isDeleted: Boolean = true)

    @Query("Select count(id) from notes where parentFolder = :title")
    fun getNotesAmount(title: String): Flow<Int>

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
}