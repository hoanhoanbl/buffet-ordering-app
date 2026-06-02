package com.example.appgoimon.data.model

import com.google.gson.annotations.SerializedName

data class TableRestaurant(
    val id: Int,

    @SerializedName("table_code")
    val tableCode: String,

    @SerializedName("table_name")
    val tableName: String,

    val status: String
)