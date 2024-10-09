package com.example.translate.model.network

import android.util.Log
import com.example.translate.model.retrofit.audio.AudioTranslateService
import com.example.translate.model.retrofit.picture.PictureTranslateService
import com.example.translate.model.retrofit.text.TextTransResponse
import com.example.translate.model.retrofit.text.TextTranslateService
import retrofit2.Retrofit
import retrofit2.await
import retrofit2.converter.gson.GsonConverterFactory

/**
 * 网络请求类 声明为单例类
 */
object TranslateNetwork {


    /**
     * retrofit实例
     */
    private val ydRetrofit = Retrofit.Builder()
        .baseUrl(YOU_DAO_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    /**
     * retrofit实例
     */
    private val bdRetrofit = Retrofit.Builder()
        .baseUrl(BAI_DU_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    /**
     * 文本翻译服务动态代理
     */
    private val textTransService = ydRetrofit.create(TextTranslateService::class.java)

    /**
     * 语音翻译服务动态代理
     */
    private val audioTransService = bdRetrofit.create(AudioTranslateService::class.java)

    /**
     * 图片翻译服务
     */
    private val pictureTransService = bdRetrofit.create(PictureTranslateService::class.java)

    /**
     * 对外暴露：发起文本翻译请求的方法
     * return：返回网络请求响应，可以为null
     */
    suspend fun textTranslateRequest(
        originText: String, fromLanguage: String, toLanguage: String
    ): TextTransResponse? {
        val curTime = (getCurrTime() / 1000).toString()
        val salt = getCurrTime().toString()
        val sb = StringBuilder().run {
            append(YOU_DAO_APP_KEY)
            append(calculateInput(originText))
            append(salt)
            append(curTime)
            append(YOU_DAO_APP_SECRET)
        }
        val sign = getSign(sb.toString())
        if (sign != null) {
            return try {
                //请求结果成功
                textTransService.getTextTranslate(
                    originText, fromLanguage, toLanguage,
                    YOU_DAO_APP_KEY, salt, sign, YOU_DAO_SIGN_TYPE, curTime
                ).await()

            } catch (e: RuntimeException) {
                Log.d("NetworkError", "textTranslateRequest: $e")
                null
            }
        } else {
            Log.d("SignIsNull", "textTranslateRequest: 签名为空，不能进行请求")
            return null
        }
    }

//    suspend fun audioTranslateRequest(
//        audioStr: String,
//        fromLanguage: String,
//        toLanguage: String
//    ): AudioTransResponse? {
//        Log.d("RecordingDebug", "Model开始网络请求，$audioStr")
//        return try {
//            //请求结果成功
//            val response = audioTransService.getAudioTranslate(
//                audioStr = audioStr,
//                fromLanguage = fromLanguage,
//                toLanguage = toLanguage
//            ).await()
//            Log.d("gcxyuiwugyecx", "Model await返回的结果 $response")
//            response
//        } catch (e: Exception) {
//            Log.e("audioTranslateRequest", "audioTranslateRequest: $e")
//            null
//        }
//    }

//    suspend fun pictureTranslateRequest(
//        imageFile: File,
//        fromLanguage: String,
//        toLanguage: String
//    ): PictureTransResponse? {
//        Log.d("pictureDebug", "pictureTranslateRequest:")
//        //先请求access_token
//        val accessToken = pictureTransService.getAccessToken().await()
//        try {
//            val await = pictureTransService.getPictureTranslate(
//                image = getImagePart(imageFile),
//                access_token = accessToken?.accessToken.toString()
//            ).await()
//            Log.d("pictureDebug", "pictureTranslateRequest: $await")
//            return await
//        } catch (e: Exception) {
//            Log.d("NetworkException", "pictureTranslateRequest: $e")
//            return null
//        }
//    }

}