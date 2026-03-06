package com.mortex.shared.cache

expect class DataBaseFactory {
    fun createDataBase(): ArticleDB
}