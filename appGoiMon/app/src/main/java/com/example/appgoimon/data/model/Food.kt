package com.example.appgoimon.data.model

import com.google.gson.annotations.SerializedName

data class Food(
    val id: Int,

    @SerializedName("category_id")
    val categoryId: Int,

    val name: String,
    val image: String?,
    val description: String?,
    val status: String,

    @SerializedName("category_name")
    val categoryName: String?
)