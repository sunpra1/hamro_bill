package com.hamrobill.data.repository

import android.util.Log
import com.hamrobill.R
import com.hamrobill.data.api.HamrobillAPIConsumer
import com.hamrobill.data.api.PrintApiConsumer
import com.hamrobill.data.pojo.PlaceOrderRequest
import com.hamrobill.data.pojo.SaveOrderRequest
import com.hamrobill.utils.RequestStatus
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillingRepository @Inject constructor(private val hamroBillAPIConsumer: HamrobillAPIConsumer, private val printAPIConsumer: PrintApiConsumer) {
    companion object {
        private const val TAG = "BillingRepository"
    }

    fun getTables() = flow {
        emit(RequestStatus.Waiting)
        val response = hamroBillAPIConsumer.getTableList()
        if (response.isSuccessful) {
            emit(RequestStatus.Success(response.body()))
        } else {
            Log.d(TAG, "getTables: ${response.errorBody()?.byteStream()?.reader()?.readText()}")
            emit(RequestStatus.Error(R.string.unable_fetch_tables))
        }
    }

    fun getTableActiveOrders(tableId: Int) = flow {
        emit(RequestStatus.Waiting)
        val response = hamroBillAPIConsumer.getTableActiveOrders(tableId)
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
        val response = hamroBillAPIConsumer.getFoodItems()
        if (response.isSuccessful) {
            emit(RequestStatus.Success(response.body()))
        } else {
            Log.d(TAG, "getFoodItems: ${response.errorBody()?.byteStream()?.reader()?.readText()}")
            emit(RequestStatus.Error(R.string.unable_fetch_food_items))
        }
    }

    fun getFoodSubItems(foodItemId: Int) = flow {
        emit(RequestStatus.Waiting)
        val response = hamroBillAPIConsumer.getFoodSubItems(foodItemId)
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
        val response = hamroBillAPIConsumer.placeTableOrders(placeOrderRequest)
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
        val printResponse = printAPIConsumer.saveTableOrders(saveOrderRequest)
        if(printResponse.isSuccessful){
            val response = hamroBillAPIConsumer.saveTableOrders(saveOrderRequest)
            if (response.isSuccessful) {
                emit(RequestStatus.Success(response.body()))
            } else {
                Log.d(
                    TAG,
                    "saveTableOrders: ${response.errorBody()?.byteStream()?.reader()?.readText()}"
                )
                emit(RequestStatus.Error(R.string.unable_save_table_order))
            }
        }else{
            Log.d(
                TAG,
                "saveTableOrders: ${printResponse.errorBody()?.byteStream()?.reader()?.readText()}"
            )
            emit(RequestStatus.Error(R.string.print_server_error))
        }
    }

    fun searchSubItems(searchTerm: String) = flow {
        emit(RequestStatus.Waiting)
        val response = hamroBillAPIConsumer.searchSubItems(searchTerm)
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
        val response = hamroBillAPIConsumer.changeTableNumber(from, to)
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
        val response = hamroBillAPIConsumer.mergeTable(from, to)
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