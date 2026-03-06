package com.mortex.shared.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.datetime.Instant

@Dao
interface ArticleDao {

    @Query("SELECT * FROM articles")
    suspend fun getArticles(): List<ArticleEntity>?

    @Query("SELECT * FROM articles WHERE id= :id LIMIT 1")
    suspend fun getArticleDetail(id: String): ArticleEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveArticles(list: List<ArticleEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDetail(articleDetail: ArticleEntity)

    @Query("DELETE FROM articles")
    suspend fun clearArticles()

    @Query("SELECT MIN(updatedAt) FROM articles")
    suspend fun getMinLastUpdated(): Instant?

}