package com.hamrobill.data.pojo

data class SelectedTableData(
    val activeTableOrders: GetTableOrderResponse?,
    val cancellableOrderItems: CancellableOrderResponse?
)
