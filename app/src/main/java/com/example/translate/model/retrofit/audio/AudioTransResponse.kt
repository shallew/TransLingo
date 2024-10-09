package com.example.translate.model.retrofit.audio


import com.google.gson.annotations.SerializedName

data class AudioTransResponse(
    @SerializedName("error_code")
    val errorCode: Long?, // 错误码
    @SerializedName("error_msg")
    val errorMsg: String?, // 错误消息体
    @SerializedName("log_id")
    val logId: Long, // 唯一的log id，用于问题定位
    @SerializedName("result")
    val result: Result?
) {
    data class Result(
        @SerializedName("source")
        val source: String, // 今天天气不错。语音识别得到的原文
        @SerializedName("target")
        val target: String, // It's a nice day today. 翻译后的目标语言文本
        @SerializedName("target_tts")
        val targetTts: String // 译文 TTS，使用base64编码
    )
}