package com.example.notes.di

import android.app.Application
import androidx.room.Room
import com.example.notes.db.AppDatabase
import com.example.notes.db.dao.FolderDao
import com.example.notes.db.dao.NoteDao
import com.example.notes.ui.view_models.FolderViewModel
import com.example.notes.ui.view_models.NotesViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NoteModule {
    @Provides
    @Singleton
    fun provideDatabase(applicationContext: Application) : AppDatabase{
        return Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "note_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideFolderDao(db: AppDatabase): FolderDao{
        return db.folderDao
    }

    @Provides
    @Singleton
    fun provideNoteDao(db: AppDatabase): NoteDao {
        return db.noteDao
    }

    @Provides
    @Singleton
    fun provideFolderViewModel(dao: FolderDao) : FolderViewModel{
        return FolderViewModel(dao)
    }

    @Provides
    @Singleton
    fun provideNoteViewModel(dao: NoteDao) : NotesViewModel {
        return NotesViewModel(dao)
    }
}