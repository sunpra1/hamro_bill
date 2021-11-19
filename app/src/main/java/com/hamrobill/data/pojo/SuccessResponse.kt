package com.hamrobill.data.pojo

import com.google.gson.annotations.SerializedName

data class SuccessResponse(
    @SerializedName("IsError") val isError: Boolean,
    @SerializedName("Message") val message: ArrayList<String>
)