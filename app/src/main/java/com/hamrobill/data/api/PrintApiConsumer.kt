package com.hamrobill.data.api

import com.hamrobill.data.pojo.SaveOrderRequest
import com.hamrobill.data.pojo.SuccessResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

interface PrintApiConsumer {
    @POST("Print/PrintAPI")
    suspend fun saveTableOrders(@Body body: SaveOrderRequest): Response<String>
}