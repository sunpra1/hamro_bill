package com.hamrobill.data.pojo

import com.google.gson.annotations.SerializedName

data class CancelOrderBody(
    @SerializedName("TableId")
    val tableId: Int,
    @SerializedName("OrderId")
    val orderId: Int,
    @SerializedName("DeleteteOrderItemList")
    val deleteOrderItems: ArrayList<DeleteOrderItem>
)

data class DeleteOrderItem(
    @SerializedName("TableId")
    val tableId: Int,
    @SerializedName("OrderItemId")
    val orderItemId: Int,
    @SerializedName("Remarks")
    val remarks: String?
)