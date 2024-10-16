package com.nmh.base.project.activity.data.db.demo

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nmh.base.project.helpers.MUSIC_DB

@Database(entities = [MusicEntity::class], version = 1, exportSchema = false)
abstract class MusicRoomDatabase : RoomDatabase() {

    abstract fun musicDao(): MusicDAO

    companion object {
        @Volatile
        private var INSTANCE: MusicRoomDatabase? = null

        fun getDatabase(context: Context): MusicRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance =
                    Room.databaseBuilder(context.applicationContext, MusicRoomDatabase::class.java, MUSIC_DB)
                        .allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}