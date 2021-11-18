package com.hamrobill.data.pojo

import com.google.gson.annotations.SerializedName

data class LoginRequest(
        @SerializedName("username") val username: String,
        val password: String,
        @SerializedName("grant_type") val grantType: String = "password"
) {
    fun getHashMap(): HashMap<String, Any> {
        return HashMap<String, Any>().apply {
            LoginRequest::class.java.declaredFields.forEach {
                put(
                        if (it.getAnnotation(SerializedName::class.java) != null) it.getAnnotation(
                                SerializedName::class.java
                        )!!.value else it.name, it.get(this@LoginRequest)!!
                )
            }
        }
    }
}