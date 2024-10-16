package com.nmh.base.project.activity.di

import android.content.Context
import com.nmh.base.project.activity.data.db.demo.MusicRoomDatabase
import com.nmh.base.project.activity.data.db.demo.MusicEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DbModule {

    @Provides
    @Singleton
    fun provide(@ApplicationContext context: Context) = MusicRoomDatabase.getDatabase(context)

    @Provides
    @Singleton
    fun provideDao(db: MusicRoomDatabase) = db.musicDao()

    @Provides
    fun provideEntity() = MusicEntity()


}