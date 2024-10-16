package com.nmh.base.project.activity.data.repository

import com.nmh.base.project.activity.data.db.demo.MusicDAO
import com.nmh.base.project.activity.data.db.demo.MusicEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DataRepository @Inject constructor(val musicDao: MusicDAO) {

     fun getAllMusic() : Flow<MutableList<MusicEntity>> {
          return musicDao.getAllMusic()
     }

     fun getAllMusicFromAlbum(name: String): Flow<MutableList<MusicEntity>> {
          return musicDao.getAllMusicFromAlbum(name)
     }

     fun getAllMusicFromText(str: String): Flow<MutableList<MusicEntity>> {
          return musicDao.getAllMusicFromText(str)
     }

     fun getAllMusicFavorite(): Flow<MutableList<MusicEntity>> {
          return musicDao.getAllMusicFavorite(true)
     }

     suspend fun getMusicWithId(id: Long) = musicDao.getMusicWithId(id)

     fun insert(musicEntity: MusicEntity): Long = musicDao.insert(musicEntity)

     fun updateWithId(id: Long, newID: Long) = musicDao.update(id, newID)

     fun updateFavorite(id: Long, isFavorite: Boolean) = musicDao.updateFavoriteMusic(id, isFavorite)

     fun deleteDB() = musicDao.deleteDB()
}