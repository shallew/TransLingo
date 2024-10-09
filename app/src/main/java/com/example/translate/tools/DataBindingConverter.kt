package com.example.translate.tools

import android.annotation.SuppressLint
import androidx.databinding.BindingConversion
import java.text.SimpleDateFormat
import java.util.Date

/**
 *   文件名: com.example.translate.tools.DataBindingConverter
 *   @author: shallew
 *   @data: 2024/7/26 11:00
 *   @about: TODO
 */
object DataBindingConverter {
    @SuppressLint("SimpleDateFormat")
    @BindingConversion
    @JvmStatic
    fun translatedTimeConverter(time: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return sdf.format(Date(time))
    }
}