package com.example.translate.model.retrofit.wallpaper

import com.example.translate.tools.GlobalApplication
import com.unsplash.pickerandroid.photopicker.data.UnsplashPhoto
import com.unsplash.pickerandroid.photopicker.data.UnsplashUrls
import retrofit2.Call

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


/**
 *   文件名: com.example.translate.model.retrofit.wallpaper.UnsplashService
 *   @author: shallew
 *   @data: 2024/7/30 12:28
 *   @about: TODO
 */
interface UnsplashService {

    @Headers(
        "Authorization: Client-ID ${GlobalApplication.ACCESS_KEY}",
        "Accept-Version: v1")
    @GET("photos/random")
    fun getRandomPhoto(
        @Query("orientation") orientation: String = "portrait",
        @Query("topics") topics: String = "j2zec6kd9Vk,bo8jQKTaE0Y,Fzo3zuOHN6w",
        @Query("count") count: Int = 7
        //主题 黄金时刻 自然 壁纸 旅游
    ): Call<WPResponse>
}