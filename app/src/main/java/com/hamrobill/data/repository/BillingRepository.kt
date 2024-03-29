package com.hamrobill.data.repository

import android.util.Log
import com.hamrobill.R
import com.hamrobill.data.api.HamrobillAPIConsumer
import com.hamrobill.data.api.PrintApiConsumer
import com.hamrobill.data.pojo.*
import com.hamrobill.utility.RequestStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillingRepository @Inject constructor(
    private val hamroBillAPIConsumer: HamrobillAPIConsumer,
    private val printAPIConsumer: PrintApiConsumer
) {
    companion object {
        private const val TAG = "BillingRepository"
    }

    fun getTablesAndFoodItems() = flow {
        emit(RequestStatus.Waiting)
        val getTablesResponse = withContext(Dispatchers.IO) { hamroBillAPIConsumer.getTableList() }
        val getFoodItemsResponse =
            withContext(Dispatchers.IO) { hamroBillAPIConsumer.getFoodItems() }
        if (getTablesResponse.isSuccessful && getFoodItemsResponse.isSuccessful) {
            emit(
                RequestStatus.Success(
                    OnLoadData(
                        tables = getTablesResponse.body(),
                        foodItems = getFoodItemsResponse.body()
                    )
                )
            )
        } else {
            Log.d(
                TAG,
                "getTablesResponse: ${
                    getTablesResponse.errorBody()?.byteStream()?.reader()?.readText()
                }"
            )
            Log.d(
                TAG,
                "getFoodItemsResponse: ${
                    getFoodItemsResponse.errorBody()?.byteStream()?.reader()?.readText()
                }"
            )
            emit(RequestStatus.Error(R.string.unable_fetch_tables_and_food_items))
        }
    }

    fun getTableActiveOrdersAndCancelableOrderItems(tableId: Int) = flow {
        emit(RequestStatus.Waiting)
        val tableActiveOrderResponse =
            withContext(Dispatchers.IO) { hamroBillAPIConsumer.getTableActiveOrders(tableId) }
        val cancellableOrderItemResponse =
            withContext(Dispatchers.IO) { hamroBillAPIConsumer.getCancelableOrderItems(tableId) }
        if (tableActiveOrderResponse.isSuccessful && cancellableOrderItemResponse.isSuccessful) {
            emit(
                RequestStatus.Success(
                    SelectedTableData(
                        activeTableOrders = tableActiveOrderResponse.body(),
                        cancellableOrderItems = cancellableOrderItemResponse.body()
                    )
                )
            )
        } else {
            Log.d(
                TAG,
                "tableActiveOrderResponse: ${
                    tableActiveOrderResponse.errorBody()?.byteStream()?.reader()?.readText()
                }"
            )
            Log.d(
                TAG,
                "cancellableOrderItemResponse: ${
                    cancellableOrderItemResponse.errorBody()?.byteStream()?.reader()?.readText()
                }"
            )
            emit(RequestStatus.Error(R.string.unable_fetch_table_details))
        }
    }

    fun getFoodSubItems(foodItemId: Int) = flow {
        emit(RequestStatus.Waiting)
        val response =
            withContext(Dispatchers.IO) { hamroBillAPIConsumer.getFoodSubItems(foodItemId) }
        if (response.isSuccessful) {
            emit(RequestStatus.Success(response.body()))
        } else {
            Log.d(
                TAG,
                "getFoodSubItems: ${response.errorBody()?.byteStream()?.reader()?.readText()}"
            )
            emit(RequestStatus.Error(R.string.unable_fetch_food_sub_items))
        }
    }

    fun placeTableOrders(placeOrderRequest: PlaceOrderRequest) = flow {
        emit(RequestStatus.Waiting)
        val response =
            withContext(Dispatchers.IO) { hamroBillAPIConsumer.placeTableOrders(placeOrderRequest) }
        if (response.isSuccessful) {
            emit(RequestStatus.Success(response.body()))
        } else {
            Log.d(
                TAG,
                "placeTableOrders: ${
                    response.errorBody()?.byteStream()?.reader()?.readText()
                }"
            )
            emit(RequestStatus.Error(R.string.unable_place_table_order))
        }
    }

    fun saveTableOrders(saveOrderRequest: SaveOrderRequest) = flow {
        emit(RequestStatus.Waiting)
        val printResponse =
            withContext(Dispatchers.IO) { printAPIConsumer.saveTableOrders(saveOrderRequest) }
        if (printResponse.isSuccessful) {
            val response =
                withContext(Dispatchers.IO) { hamroBillAPIConsumer.saveTableOrders(saveOrderRequest) }
            if (response.isSuccessful) {
                emit(RequestStatus.Success(response.body()))
            } else {
                Log.d(
                    TAG,
                    "saveTableOrders: ${response.errorBody()?.byteStream()?.reader()?.readText()}"
                )
                emit(RequestStatus.Error(R.string.unable_save_table_order))
            }
        } else {
            Log.d(
                TAG,
                "saveTableOrders: ${printResponse.errorBody()?.byteStream()?.reader()?.readText()}"
            )
            emit(
                RequestStatus.Error(
                    printResponse.errorBody()?.byteStream()?.reader()?.readText()
                        ?: R.string.print_server_error
                )
            )
        }
    }

    fun cancelTableOrder(
        saveOrderRequest: SaveOrderRequest,
        cancelOrderBody: CancelOrderBody,
        isOrderPlaced: Boolean
    ) =
        flow {
            emit(RequestStatus.Waiting)
            if (isOrderPlaced) {
                val printResponse =
                    withContext(Dispatchers.IO) { printAPIConsumer.saveTableOrders(saveOrderRequest) }
                if (printResponse.isSuccessful) {
                    val response = withContext(Dispatchers.IO) {
                        hamroBillAPIConsumer.cancelOrder(cancelOrderBody)
                    }
                    if (response.isSuccessful) {
                        emit(RequestStatus.Success(response.body()))
                    } else {
                        Log.d(
                            TAG,
                            "cancelTableOrder: ${
                                response.errorBody()?.byteStream()?.reader()?.readText()
                            }"
                        )
                        emit(RequestStatus.Error(R.string.unable_cancel_table_order))
                    }
                } else {
                    Log.d(
                        TAG,
                        "cancelTableOrder: ${
                            printResponse.errorBody()?.byteStream()?.reader()?.readText()
                        }"
                    )
                    emit(
                        RequestStatus.Error(
                            printResponse.errorBody()?.byteStream()?.reader()?.readText()
                                ?: R.string.print_server_error
                        )
                    )
                }
            } else {
                val response = hamroBillAPIConsumer.cancelOrder(cancelOrderBody)
                if (response.isSuccessful) {
                    emit(RequestStatus.Success(response.body()))
                } else {
                    Log.d(
                        TAG,
                        "cancelTableOrder: ${
                            response.errorBody()?.byteStream()?.reader()?.readText()
                        }"
                    )
                    emit(RequestStatus.Error(R.string.unable_cancel_table_order))
                }
            }
        }

    fun searchSubItems(searchTerm: String) = flow {
        emit(RequestStatus.Waiting)
        val response =
            withContext(Dispatchers.IO) { hamroBillAPIConsumer.searchSubItems(searchTerm) }
        if (response.isSuccessful) {
            emit(RequestStatus.Success(response.body()))
        } else {
            Log.d(
                TAG,
                "searchSubItems: ${response.errorBody()?.byteStream()?.reader()?.readText()}"
            )
            emit(RequestStatus.Error(R.string.unable_search_sub_items))
        }
    }

    fun changeTableNumber(from: Int, to: Int) = flow {
        emit(RequestStatus.Waiting)
        val response =
            withContext(Dispatchers.IO) { hamroBillAPIConsumer.changeTableNumber(from, to) }
        if (response.isSuccessful) {
            emit(RequestStatus.Success(response.body()))
        } else {
            Log.d(
                TAG,
                "changeTableNumber: ${response.errorBody()?.byteStream()?.reader()?.readText()}"
            )
            emit(RequestStatus.Error(R.string.unable_change_table_number))
        }
    }

    fun mergeTable(from: Int, to: Int) = flow {
        emit(RequestStatus.Waiting)
        val response = withContext(Dispatchers.IO) { hamroBillAPIConsumer.mergeTable(from, to) }
        if (response.isSuccessful) {
            emit(RequestStatus.Success(response.body()))
        } else {
            Log.d(
                TAG,
                "mergeTable: ${response.errorBody()?.byteStream()?.reader()?.readText()}"
            )
            emit(RequestStatus.Error(R.string.unable_merge_tables))
        }
    }

    fun changePassword(requestBody: ChangePasswordRequestBody) = flow {
        emit(RequestStatus.Waiting)
        val response =
            withContext(Dispatchers.IO) { hamroBillAPIConsumer.changePassword(requestBody) }
        if (response.isSuccessful) {
            emit(RequestStatus.Success(response.body()))
        } else {
            Log.d(
                TAG,
                "changePassword: ${response.errorBody()?.byteStream()?.reader()?.readText()}"
            )
            emit(RequestStatus.Error(R.string.unable_change_password))
        }
    }
}