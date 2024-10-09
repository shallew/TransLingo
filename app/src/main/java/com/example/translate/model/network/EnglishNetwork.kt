package com.example.translate.model.network

import android.util.Log
import com.example.translate.model.retrofit.english_motto.EnglishMottoService
import com.example.translate.model.retrofit.wallpaper.UnsplashService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.await
import retrofit2.converter.gson.GsonConverterFactory


/**
 *   文件名: com.example.translate.model.network.EnglishNetwork
 *   @author: shallew
 *   @data: 2024/7/30 9:57
 *   @about: TODO
 */
object EnglishNetwork {
    private val tianRetrofit = Retrofit.Builder()
        .baseUrl(TIAN_API_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val unsplashRetrofit = Retrofit.Builder()
        .baseUrl("https://api.unsplash.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    /**
     * 英语格言的服务代理
     */
    private val englishMottoService = tianRetrofit.create(EnglishMottoService::class.java)

    /**
     * 随即壁纸的服务代理
     */
    private val unsplashService = unsplashRetrofit.create(UnsplashService::class.java)

    suspend fun getAnEnglishMotto() = withContext(Dispatchers.IO) {
        try {
            englishMottoService.getEnglishMotto().await()
        } catch (e: Exception) {
            Log.d("NetworkError", "getAnEnglishMotto: $e")
            null
        }
    }

    suspend fun getWallPaper() = withContext(Dispatchers.IO) {
        try {
            unsplashService.getRandomPhoto().await()
        } catch (e: Exception) {
            Log.d("NetworkError", "getWallPaper: $e")
            null
        }
    }

    suspend fun getWordDefinition(word: String) = withContext(Dispatchers.IO) {
        try {
            englishMottoService.getWordDefinitions(word = word).await()
        } catch (e: Exception) {
            Log.d("NetworkError", "getWordDefinition: $e")
            null
        }
    }

}