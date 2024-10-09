package com.example.translate.model.retrofit.audio

import com.example.translate.model.network.BAI_DU_ACCESS_TOKEN
import com.example.translate.model.network.getCurrTime
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

/**
 *   文件名: com.example.translate.model.retrofit.audio.AudioTransService
 *   @author: shallew
 *   @data: 2024/7/27 11:49
 *   @about: TODO
 */
interface AudioTranslateService {
    @POST("rpc/2.0/mt/v2/speech-translation")
    @Headers(
        "Content-Type: application/json"
    )
    fun getAudioTranslate(
        @Query("access_token") accessToken: String = BAI_DU_ACCESS_TOKEN,
        @Query("from") fromLanguage: String,
        @Query("to") toLanguage: String,
        @Query("voice") audioStr: String,
        @Query("format") format: String = "wav",
    ): Call<AudioTransResponse>

}