package com.example.translate.model.retrofit.text


import com.google.gson.annotations.SerializedName

data class TextTransResponse(
    @SerializedName("dict")
    val dict: Dict, //词典deeplink
    @SerializedName("errorCode")
    val errorCode: String = "", // 错误返回码
    @SerializedName("isDomainSupport")
    val isDomainSupport: String? = "", // 翻译结果是否为领域翻译(仅开通领域翻译时存在)
    @SerializedName("l")
    var l: String, // 源语言和目标语言 "EN2zh-CHS"
    @SerializedName("query")
    val query: String, // 源语言
    @SerializedName("speakUrl")
    val speakUrl: String? = "", // 源语言发音地址
    @SerializedName("tSpeakUrl")
    val tSpeakUrl: String?, // 翻译结果发音地址
    @SerializedName("translation")
    val translation: List<String>, //翻译结果
    @SerializedName("webdict")
    val webdict: Webdict //webdeeplink
) {
    data class Dict(
        @SerializedName("url")
        val url: String?
    )

    data class Webdict(
        @SerializedName("url")
        val url: String?
    )
}