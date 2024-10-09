package com.example.translate.model.retrofit.picture


import com.google.gson.annotations.SerializedName

data class PictureTransResponse(
    @SerializedName("Image")
    val image: String, // iVBORw0KGgoAAAANSUhEUgAA......IpHha3IAAAAASUVORK5CYII=
    @SerializedName("ResponseMetadata")
    val responseMetadata: ResponseMetadata,
    @SerializedName("TextBlocks")
    val textBlocks: List<TextBlock>
) {
    data class ResponseMetadata(
        @SerializedName("Action")
        val action: String, // TranslateImage
        @SerializedName("Region")
        val region: String, // cn-north-1
        @SerializedName("RequestId")
        val requestId: String, // 20200708125936010014044066270A1763
        @SerializedName("Service")
        val service: String, // translate
        @SerializedName("Version")
        val version: String // 2020-07-01
    )

    data class TextBlock(
        @SerializedName("BackColor")
        val backColor: List<Int>,
        @SerializedName("DetectedLanguage")
        val detectedLanguage: String, // en
        @SerializedName("ForeColor")
        val foreColor: List<Int>,
        @SerializedName("Points")
        val points: List<Point>,
        @SerializedName("Text")
        val text: String, // Hello
        @SerializedName("Translation")
        val translation: String // 你好
    ) {
        data class Point(
            @SerializedName("X")
            val x: Int, // 0
            @SerializedName("Y")
            val y: Int // 0
        )
    }
}