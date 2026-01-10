package com.mortex.shared.cache

import android.content.Context
import androidx.room.Room

actual class DataBaseFactory(
    private val context: Context
) {
    actual fun createDataBase(): ArticleDB {
        return Room.databaseBuilder(
            context = context.applicationContext,
            ArticleDB::class.java,
            "help_articles.db"
        ).build()
    }
}