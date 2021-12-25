package com.hamrobill.data.pojo


import com.google.gson.annotations.SerializedName

data class ChangePasswordRequestBody(
    @SerializedName("OldPassword")
    val oldPassword: String,
    @SerializedName("NewPassword")
    val newPassword: String,
    @SerializedName("ConfirmPassword")
    val confirmPassword: String = newPassword
)