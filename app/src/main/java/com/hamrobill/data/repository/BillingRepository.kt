package com.hamrobill.data.repository

import android.util.Log
import com.hamrobill.R
import com.hamrobill.data.api.HamrobillAPIConsumer
import com.hamrobill.data.pojo.PlaceOrderRequest
import com.hamrobill.data.pojo.SaveOrderRequest
import com.hamrobill.utils.RequestStatus
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillingRepository @Inject constructor(private val apiConsumer: HamrobillAPIConsumer) {
    companion object {
        private const val TAG = "BillingRepository"
    }

    fun getTables() = flow {
        emit(RequestStatus.Waiting)
        val response = apiConsumer.getTableList()
        if (response.isSuccessful) {
            emit(RequestStatus.Success(response.body()))
        } else {
            Log.d(TAG, "getTables: ${response.errorBody()?.byteStream()?.reader()?.readText()}")
            emit(RequestStatus.Error(R.string.unable_fetch_tables))
        }
    }

    fun getTableActiveOrders(tableId: Int) = flow {
        emit(RequestStatus.Waiting)
        val response = apiConsumer.getTableActiveOrders(tableId)
        if (response.isSuccessful) {
            emit(RequestStatus.Success(response.body()))
        } else {
            Log.d(
                    TAG,
                    "getTableActiveOrders: ${response.errorBody()?.byteStream()?.reader()?.readText()}"
            )
            emit(RequestStatus.Error(R.string.unable_fetch_table_orders))
        }
    }

    fun getFoodItems() = flow {
        emit(RequestStatus.Waiting)
        val response = apiConsumer.getFoodItems()
        if (response.isSuccessful) {
            emit(RequestStatus.Success(response.body()))
        } else {
            Log.d(TAG, "getFoodItems: ${response.errorBody()?.byteStream()?.reader()?.readText()}")
            emit(RequestStatus.Error(R.string.unable_fetch_food_items))
        }
    }

    fun getFoodSubItems(foodItemId: Int) = flow {
        emit(RequestStatus.Waiting)
        val response = apiConsumer.getFoodSubItems(foodItemId)
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
        val response = apiConsumer.placeTableOrders(placeOrderRequest)
        if (response.isSuccessful) {
            emit(RequestStatus.Success(response.body()))
        } else {
            Log.d(
                    TAG,
                    "placeTableOrders: ${response.errorBody()?.byteStream()?.reader()?.readText()}"
            )
            emit(RequestStatus.Error(R.string.unable_place_table_order))
        }
    }

    fun saveTableOrders(saveOrderRequest: SaveOrderRequest) = flow {
        emit(RequestStatus.Waiting)
        val response = apiConsumer.saveTableOrders(saveOrderRequest)
        if (response.isSuccessful) {
            emit(RequestStatus.Success(response.body()))
        } else {
            Log.d(
                    TAG,
                    "saveTableOrders: ${response.errorBody()?.byteStream()?.reader()?.readText()}"
            )
            emit(RequestStatus.Error(R.string.unable_save_table_order))
        }
    }

    fun searchSubItems(searchTerm: String) = flow {
        emit(RequestStatus.Waiting)
        val response = apiConsumer.searchSubItems(searchTerm)
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
        val response = apiConsumer.changeTableNumber(from, to)
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
        val response = apiConsumer.mergeTable(from, to)
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
}