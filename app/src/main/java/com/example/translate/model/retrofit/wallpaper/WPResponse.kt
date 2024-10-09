package com.example.translate.model.retrofit.wallpaper


import com.google.gson.annotations.SerializedName

class WPResponse : ArrayList<WPResponse.WPResponseItem>(){
    data class WPResponseItem(
        @SerializedName("blur_hash")
        val blurHash: String, // LFC$yHwc8^$yIAS$%M%00KxukYIp
        @SerializedName("color")
        val color: String, // #6E633A
        @SerializedName("created_at")
        val createdAt: String, // 2016-05-03T11:00:28-04:00
        @SerializedName("description")
        val description: String, // A man drinking a coffee.
        @SerializedName("downloads")
        val downloads: Int, // 1345
        @SerializedName("height")
        val height: Int, // 3264
        @SerializedName("id")
        val id: String, // Dwu85P9SOIk
        @SerializedName("liked_by_user")
        val likedByUser: Boolean, // false
        @SerializedName("likes")
        val likes: Int, // 24
        @SerializedName("updated_at")
        val updatedAt: String, // 2016-07-10T11:00:01-05:00
        @SerializedName("urls")
        val urls: Urls,
        @SerializedName("width")
        val width: Int // 2448
    ) {
        data class Urls(
            @SerializedName("full")
            val full: String, // https://images.unsplash.com/photo-1417325384643-aac51acc9e5d?q=75&fm=jpg
            @SerializedName("raw")
            val raw: String, // https://images.unsplash.com/photo-1417325384643-aac51acc9e5d
            @SerializedName("regular")
            val regular: String, // https://images.unsplash.com/photo-1417325384643-aac51acc9e5d?q=75&fm=jpg&w=1080&fit=max
            @SerializedName("small")
            val small: String, // https://images.unsplash.com/photo-1417325384643-aac51acc9e5d?q=75&fm=jpg&w=400&fit=max
            @SerializedName("thumb")
            val thumb: String // https://images.unsplash.com/photo-1417325384643-aac51acc9e5d?q=75&fm=jpg&w=200&fit=max
        )
    }
}