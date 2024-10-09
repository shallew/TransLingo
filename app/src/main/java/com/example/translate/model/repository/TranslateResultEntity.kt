package com.example.translate.model.repository

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 *   文件名: com.example.translate.model.repository.TranslateResultEntitty
 *   @author: shallew
 *   @data: 2024/7/25 11:58
 *   @about: TODO
 */
@Entity(tableName = "TranslateResult")
data class TranslateResultEntity(
    //主键，id
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "dict")
    val dict: String?,
    @ColumnInfo(name = "l")
    val l: String,
    @ColumnInfo(name = "_query")
    val _query: String, // 源语言
    @ColumnInfo(name = "tSpeakUrl")
    val tSpeakUrl: String?, // 翻译结果发音地址
    @ColumnInfo(name = "translation")
    val translation: String, //翻译结果
    @ColumnInfo(name = "webdict")
    val webdict: String?, //webdeeplink
    @ColumnInfo(name = "translatedTime")
    var translatedTime: Long //翻译或调出的时间

)