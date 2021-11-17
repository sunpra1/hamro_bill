package com.hamrobill.data.pojo

import com.google.gson.annotations.SerializedName
import com.hamrobill.utils.getISOFormattedStringDate
import java.util.*

data class SaveOrderRequest(
    @SerializedName("TableId") val tableId: Int,
    @SerializedName("Quantity") val quantity: Float = 0f,
    @SerializedName("SubItemPrice") val subItemPrice: Float = 0f,
    @SerializedName("TotalPrice") val totalPrice: Float = 0f,
    @SerializedName("IsChecked") val isPacking: Boolean = false,
    @SerializedName("Remarks") val remarks: String = "",
    @SerializedName("OrderBy") val orderBy: String = "",
    @SerializedName("CountCustomer") val countCustomer: Int = 0,
    @SerializedName("OrderItemList") val orderItemList: ArrayList<SaveOrderItem>,
    @SerializedName("RoomCategoryId") val roomCategoryId: Int = 0,
    @SerializedName("OrderId") val orderId: Int = 1,
    @SerializedName("PrintTitle") val printTitle: String,
    @SerializedName("PrintDate") val printDate: String = Calendar.getInstance()
        .getISOFormattedStringDate(),
    @SerializedName("UniqGuid") val uniqueGuid: String = UUID.randomUUID().toString(),
    @SerializedName("TableName") val tableName: String,
    @SerializedName("BillNumber") val billNumber: Int,
    @SerializedName("isCheckOutTable") val isCheckOutTable: Boolean = false,
    @SerializedName("IsPrintByCode") val isPrintByCode: Boolean = true
) {
    data class SaveOrderItem(
        @SerializedName("TableId") val tableId: Int,
        @SerializedName("SubItemId") val subItemId: String?,
        @SerializedName("ItemId") val itemId: Int,
        @SerializedName("SubItemName") val subItemName: String,
        @SerializedName("Quantity") val quantity: Float,
        @SerializedName("Remarks") val remarks: String?
    )
}