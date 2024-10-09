package com.example.translate.model.retrofit.text

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 *   项目名: HeartTrip
 *   文件名: com.example.translate.model.retrofit.text.TextTranslateService
 *   @author: shallew
 *   @data: 2024/7/23 20:37
 *   @about: TODO
 */
interface TextTranslateService {

    @GET("api")
    fun getTextTranslate(@Query("q") originText: String,
                         @Query("from") fromLanguage: String,
                         @Query("to") toLanguage: String,
                         @Query("appKey") appKey: String,
                         @Query("salt") salt: String,
                         @Query("sign") sign: String,
                         @Query("signType") signType: String,
                         @Query("curtime") curtime: String
    ): Call<TextTransResponse>
}