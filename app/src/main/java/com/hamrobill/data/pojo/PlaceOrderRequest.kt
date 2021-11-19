package com.hamrobill.data.pojo

import com.google.gson.annotations.SerializedName

data class PlaceOrderRequest(
    @SerializedName("TableId") val tableId: Int,
    @SerializedName("OrderItemList") val orderItemList: ArrayList<PlaceOrderItem>,
    @SerializedName("Quantity") val quantity: Float = 0f,
    @SerializedName("SubItemPrice") val subItemPrice: Float = 0f,
    @SerializedName("TotalPrice") val totalPrice: Float = 0f,
    @SerializedName("IsChecked") val isPacking: Boolean = false,
    @SerializedName("Remarks") val remarks: String = "",
    @SerializedName("CountCustomer") val countCustomer: Int = 0,
    @SerializedName("OrderBy") val orderBy: String = "",
    @SerializedName("RoomCategoryId") val roomCategoryId: Int = 0,
    @SerializedName("OrderId") val orderId: Int = 0
)

data class PlaceOrderItem(
    @SerializedName("TableId") val tableId: Int,
    @SerializedName("SubItemId") val subItemId: Int,
    @SerializedName("SubItemPrice") val subItemPrice: Float,
    @SerializedName("Quantity") val quantity: Float,
    @SerializedName("TotalPrice") val totalPrice: Float,
    @SerializedName("ItemId") val itemId: Int,
    @SerializedName("Discount") val discount: Float,
    @SerializedName("OrderItemPOSId") val orderItemPOSId: Int? = null,
    @SerializedName("IsOrderByPOS") val isOrderByPOS: Boolean = false,
    @SerializedName("CustomerId") val customerId: Int? = null,
    @SerializedName("CountCustomer") val countCustomer: Int = 0,
    @SerializedName("IsHappyHour") val isHappyHour: Boolean = false
)