package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.AudioProject
import com.example.data.model.ClonedVoice
import com.example.data.model.PronunciationRule

@Database(
    entities = [AudioProject::class, PronunciationRule::class, ClonedVoice::class],
    version = 1,
    exportSchema = false
)
abstract class VocalForgeDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun pronunciationDao(): PronunciationDao
    abstract fun clonedVoiceDao(): ClonedVoiceDao

    companion object {
        @Volatile
        private var INSTANCE: VocalForgeDatabase? = null

        fun getDatabase(context: Context): VocalForgeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VocalForgeDatabase::class.java,
                    "vocalforge_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
