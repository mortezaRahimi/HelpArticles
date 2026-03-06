package com.mortex.shared.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mortex.shared.util.RoomConverters

@Database(
    entities = [ArticleEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(RoomConverters::class)
abstract class ArticleDB : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
}