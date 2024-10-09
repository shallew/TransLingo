package com.example.translate.model.retrofit.picture

import com.example.translate.model.network.BAI_DU_ACCESS_TOKEN
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

/**
 *   文件名: com.example.translate.model.retrofit.picture.PuctureTranslateService
 *   @author: shallew
 *   @data: 2024/7/29 10:36
 *   @about: TODO
 */
interface PictureTranslateService {

    @Multipart
    @POST("file/2.0/mt/pictrans/v1")
    fun getPictureTranslate(
        @Part image: MultipartBody.Part,
        @Part("v") v: Int = 3,
        @Part("to") toLanguage: String = "en",
        @Part("from") fromLanguage: String = "zh",
        @Query("access_token") access_token: String = BAI_DU_ACCESS_TOKEN,
        @Part("paste") paste: Int = 1
    ): Call<PictureTransResponse>

    @POST("oauth/2.0/token")
    fun getAccessToken(
        @Query("grant_type") grantType: String = "client_credentials",
        @Query("client_id") clientId: String = "h3pdvZQwtaAJuHr3wl8Wzhnc",
        @Query("client_secret") clientSecret: String = "89n135IwuwBtBiHp8RAMbcDWXmItzJqc"
    ): Call<AccessTokenResponse>

}