package com.example.translate.model.retrofit.english_motto

import com.example.translate.model.network.TIAN_API_KEY
import com.example.translate.model.network.YOU_DAO_APP_KEY
import com.example.translate.model.network.getCurrTime
import com.example.translate.model.network.getSign
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 *   文件名: com.example.translate.model.retrofit.english_motto.EnglishMottoService
 *   @author: shallew
 *   @data: 2024/7/29 21:28
 *   @about: TODO
 */
interface EnglishMottoService {

    @GET("enmaxim/index")
    fun getEnglishMotto(@Query("key") key: String = TIAN_API_KEY): Call<EnglishMottoResponse>

    @GET("enwords/index")
    fun getWordDefinitions(
        @Query("key") key: String = TIAN_API_KEY,
        @Query("word") word: String
    ): Call<EnglishWordResponse>
}