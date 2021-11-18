package com.hamrobill.data.pojo

import com.google.gson.annotations.SerializedName

data class GetFoodSubItemResponse(
        @SerializedName("IsError") val isError: Boolean,
        @SerializedName("Message") val message: ArrayList<String>,
        @SerializedName("Data") val data: ArrayList<FoodSubItem>
)

data class FoodSubItem(
        @SerializedName("SubItemId") val subItemId: Int,
        @SerializedName("ItemId") val itemId: Int,
        @SerializedName("ItemName") val itemName: String?,
        @SerializedName("HappyHourCount") val happyHourCount: Int,
        @SerializedName("Discount") val discount: Float,
        @SerializedName("SubItemName") val subItemName: String,
        @SerializedName("UniteName") val uniteName: String?,
        @SerializedName("UniteId") val uniteId: Int,
        @SerializedName("SubItemPrice") val subItemPrice: Float,
        @SerializedName("DisplayOrder") val displayOrder: Int,
        @SerializedName("MinimumUnit") val minimumUnit: Int?,
        @SerializedName("IsActive") val isActive: Boolean,
        @SerializedName("IsHappyHour") val isHappyHour: Boolean,
        @SerializedName("AvailableQuantity") val availableQuantity: Float,
        @SerializedName("CreatedDate") val createdDate: String?,
        @SerializedName("UpdatedDate") val updatedDate: String?,
        @SerializedName("CreatedBy") val createdBy: Int,
        @SerializedName("UpdatedBy") val updatedBy: Int,
        @SerializedName("DeletedBy") val deletedBy: Int,
        @SerializedName("DeletedDate") val deletedDate: String?,
        @SerializedName("SetupSubItemGetList") val setupSubItemGetList: Any?
)

