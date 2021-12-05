package com.hamrobill.data.pojo


import com.google.gson.annotations.SerializedName

data class CancellableOrderResponse(
    @SerializedName("Data")
    val data: ArrayList<CancellableOrderItem>,
    @SerializedName("IsError")
    val isError: Boolean,
    @SerializedName("Message")
    val message: ArrayList<Any>
)

data class CancellableOrderItem(
    @SerializedName("AdvanceAmount")
    val advanceAmount: Any?,
    @SerializedName("BillNumber")
    val billNumber: Int,
    @SerializedName("CountCustomer")
    val countCustomer: Int?,
    @SerializedName("CustomerId")
    val customerId: String?,
    @SerializedName("DeletedBy")
    val deletedBy: Int,
    @SerializedName("DeletedDate")
    val deletedDate: String?,
    @SerializedName("Discount")
    val discount: Double,
    @SerializedName("EndTime")
    val endTime: String?,
    @SerializedName("ExtraColumn")
    val extraColumn: Boolean,
    @SerializedName("HappyHourDiscount")
    val happyHourDiscount: Double,
    @SerializedName("HotelCustomerId")
    val hotelCustomerId: String?,
    @SerializedName("IsActive")
    val isActive: Boolean,
    @SerializedName("IsBar")
    val isBar: Boolean,
    @SerializedName("isCheckOutTable")
    val isCheckOutTable: Boolean?,
    @SerializedName("IsCoffee")
    val isCoffee: Boolean,
    @SerializedName("IsDelelteSeen")
    val isDelelteSeen: Boolean?,
    @SerializedName("IsHappyHour")
    val isHappyHour: Boolean,
    @SerializedName("IsOrder")
    val isOrder: Boolean,
    @SerializedName("IsOrderByPOS")
    val isOrderByPOS: Boolean?,
    @SerializedName("IsOrderPrint")
    val isOrderPrint: Boolean?,
    @SerializedName("IsOrderSeen")
    val isOrderSeen: Boolean?,
    @SerializedName("IsPrintByCode")
    val isPrintByCode: Boolean,
    @SerializedName("ItemId")
    val itemId: Int?,
    @SerializedName("MargeTableId")
    val margeTableId: String?,
    @SerializedName("OrderBy")
    val orderBy: String?,
    @SerializedName("OrderDate")
    val orderDate: String,
    @SerializedName("OrderEntryBy")
    val orderEntryBy: Int,
    @SerializedName("OrderId")
    val orderId: Int,
    @SerializedName("OrderItemPOSId")
    val orderItemPOSId: String?,
    @SerializedName("PrintDate")
    val printDate: String?,
    @SerializedName("PrintTitle")
    val printTitle: String?,
    @SerializedName("Quantity")
    var quantity: Float,
    @SerializedName("Remarks")
    val remarks: String?,
    @SerializedName("RoomCategoryId")
    val roomCategoryId: String?,
    @SerializedName("StartTime")
    val startTime: String?,
    @SerializedName("SubItemId")
    val subItemId: Int,
    @SerializedName("SubItemName")
    val subItemName: String,
    @SerializedName("SubItemPrice")
    val subItemPrice: Double,
    @SerializedName("TableId")
    val tableId: Int,
    @SerializedName("TableName")
    val tableName: String?,
    @SerializedName("TotalPrice")
    val totalPrice: Double,
    @SerializedName("UniqGuid")
    val uniqGuid: String?,
    @SerializedName("UserName")
    val userName: String?
)