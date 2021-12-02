package com.hamrobill.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hamrobill.R
import com.hamrobill.data.pojo.*
import com.hamrobill.data.repository.BillingRepository
import com.hamrobill.di.scope.ActivityScope
import com.hamrobill.model.FoodCategory
import com.hamrobill.model.OrderItem
import com.hamrobill.model.TableItemChanged
import com.hamrobill.utils.NetworkConnectivity
import com.hamrobill.utils.RequestStatus
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@ActivityScope
class SharedViewModel @Inject constructor(
    networkConnectivity: NetworkConnectivity,
    private val billingRepository: BillingRepository
) : ViewModel() {
    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage: MutableLiveData<Any> = MutableLiveData()
    val errorMessage: LiveData<Any> = _errorMessage

    private val _isNetworkAvailable: MutableLiveData<Boolean> = MutableLiveData()
    val isNetworkAvailable: LiveData<Boolean> = _isNetworkAvailable

    private val _tables: MutableLiveData<ArrayList<Table>> = MutableLiveData()
    val tables: LiveData<ArrayList<Table>> = _tables

    private val _foodItems: MutableLiveData<ArrayList<FoodItem>> = MutableLiveData()
    val foodItems: LiveData<ArrayList<FoodItem>> = _foodItems

    private val _selectedTable: MutableLiveData<Table> = MutableLiveData()
    val selectedTable: LiveData<Table> = _selectedTable

    private val _tableOrders: MutableLiveData<ArrayList<OrderItem>> = MutableLiveData()
    val tableOrders: LiveData<ArrayList<OrderItem>> = _tableOrders

    private val _activeTableOrders: MutableLiveData<ArrayList<ActiveOrderItem>> = MutableLiveData()
    val activeTableOrders: LiveData<ArrayList<ActiveOrderItem>> = _activeTableOrders

    private val _selectedFoodItem: MutableLiveData<FoodItem> = MutableLiveData()
    val selectedFoodItem: LiveData<FoodItem> = _selectedFoodItem

    private val _foodSubItems: MutableLiveData<ArrayList<FoodSubItem>> = MutableLiveData()
    val foodSubItems: LiveData<ArrayList<FoodSubItem>> = _foodSubItems

    private val _tableItemChanged: MutableLiveData<TableItemChanged> = MutableLiveData()
    val tableItemChanged: LiveData<TableItemChanged> = _tableItemChanged

    private val _isOrderPlaced: MutableLiveData<Boolean> = MutableLiveData()
    val isOrderPlaced: LiveData<Boolean> = _isOrderPlaced

    private val _searchResult: MutableLiveData<ArrayList<FoodSubItem>> = MutableLiveData()
    val searchResult: LiveData<ArrayList<FoodSubItem>> = _searchResult

    private val _cancelOrderItem: MutableLiveData<ActiveOrderItem> = MutableLiveData()
    val cancelOrderItem: LiveData<ActiveOrderItem> = _cancelOrderItem

    private val _isCancelComplete : MutableLiveData<Boolean> = MutableLiveData()
    val isCancelComplete: LiveData<Boolean> = _isCancelComplete

    init {
        networkConnectivity.observeForever { _isNetworkAvailable.value = it }
    }

    fun setSelectedFoodItem(foodItem: FoodItem) {
        _selectedFoodItem.value = foodItem
    }

    fun setSelectedTable(table: Table?) {
        _selectedTable.value = table
    }

    fun setActiveTableOrders(tableOrders: ArrayList<ActiveOrderItem>?) {
        _activeTableOrders.value = tableOrders
    }

    fun setIsOrderPlaced(isPlaced: Boolean) {
        _isOrderPlaced.value = isPlaced
    }

    fun setCancelOrderItem(activeOrderItem: ActiveOrderItem) {
        if (cancelOrderItem.value == activeOrderItem) {
            _cancelOrderItem.value = null
        } else {
            _cancelOrderItem.value = activeOrderItem
        }
    }

    fun hasActiveTableOrders(): Boolean {
        return tables.value != null && tables.value!!.indexOfFirst { it.isBooked } > -1
    }

    fun addNewOrder(foodSubItem: FoodSubItem) {
        val foodItem =
            selectedFoodItem.value
                ?: foodItems.value!!.first { it.itemId == foodSubItem.itemId }
        val index =
            tableOrders.value?.indexOfFirst { it.table.tableID == selectedTable.value!!.tableID && it.foodItem.itemId == foodItem.itemId && it.foodSubItem.subItemId == foodSubItem.subItemId }
                ?: -1
        if (index > -1) {
            _tableOrders.value = tableOrders.value?.apply {
                removeAt(index)
            }
        } else {
            val orderItem = OrderItem(selectedTable.value!!, foodItem, foodSubItem)
            if (tableOrders.value.isNullOrEmpty()) {
                _tableOrders.value = arrayListOf(orderItem)
            } else {
                _tableOrders.value = tableOrders.value!!.apply {
                    add(orderItem)
                }
            }
        }
    }

    fun editOrder(
        foodSubItem: FoodSubItem,
        quantity: Float = 1f,
        priority: Int? = null,
        remarks: String? = null
    ) {
        val foodItem =
            selectedFoodItem.value
                ?: foodItems.value!!.first { it.itemId == foodSubItem.itemId }
        val index =
            tableOrders.value?.indexOfFirst { it.table.tableID == selectedTable.value!!.tableID && it.foodItem.itemId == foodItem.itemId && it.foodSubItem.subItemId == foodSubItem.subItemId }
                ?: -1
        val orderItem = OrderItem(
            selectedTable.value!!,
            foodItem,
            foodSubItem,
            quantity,
            priority,
            remarks
        )
        if (index > -1 && quantity < 1)
            _tableOrders.value = tableOrders.value?.apply {
                removeAt(index)
            }
        else if (index > -1) {
            _tableOrders.value = tableOrders.value?.apply {
                set(index, orderItem)
            }
        } else {
            _tableOrders.value = tableOrders.value?.apply {
                add(orderItem)
            } ?: arrayListOf(orderItem)
        }
    }

    fun getTables() {
        viewModelScope.launch {
            billingRepository.getTables()
                .catch {
                    _isLoading.value = false
                    _errorMessage.value = R.string.unable_fetch_tables
                }
                .collect {
                    when (it) {
                        is RequestStatus.Waiting -> {
                            _isLoading.value = true
                        }
                        is RequestStatus.Success -> {
                            _isLoading.value = false
                            _tables.value =
                                it.data?.data?.distinctBy { t -> t.tableID } as ArrayList<Table>
                        }
                        is RequestStatus.Error -> {
                            _isLoading.value = false
                            _errorMessage.value = it.message
                        }
                    }
                }
        }
    }

    fun getTableActiveOrders() {
        viewModelScope.launch {
            billingRepository.getTableActiveOrders(selectedTable.value!!.tableID)
                .catch {
                    _isLoading.value = false
                    _errorMessage.value = R.string.unable_fetch_tables
                }
                .collect {
                    when (it) {
                        is RequestStatus.Waiting -> {
                            _isLoading.value = true
                        }
                        is RequestStatus.Success -> {
                            _isLoading.value = false
                            _activeTableOrders.value = it.data?.data
                        }
                        is RequestStatus.Error -> {
                            _isLoading.value = false
                            _errorMessage.value = it.message
                        }
                    }
                }
        }
    }

    fun getFoodItems() {
        viewModelScope.launch {
            billingRepository.getFoodItems()
                .catch {
                    _isLoading.value = false
                    _errorMessage.value = R.string.unable_fetch_food_items
                }
                .collect {
                    when (it) {
                        is RequestStatus.Waiting -> {
                            _isLoading.value = true
                        }
                        is RequestStatus.Success -> {
                            _isLoading.value = false
                            _foodItems.value = it.data?.data
                        }
                        is RequestStatus.Error -> {
                            _isLoading.value = false
                            _errorMessage.value = it.message
                        }
                    }
                }
        }
    }

    fun getFoodSubItems() {
        selectedFoodItem.value?.let { foodItem ->
            viewModelScope.launch {
                billingRepository.getFoodSubItems(foodItem.itemId)
                    .catch {
                        _isLoading.value = false
                        _errorMessage.value = R.string.unable_fetch_food_sub_items
                    }
                    .collect {
                        when (it) {
                            is RequestStatus.Waiting -> {
                                _isLoading.value = true
                            }
                            is RequestStatus.Success -> {
                                _isLoading.value = false
                                _foodSubItems.value = it.data?.data
                            }
                            is RequestStatus.Error -> {
                                _isLoading.value = false
                                _errorMessage.value = it.message
                            }
                        }
                    }
            }
        }
    }

    fun placeTableOrders() {
        val table = selectedTable.value!!
        val index = tables.value!!.indexOf(table)
        val orderItems = tableOrders.value!!
            .map {
                PlaceOrderItem(
                    table.tableID,
                    it.foodSubItem.subItemId,
                    it.foodSubItem.subItemPrice,
                    it.quantity,
                    it.quantity * it.foodSubItem.subItemPrice,
                    it.foodItem.itemId,
                    0f
                )
            } as ArrayList
        val placeOrderRequest = PlaceOrderRequest(table.tableID, orderItems)

        viewModelScope.launch {
            billingRepository.placeTableOrders(placeOrderRequest)
                .catch {
                    _isLoading.value = false
                    _errorMessage.value = R.string.unable_place_table_order
                }
                .collect {
                    when (it) {
                        is RequestStatus.Waiting -> {
                            _isLoading.value = true
                        }
                        is RequestStatus.Success -> {
                            _isLoading.value = false
                            tables.value!!.apply {
                                set(index, table.apply { isBooked = true })
                            }
                            _tableItemChanged.value = TableItemChanged(index, table)
                            _selectedFoodItem.value = null
                            _foodSubItems.value = null
                            _isOrderPlaced.value = true
                            getTableActiveOrders()
                        }
                        is RequestStatus.Error -> {
                            _isLoading.value = false
                            _errorMessage.value = it.message
                        }
                    }
                }
        }
    }

    fun clearTableOrders() {
        _tableOrders.value = ArrayList()
    }

    fun saveTableOrders(foodCategory: FoodCategory) {
        if (activeTableOrders.value != null && activeTableOrders.value!!.size > 0) {
            val orderItems = when (foodCategory) {
                FoodCategory.SEKUWA_ITEM -> activeTableOrders.value!!.filter { it.isExtraColumn && it.isOrder }
                FoodCategory.BAR_ITEM -> activeTableOrders.value!!.filter { it.isBar && it.isOrder }
                FoodCategory.COFFEE_ITEM -> activeTableOrders.value!!.filter { it.isCoffee && it.isOrder }
                FoodCategory.KITCHEN_ITEM -> activeTableOrders.value!!.filter { !it.isCoffee && !it.isExtraColumn && !it.isBar && it.isOrder }
            }

            val printTitle = when (foodCategory) {
                FoodCategory.SEKUWA_ITEM -> "SEKUWA"
                FoodCategory.BAR_ITEM -> "BOT"
                FoodCategory.COFFEE_ITEM -> "COFFEE"
                FoodCategory.KITCHEN_ITEM -> "KOT"
            }

            val saveOrderItems = orderItems.map {
                SaveOrderRequest.SaveOrderItem(
                    tableId = selectedTable.value!!.tableID,
                    subItemId = it.subItemId,
                    itemId = it.itemId,
                    subItemName = it.subItemName,
                    quantity = it.quantity,
                    remarks = it.remarks
                )
            } as ArrayList
            val saveOrderRequestBody = SaveOrderRequest(
                tableId = selectedTable.value!!.tableID,
                printTitle = printTitle,
                tableName = selectedTable.value!!.tableName,
                billNumber = orderItems.last().billNumber,
                orderItemList = saveOrderItems
            )

            viewModelScope.launch {
                billingRepository.saveTableOrders(saveOrderRequestBody)
                    .catch {
                        _isLoading.value = false
                        _errorMessage.value = R.string.unable_save_table_order
                    }
                    .collect {
                        when (it) {
                            is RequestStatus.Waiting -> {
                                _isLoading.value = true
                            }
                            is RequestStatus.Success -> {
                                _isLoading.value = false
                                _activeTableOrders.value = activeTableOrders.value?.map { item ->
                                    if (orderItems.contains(item)) item.isOrder = false
                                    item
                                } as ArrayList<ActiveOrderItem>
                            }
                            is RequestStatus.Error -> {
                                _isLoading.value = false
                                _errorMessage.value = it.message
                            }
                        }
                    }
            }
        }
    }

    fun cancelTableOrder(remarks: String) {
        if (cancelOrderItem.value != null) {
            val printTitle = when {
                cancelOrderItem.value!!.isExtraColumn -> "SEKUWA"
                cancelOrderItem.value!!.isBar -> "BOT"
                cancelOrderItem.value!!.isCoffee -> "COFFEE"
                else -> "KOT"
            }

            val item = SaveOrderRequest.SaveOrderItem(
                tableId = selectedTable.value!!.tableID,
                subItemId = cancelOrderItem.value!!.subItemId,
                itemId = cancelOrderItem.value!!.itemId,
                subItemName = cancelOrderItem.value!!.subItemName,
                quantity = cancelOrderItem.value!!.quantity,
                remarks = cancelOrderItem.value!!.remarks
            )

            val saveOrderRequestBody = SaveOrderRequest(
                tableId = selectedTable.value!!.tableID,
                printTitle = printTitle,
                tableName = selectedTable.value!!.tableName,
                billNumber = cancelOrderItem.value!!.billNumber,
                orderItemList = ArrayList<SaveOrderRequest.SaveOrderItem>().apply { add(item) }
            )

            val cancelOrderBody = CancelOrderBody(
                tableId = selectedTable.value!!.tableID,
                orderItemId = cancelOrderItem.value!!.itemId,
                remarks = remarks
            )

            viewModelScope.launch {
                billingRepository.cancelTableOrder(saveOrderRequestBody, cancelOrderBody)
                    .catch {
                        _isLoading.value = false
                        _errorMessage.value = R.string.unable_cancel_table_order
                    }
                    .collect {
                        when (it) {
                            is RequestStatus.Waiting -> {
                                _isLoading.value = true
                            }
                            is RequestStatus.Success -> {
                                _isLoading.value = false
                                val list = ArrayList<ActiveOrderItem>()
                                activeTableOrders.value!!.forEach { order ->
                                    if (cancelOrderItem.value!! == order && order.quantity > 1f)
                                        list.add(order.apply { quantity -= 1 })

                                }
                                _activeTableOrders.value = list
                                _cancelOrderItem.value = null
                                _isCancelComplete.value = true
                            }
                            is RequestStatus.Error -> {
                                _isLoading.value = false
                                _errorMessage.value = it.message
                            }
                        }
                    }
            }
        }
    }

    fun searchSubItems(searchTerm: String) {
        viewModelScope.launch {
            billingRepository.searchSubItems(searchTerm)
                .catch {
                    _isLoading.value = false
                    _errorMessage.value = R.string.unable_search_sub_items
                }
                .collect {
                    when (it) {
                        is RequestStatus.Waiting -> {
                            _isLoading.value = true
                        }
                        is RequestStatus.Success -> {
                            _isLoading.value = false
                            _searchResult.value = it.data?.data
                        }
                        is RequestStatus.Error -> {
                            _isLoading.value = false
                            _errorMessage.value = it.message
                        }
                    }
                }
        }
    }

    fun changeTableNumber(from: Table, to: Table) {
        viewModelScope.launch {
            billingRepository.changeTableNumber(from.tableID, to.tableID)
                .catch {
                    _isLoading.value = false
                    _errorMessage.value = R.string.unable_change_table_number
                }
                .collect {
                    when (it) {
                        is RequestStatus.Waiting -> {
                            _isLoading.value = true
                        }
                        is RequestStatus.Success -> {
                            _isLoading.value = false
                            from.apply { isBooked = false }
                            to.apply { isBooked = true }
                            val fromTableIndex = tables.value!!.indexOf(from)
                            val toTableIndex = tables.value!!.indexOf(to)
                            tables.value?.apply {
                                set(fromTableIndex, from)
                                set(toTableIndex, to)
                            }
                            _selectedTable.value = to
                            _tableItemChanged.value =
                                TableItemChanged(fromTableIndex, from)
                            _tableItemChanged.value =
                                TableItemChanged(toTableIndex, to)
                        }
                        is RequestStatus.Error -> {
                            _isLoading.value = false
                            _errorMessage.value = it.message
                        }
                    }
                }
        }
    }

    fun mergeTable(from: Table, to: Table) {
        viewModelScope.launch {
            billingRepository.mergeTable(from.tableID, to.tableID)
                .catch {
                    _isLoading.value = false
                    _errorMessage.value = R.string.unable_merge_tables
                }
                .collect {
                    when (it) {
                        is RequestStatus.Waiting -> {
                            _isLoading.value = true
                        }
                        is RequestStatus.Success -> {
                            _isLoading.value = false
                            from.apply { isBooked = false }
                            val fromTableIndex = tables.value!!.indexOf(from)
                            tables.value?.apply {
                                set(fromTableIndex, from)
                            }
                            _selectedTable.value = to
                            _tableItemChanged.value =
                                TableItemChanged(fromTableIndex, from)

                        }
                        is RequestStatus.Error -> {
                            _isLoading.value = false
                            _errorMessage.value = it.message
                        }
                    }
                }
        }
    }
}