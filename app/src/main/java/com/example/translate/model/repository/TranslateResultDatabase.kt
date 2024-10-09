package com.example.translate.model.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 *   文件名: com.example.translate.model.repository.TranslateResultDatabase
 *   @author: shallew
 *   @data: 2024/7/25 15:20
 *   @about: TODO
 */
@Database(version = 1, entities = [TranslateResultEntity::class], exportSchema = false)
abstract class TranslateResultDatabase: RoomDatabase() {
    abstract fun translateResultDao(): TranslateResultDao
    companion object {
        private var instance: TranslateResultDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): TranslateResultDatabase {
            instance?.let{
                return it
            }
            return Room.databaseBuilder(context.applicationContext,
            TranslateResultDatabase::class.java, "translate_result_db")
                .build().apply {
                    instance = this
                }
        }
    }
}