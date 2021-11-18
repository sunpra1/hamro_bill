package com.hamrobill.model

import com.hamrobill.data.pojo.FoodItem
import com.hamrobill.data.pojo.FoodSubItem
import com.hamrobill.data.pojo.Table

data class OrderItem(
        val table: Table,
        val foodItem: FoodItem,
        val foodSubItem: FoodSubItem,
        val quantity: Float = 1f,
        val priority: Int? = null,
        val remarks: String? = null
)