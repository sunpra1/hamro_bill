package com.hamrobill.data.pojo

import com.google.gson.annotations.SerializedName

data class GetFoodItemResponse(
        @SerializedName("IsError") val isError: Boolean,
        @SerializedName("Message") val message: ArrayList<String>,
        @SerializedName("Data") val data: ArrayList<FoodItem>
)

data class FoodItem(
        @SerializedName("ItemId") val itemId: Int,
        @SerializedName("ItemName") val itemName: String,
        @SerializedName("IsBilling") val isBilling: Boolean,
        @SerializedName("IsInventory") val isInventory: Boolean,
        @SerializedName("IsBar") val isBar: Boolean,
        @SerializedName("DisplayOrder") val displayOrder: Int,
        @SerializedName("IsActive") val isActive: Boolean,
        @SerializedName("IsCoffee") val isCoffee: Boolean,
        @SerializedName("ExtraColumn") val extraColumn: Boolean,
        @SerializedName("WholeSale") val isWholeSale: Boolean,
        @SerializedName("CreatedDate") val createdDate: String,
        @SerializedName("UpdatedDate") val updatedDate: String,
        @SerializedName("CreatedBy") val createdBy: Int,
        @SerializedName("DeletedBy") val deletedBy: Int?,
        @SerializedName("UpdatedBy") val updatedBy: Int?,
        @SerializedName("DeletedDate") val deletedDate: String?
)
