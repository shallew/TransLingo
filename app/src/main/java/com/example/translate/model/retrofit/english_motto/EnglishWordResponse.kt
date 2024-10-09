package com.example.translate.model.retrofit.english_motto


import com.google.gson.annotations.SerializedName

data class EnglishWordResponse(
    @SerializedName("code")
    val code: Int, // 200
    @SerializedName("msg")
    val msg: String, // success
    @SerializedName("result")
    val result: Result?
) {
    data class Result(
        @SerializedName("content")
        val content: String, // n:简编,字汇,字书,词典,词库,辞典,概要
        @SerializedName("word")
        val word: String // lexicon
    )
}