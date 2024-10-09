package com.example.translate.view

import android.text.SpannableString
import android.util.LruCache

/**
 *   文件名: com.example.translate.view.ShareableData
 *   @author: shallew
 *   @data: 2024/7/31 10:29
 *   @about: TODO
 */

data class EnglishMottoAndWallPaper(
    val mottoEn: String, //英文
    val mottoZh: String, //中文
    val wallPaper: String, //壁纸url
    val spannableString: SpannableString //分词字符串

)

val englishMottoList = mutableListOf<EnglishMottoAndWallPaper>()

var cacheSize = 12 * 1024 * 1024 //12M
//匿名对象表达式 创建了一个继承子LruCache的匿名对象实例，并重写了sizeOf和entryRemoved方法
val dictionaryLruCache = object : LruCache<String, String>(cacheSize) {
    override fun sizeOf(key: String?, value: String?): Int {
        return super.sizeOf(key, value)
    }

    override fun entryRemoved(
        evicted: Boolean,
        key: String?,
        oldValue: String?,
        newValue: String?
    ) {
        super.entryRemoved(evicted, key, oldValue, newValue)
    }
}




