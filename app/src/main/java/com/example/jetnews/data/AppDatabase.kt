package com.example.jetnews.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.jetnews.data.dao.PostData
import com.example.jetnews.data.dao.PostsDao


@Database(entities = [PostData::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun postsDao(): PostsDao
}
