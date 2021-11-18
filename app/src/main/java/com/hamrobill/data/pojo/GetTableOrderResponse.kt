package com.hamrobill.data.pojo

import com.google.gson.annotations.SerializedName

data class GetTableOrderResponse(
        @SerializedName("Data") val data: ArrayList<ActiveOrderItem>,
        @SerializedName("IsError") val isError: Boolean,
        @SerializedName("Message") val message: List<String>
)

data class ActiveOrderItem(
        @SerializedName("AdvanceAmount") val advanceAmount: Int?,
        @SerializedName("BillNumber") val billNumber: Int,
        @SerializedName("CountCustomer") val countCustomer: Int?,
        @SerializedName("CustomerId") val customerId: Int?,
        @SerializedName("DeletedBy") val deletedBy: Int,
        @SerializedName("DeletedDate") val deletedDate: String?,
        @SerializedName("Discount") val discount: Float,
        @SerializedName("EndTime") val endTime: String?,
        @SerializedName("ExtraColumn") val isExtraColumn: Boolean,
        @SerializedName("HappyHourDiscount") val happyHourDiscount: Float,
        @SerializedName("HotelCustomerId") val hotelCustomerId: Int?,
        @SerializedName("IsActive") val isActive: Boolean,
        @SerializedName("IsBar") val isBar: Boolean,
        @SerializedName("isCheckOutTable") val isCheckOutTable: Boolean?,
        @SerializedName("IsCoffee") val isCoffee: Boolean,
        @SerializedName("IsDelelteSeen") val isDeleteSeen: Any?,
        @SerializedName("IsHappyHour") val isHappyHour: Boolean,
        @SerializedName("IsOrder") var isOrder: Boolean,
        @SerializedName("IsOrderByPOS") val isOrderByPOS: Boolean?,
        @SerializedName("IsOrderPrint") val isOrderPrint: Boolean?,
        @SerializedName("IsOrderSeen") val isOrderSeen: Boolean?,
        @SerializedName("IsPrintByCode") val isPrintByCode: Boolean,
        @SerializedName("ItemId") val itemId: Int,
        @SerializedName("MargeTableId") val margeTableId: Int?,
        @SerializedName("OpenFood") val openFood: Any?,
        @SerializedName("OrderBy") val orderBy: Int?,
        @SerializedName("OrderDate") val orderDate: String?,
        @SerializedName("OrderEntryBy") val orderEntryBy: Int,
        @SerializedName("OrderId") val orderId: Int,
        @SerializedName("OrderItemList") val orderItemList: Any?,
        @SerializedName("OrderItemPOSId") val orderItemPOSId: Int?,
        @SerializedName("PrintDate") val printDate: String?,
        @SerializedName("PrintTitle") val printTitle: String?,
        @SerializedName("Quantity") val quantity: Float,
        @SerializedName("Remarks") val remarks: String?,
        @SerializedName("RoomCategoryId") val roomCategoryId: Int?,
        @SerializedName("StartTime") val startTime: String?,
        @SerializedName("SubItemId") val subItemId: String?,
        @SerializedName("SubItemName") val subItemName: String,
        @SerializedName("SubItemPrice") val subItemPrice: Float,
        @SerializedName("TableId") val tableId: Int,
        @SerializedName("TableName") val tableName: String?,
        @SerializedName("TotalPrice") val totalPrice: Float,
        @SerializedName("UniqGuid") val uniqueGuid: Int?,
        @SerializedName("UserName") val userName: String?
)