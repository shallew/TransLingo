package com.example.translate.tools

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.unsplash.pickerandroid.photopicker.UnsplashPhotoPicker
import com.unsplash.pickerandroid.photopicker.data.UnsplashPhoto

/**
 *   文件名: com.example.translate.tools.GlobalApplication
 *   @author: shallew
 *   @data: 2024/7/25 15:54
 *   @about: TODO
 */
class GlobalApplication : Application() {
    /**
     * 类似实例变量的用法
     */
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

        val languages = listOf(
            "自动识别" to "auto",
            "阿拉伯语" to "ar",
            "德语" to "de",
            "英语" to "en",
            "西班牙语" to "es",
            "法语" to "fr",
            "印地语" to "hi",
            "印度尼西亚语" to "id",
            "意大利语" to "it",
            "日语" to "ja",
            "韩语" to "ko",
            "荷兰语" to "nl",
            "葡萄牙语" to "pt",
            "俄语" to "ru",
            "泰语" to "th",
            "越南语" to "vi",
            "简体中文" to "zh-CHS",
            "繁体中文" to "zh-CHT",
            "南非荷兰语" to "af",
            "白俄罗斯语" to "be",
            "保加利亚语" to "bg",
            "孟加拉语" to "bn",
            "波斯尼亚语" to "bs",
            "丹麦语" to "da",
            "世界语" to "eo",
            "芬兰语" to "fi",
            "蒙古语" to "mn",
            "马来语" to "ms",
            "粤语" to "yue"
        )
        const val ACCESS_KEY = "HY0utSDcLqvtKp7hndI6Cmim69HvHzjzh-sG8OkR6-0"
        const val SECRET_KEY = "9uxfaJ7KEApOapKAh3Jv8lK7fV2vafVcLb_kkmutwbc"
    }


    override fun onCreate() {
        super.onCreate()
        context = applicationContext



    }
}