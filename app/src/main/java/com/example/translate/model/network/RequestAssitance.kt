
package com.example.translate.model.network

import android.util.Log
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 *   文件名: com.example.translate.model.network.RequestAssitance
 *   @author: shallew
 *   @data: 2024/7/24 15:30
 *   @about: TODO 协助生成一些网络请求需要传递的参数
 */

/**
 * 获取当前时间
 */
fun getCurrTime() = System.currentTimeMillis()

/**
 * sha256加密获取签名sign
 */
fun getSign(string: String?): String? {
    if (string == null) {
        return null
    }
    val hexDigits = charArrayOf(
        '0', '1', '2', '3', '4', '5',
        '6', '7', '8', '9', 'A', 'B',
        'C', 'D', 'E', 'F'
    )
    val btInput = string.toByteArray(StandardCharsets.UTF_8)
    return try {
        val mdInst = MessageDigest.getInstance("SHA-256")
        mdInst.update(btInput)
        val md = mdInst.digest()
        val j = md.size
        val str = CharArray(j * 2)
        var k = 0
        for (byte0 in md) {
            str[k++] = hexDigits[byte0.toInt() ushr 4 and 0xf]
            str[k++] = hexDigits[byte0.toInt() and 0xf]
        }
        String(str)
    } catch (e: NoSuchAlgorithmException) {
        null
    }
}

fun calculateInput(q: String?): String? {
    if (q == null) {
        return null
    }
    val len = q.length
    return if (len <= 20) q else q.substring(0, 10) + len + q.substring(len - 10, len)
}

/**
 * 百度的access_token
 */
const val BAI_DU_ACCESS_TOKEN = "25.783c0ab2f6aebc2626fefc78146a0b15.315360000.2037600075.282335-99505698"

/**
 * 获取文件MultipartBody.Part
 */
fun getImagePart(imageFile: File): MultipartBody.Part {
    val mediaType = MediaType.parse("image/png")
    val requestBody = RequestBody.create(mediaType, imageFile)
    return MultipartBody.Part.createFormData("image", imageFile.name, requestBody)
}

/**
 * 天聚的key
 */
const val TIAN_API_KEY = "c62a795d529728b9fe69b1e158a517a1"

/**
 * 网络请求回调模板方法 通过泛型都可以调用
 */
suspend fun <T> Call<T>.await(): T {
    return suspendCoroutine { continuation ->
        enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                val body = response.body()
                Log.d("gcxyuiwugyecx", "Model网络请求的Response ${response.code()}, $body")
                if (body != null) {
                    continuation.resume(body)
                } else {
                    continuation.resumeWithException(RuntimeException("body == null"))
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                Log.d("gcxyuiwugyecx", "Model网络请求的$t")
                continuation.resumeWithException(t)
            }
        })
    }
}
const val YOU_DAO_BASE_URL = "https://openapi.youdao.com/"
const val BAI_DU_BASE_URL = "https://aip.baidubce.com/"
const val YOU_DAO_APP_KEY = "42a56020dea4bc50"
const val YOU_DAO_APP_SECRET = "3hT3Dozc2nhMIa7JAtX0n81B7tsnW8qA"
const val YOU_DAO_SIGN_TYPE = "v3"
const val TIAN_API_BASE_URL = "https://apis.tianapi.com/"

