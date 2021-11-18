package com.hamrobill.data.api

import com.hamrobill.data.pojo.*
import retrofit2.Response
import retrofit2.http.*

interface HamrobillAPIConsumer {
    @FormUrlEncoded
    @POST("Token")
    suspend fun get(
            @FieldMap body: HashMap<String, Any>
    ): Response<LoginResponse>

    @GET("api/SetupTable/GetTableList")
    suspend fun getTableList(): Response<GetTableResponse>

    @GET("api/SetupItem/GetItemList")
    suspend fun getFoodItems(): Response<GetFoodItemResponse>

    @GET("api/SetupSubItem/GetSubItemListByItemId/{foodItemId}")
    suspend fun getFoodSubItems(@Path("foodItemId") foodItemId: Int): Response<GetFoodSubItemResponse>

    @GET("api/OrderItem/GetOrderItemByTableId/{tableID}/Table")
    suspend fun getTableActiveOrders(@Path("tableID") foodTableId: Int): Response<GetTableOrderResponse>

    @POST("api/OrderItem/SaveOrderItem")
    suspend fun placeTableOrders(@Body body: PlaceOrderRequest): Response<SuccessResponse>

    @POST("api/OrderItem/SaveOrderItem")
    suspend fun saveTableOrders(@Body body: SaveOrderRequest): Response<SuccessResponse>

    @GET("api/SetupSubItem/SearchSubItemList/{searchTerm}")
    suspend fun searchSubItems(@Path("searchTerm") searchTerm: String): Response<GetFoodSubItemResponse>

    @POST("api/OrderItem/ChangeTableNumber/{from}/{to}/ChangeTable")
    suspend fun changeTableNumber(@Path("from") from: Int, @Path("to") to: Int): Response<SuccessResponse>

    @POST("api/OrderItem/ChangeTableNumber/{to}/{from}/MargeTable")
    suspend fun mergeTable(@Path("from") from: Int, @Path("to") to: Int): Response<SuccessResponse>
}