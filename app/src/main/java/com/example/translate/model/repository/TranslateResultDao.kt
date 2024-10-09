package com.example.translate.model.repository

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

/**
 *   文件名: com.example.translate.model.repository.TranslateResultDao
 *   @author: shallew
 *   @data: 2024/7/25 14:58
 *   @about: TODO
 */
@Dao
interface TranslateResultDao {
    /**
     * 插入翻译结果（文本翻译）
     */
    @Insert
    fun insertTextTranslateResult(transResult: TranslateResultEntity): Long

    @Update
    fun updateTextTranslateResult(transResult: TranslateResultEntity): Int

    @Delete()
    fun deleteTextTranslateResult(transResult: TranslateResultEntity): Int

    /**
     * 查找指定文本翻译结果
     */
    @Query("SELECT * FROM TranslateResult WHERE _query = :originalText AND l = :language")
    fun getTextTranslateResult(originalText: String, language: String): TranslateResultEntity?

    /**
     * 查找全部文本翻译结果
     */
    @Query("SELECT * FROM TranslateResult")
    fun getAllTextTranslateResult(): List<TranslateResultEntity>

//    @Query("DELETE FROM TranslateResult WHERE `query`=:originalText AND l=:language")
}