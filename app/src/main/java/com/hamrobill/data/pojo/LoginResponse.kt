package com.hamrobill.data.pojo

import com.google.gson.annotations.SerializedName

data class LoginResponse(
        @SerializedName("access_token") val accessToken: String,
        @SerializedName("token_type") val tokenType: String,
        @SerializedName("expires_in") val expiresIn: String,
        @SerializedName("userName") val userName: String,
        @SerializedName("RoleType") val roleType: String,
        @SerializedName(".issued") val issuedAt: String,
        @SerializedName(".expires") val expiresAt: String
)