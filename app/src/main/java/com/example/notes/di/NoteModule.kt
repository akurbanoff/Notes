package com.example.notes.di

import android.app.Application
import androidx.room.Room
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.notes.db.AppDatabase
import com.example.notes.db.dao.FolderDao
import com.example.notes.db.dao.NoteDao
import com.example.notes.db.migrations.MIGRATION_5_7
import com.example.notes.domain.services.NotifyRecentlyDeleted
import com.example.notes.ui.view_models.FolderViewModel
import com.example.notes.ui.view_models.NotesViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.time.Duration
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit
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
        )
            //.addMigrations(MIGRATION_5_7)
            .build()
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

    @Provides
    fun provideService(notesViewModel: NotesViewModel): NotifyRecentlyDeleted{
        return NotifyRecentlyDeleted(notesViewModel)
    }

//    @Provides
//    @Singleton
//    fun provideDateService(context: Application){
//        val currentTime = ZonedDateTime.now(ZoneId.systemDefault())
//        val nextExecutionTime = currentTime.with(LocalTime.of(0, 1))
//
//        val initialDelay = Duration.between(currentTime, nextExecutionTime).toMillis()
//
//        val duration = Duration.ofHours(24)
//        val request: WorkRequest = PeriodicWorkRequestBuilder<UpdateNoteDateTask>(duration)
//            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
//            .build()
//        WorkManager.getInstance(context).enqueue(request)
//    }
}