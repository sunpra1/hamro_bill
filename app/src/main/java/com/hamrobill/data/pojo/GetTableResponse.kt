package com.hamrobill.data.pojo

import com.google.gson.annotations.SerializedName

data class GetTableResponse(
        @SerializedName("IsError") val isError: Boolean,
        @SerializedName("Message") val message: ArrayList<String>,
        @SerializedName("Data") val data: ArrayList<Table>
)

data class Table(
        @SerializedName("TableId") val tableID: Int,
        @SerializedName("IsBooked") var isBooked: Boolean,
        @SerializedName("TableName") val tableName: String,
        @SerializedName("DisplayOrder") val displayOrder: Int,
        @SerializedName("MaxSites") val maxSites: Int,
        @SerializedName("IsActive") val isActive: Boolean,
        @SerializedName("CountCustomer") val customerCount: Int
)
