package com.example.translate.model.retrofit.english_motto


import com.google.gson.annotations.SerializedName

data class EnglishMottoResponse(
    @SerializedName("code")
    val code: Int, // 200
    @SerializedName("msg")
    val msg: String, // success
    @SerializedName("result")
    val result: Result
) {
    data class Result(
        @SerializedName("en")
        val en: String, // A kiss can bring either unrest or peace to one's heart.
        @SerializedName("zh")
        val zh: String // 一个吻既可给人的心灵带来不安,也可以使它得到安宁.
    )
}