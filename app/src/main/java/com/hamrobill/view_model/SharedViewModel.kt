package com.hamrobill.view_model

import androidx.lifecycle.*
import com.hamrobill.R
import com.hamrobill.data.pojo.*
import com.hamrobill.data.repository.BillingRepository
import com.hamrobill.di.scope.ActivityScope
import com.hamrobill.model.FoodCategory
import com.hamrobill.model.OrderItem
import com.hamrobill.model.TableItemChanged
import com.hamrobill.utility.Event
import com.hamrobill.utility.NetworkConnectivity
import com.hamrobill.utility.RequestStatus
import com.hamrobill.utility.SharedPreferenceStorage
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@ActivityScope
class SharedViewModel @Inject constructor(
    private val networkConnectivity: NetworkConnectivity,
    private val billingRepository: BillingRepository,
    private val sharedPreferenceStorage: SharedPreferenceStorage
) : ViewModel() {

    private var _isLoading: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val isLoading: LiveData<Event<Boolean>> = _isLoading

    private val _errorMessage: MutableLiveData<Event<Any>> = MutableLiveData()
    val errorMessage: LiveData<Event<Any>> = _errorMessage

    private val _toast: MutableLiveData<Event<Any>> = MutableLiveData()
    val toast: LiveData<Event<Any>> = _toast

    private val _isNetworkAvailable: MutableLiveData<Boolean> = MutableLiveData()
    val isNetworkAvailable: LiveData<Boolean> = _isNetworkAvailable

    private val _tables: MutableLiveData<ArrayList<Table>> = MutableLiveData()
    val tables: LiveData<ArrayList<Table>> = _tables

    private val _foodItems: MutableLiveData<ArrayList<FoodItem>> = MutableLiveData()
    val foodItems: LiveData<ArrayList<FoodItem>> = _foodItems

    private val _isInitialDataLoaded: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val isInitialDataLoaded: LiveData<Event<Boolean>> = _isInitialDataLoaded

    private val _selectedTable: MutableLiveData<Table?> = MutableLiveData()
    val selectedTable: LiveData<Table?> = _selectedTable

    private val _tableOrders: MutableLiveData<ArrayList<OrderItem>?> = MutableLiveData()
    val tableOrders: LiveData<ArrayList<OrderItem>?> = _tableOrders

    private val _activeTableOrders: MutableLiveData<ArrayList<ActiveOrderItem>?> = MutableLiveData()
    val activeTableOrders: LiveData<ArrayList<ActiveOrderItem>?> = _activeTableOrders

    private val _cancellableTableOrders: MutableLiveData<ArrayList<CancellableOrderItem>?> =
        MutableLiveData()
    val cancellableTableOrders: LiveData<ArrayList<CancellableOrderItem>?> = _cancellableTableOrders

    private val _selectedFoodItem: MutableLiveData<FoodItem?> = MutableLiveData()
    val selectedFoodItem: LiveData<FoodItem?> = _selectedFoodItem

    private val _foodSubItems: MutableLiveData<ArrayList<FoodSubItem>?> = MutableLiveData()
    val foodSubItems: LiveData<ArrayList<FoodSubItem>?> = _foodSubItems

    private val _tableItemChanged: MutableLiveData<TableItemChanged> = MutableLiveData()
    val tableItemChanged: LiveData<TableItemChanged> = _tableItemChanged

    private val _isOrderPlaced: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val isOrderPlaced: LiveData<Event<Boolean>> = _isOrderPlaced

    private val _searchResult: MutableLiveData<ArrayList<FoodSubItem>> = MutableLiveData()
    val searchResult: LiveData<ArrayList<FoodSubItem>> = _searchResult

    private val _cancelOrderItem: MutableLiveData<CancellableOrderItem?> = MutableLiveData()
    private val cancelOrderItem: LiveData<CancellableOrderItem?> = _cancelOrderItem

    private val _isPasswordChanged: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val isPasswordChanged: LiveData<Event<Boolean>> = _isPasswordChanged

    private val locationObserver = Observer<Boolean> { _isNetworkAvailable.value = it }

    init {
        networkConnectivity.observeForever(locationObserver)
    }

    override fun onCleared() {
        super.onCleared()
        networkConnectivity.removeObserver(locationObserver)
    }

    fun setSelectedFoodItem(foodItem: FoodItem?) {
        _selectedFoodItem.value = foodItem
    }

    fun setTableOrders(tableOrders: ArrayList<OrderItem>?) {
        _tableOrders.value = tableOrders
    }

    fun setSelectedTable(table: Table?) {
        _selectedTable.value = table
    }

    fun setActiveTableOrders(tableOrders: ArrayList<ActiveOrderItem>?) {
        _activeTableOrders.value = tableOrders
    }

    fun setCancellableTableOrders(cancellableTableOrders: ArrayList<CancellableOrderItem>?) {
        _cancellableTableOrders.value = cancellableTableOrders
    }

    fun setCancelOrderItem(cancellableOrderItem: CancellableOrderItem?) {
        if (cancelOrderItem.value == cancellableOrderItem) {
            _cancelOrderItem.value = null
        } else {
            _cancelOrderItem.value = cancellableOrderItem
        }
    }

    fun setFoodSubItems(foodSubItems: ArrayList<FoodSubItem>?) {
        _foodSubItems.value = foodSubItems
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
            tableOrders.value?.indexOfFirst {
                it.table.tableID == selectedTable.value!!.tableID
                        && it.foodItem.itemId == foodItem.itemId
                        && it.foodSubItem.subItemId == foodSubItem.subItemId
            } ?: -1
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
        } else if (quantity > 0) {
            _tableOrders.value = tableOrders.value?.apply {
                add(orderItem)
            } ?: arrayListOf(orderItem)
        }
    }

    fun getTablesAndFoodItems() {
        viewModelScope.launch {
            billingRepository.getTablesAndFoodItems()
                .catch {
                    _isLoading.value = Event(false)
                    _errorMessage.value = Event(R.string.unable_fetch_tables_and_food_items)
                }
                .collect {
                    when (it) {
                        is RequestStatus.Waiting -> {
                            _isLoading.value = Event(true)
                        }
                        is RequestStatus.Success -> {
                            _isLoading.value = Event(false)
                            _tables.value =
                                it.data.tables?.data?.distinctBy { t -> t.tableID } as ArrayList<Table>
                            _foodItems.value = it.data.foodItems?.data
                            _isInitialDataLoaded.value = Event(true)
                        }
                        is RequestStatus.Error -> {
                            _isLoading.value = Event(false)
                            _errorMessage.value = Event(it.message)
                        }
                    }
                }
        }
    }

    fun getTableActiveOrdersAndCancelableOrderItems() {
        viewModelScope.launch {
            billingRepository.getTableActiveOrdersAndCancelableOrderItems(selectedTable.value!!.tableID)
                .catch {
                    _isLoading.value = Event(false)
                    _errorMessage.value = Event(R.string.unable_fetch_table_details)
                }
                .collect {
                    when (it) {
                        is RequestStatus.Waiting -> {
                            _isLoading.value = Event(true)
                        }
                        is RequestStatus.Success -> {
                            _isLoading.value = Event(false)
                            _activeTableOrders.value = it.data.activeTableOrders?.data
                            _cancellableTableOrders.value = it.data.cancellableOrderItems?.data
                            if ((it.data.activeTableOrders?.data.isNullOrEmpty() || it.data.cancellableOrderItems?.data.isNullOrEmpty()) && selectedTable.value?.isBooked == true) {
                                val index = _tables.value?.lastIndexOf(selectedTable.value)
                                if (index != null)
                                    _tableItemChanged.value = TableItemChanged(
                                        index,
                                        _tables.value!![index].apply { isBooked = false }
                                    )
                            }
                        }
                        is RequestStatus.Error -> {
                            _isLoading.value = Event(false)
                            _errorMessage.value = Event(it.message)
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
                        _isLoading.value = Event(false)
                        _errorMessage.value = Event(R.string.unable_fetch_food_sub_items)
                    }
                    .collect {
                        when (it) {
                            is RequestStatus.Waiting -> {
                                _isLoading.value = Event(true)
                            }
                            is RequestStatus.Success -> {
                                _isLoading.value = Event(false)
                                _foodSubItems.value = it.data?.data
                                if (it.data?.data.isNullOrEmpty()) {
                                    _toast.value = Event(R.string.food_sub_items_empty)
                                }
                            }
                            is RequestStatus.Error -> {
                                _isLoading.value = Event(false)
                                _errorMessage.value = Event(it.message)
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
                    it.remarks,
                    0f
                )
            } as ArrayList
        val placeOrderRequest = PlaceOrderRequest(
            table.tableID,
            orderItems,
            orderBy = sharedPreferenceStorage.loggedUserName ?: ""
        )

        viewModelScope.launch {
            billingRepository.placeTableOrders(placeOrderRequest)
                .catch {
                    _isLoading.value = Event(false)
                    _errorMessage.value = Event(R.string.unable_place_table_order)
                }
                .collect {
                    when (it) {
                        is RequestStatus.Waiting -> {
                            _isLoading.value = Event(true)
                        }
                        is RequestStatus.Success -> {
                            _isLoading.value = Event(false)
                            tables.value!!.apply {
                                set(index, table.apply { isBooked = true })
                            }
                            _tableItemChanged.value = TableItemChanged(index, table)
                            _selectedFoodItem.value = null
                            _foodSubItems.value = null
                            _tableOrders.value = null
                            _activeTableOrders.value = null
                            _cancellableTableOrders.value = null
                            _isOrderPlaced.value = Event(true)
                            getTableActiveOrdersAndCancelableOrderItems()
                        }
                        is RequestStatus.Error -> {
                            _isLoading.value = Event(false)
                            _errorMessage.value = Event(it.message)
                        }
                    }
                }
        }
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
                orderItemList = saveOrderItems,
                orderBy = sharedPreferenceStorage.loggedUserName ?: ""
            )

            viewModelScope.launch {
                billingRepository.saveTableOrders(saveOrderRequestBody)
                    .catch {
                        _isLoading.value = Event(false)
                        _errorMessage.value = Event(R.string.unable_save_table_order)
                    }
                    .collect {
                        when (it) {
                            is RequestStatus.Waiting -> {
                                _isLoading.value = Event(true)
                            }
                            is RequestStatus.Success -> {
                                _isLoading.value = Event(false)
                                _activeTableOrders.value = activeTableOrders.value?.map { item ->
                                    if (orderItems.contains(item)) item.isOrder = false
                                    item
                                } as ArrayList<ActiveOrderItem>
                                getTableActiveOrdersAndCancelableOrderItems()
                            }
                            is RequestStatus.Error -> {
                                _isLoading.value = Event(false)
                                _errorMessage.value = Event(it.message)
                            }
                        }
                    }
            }
        }
    }

    fun cancelTableOrder(remarks: String) {
        if (cancelOrderItem.value != null) {

            val itemA = cancelOrderItem.value!!

            val printTitle = when {
                itemA.extraColumn -> "SEKUWA"
                itemA.isBar -> "BOT"
                itemA.isCoffee -> "COFFEE"
                else -> "KOT"
            }

            val item = SaveOrderRequest.SaveOrderItem(
                tableId = selectedTable.value!!.tableID,
                subItemId = cancelOrderItem.value!!.subItemId,
                itemId = cancelOrderItem.value!!.itemId,
                subItemName = cancelOrderItem.value!!.subItemName,
                quantity = cancelOrderItem.value!!.quantity,
                remarks = remarks
            )

            val saveOrderRequestBody = SaveOrderRequest(
                tableId = selectedTable.value!!.tableID,
                printTitle = printTitle,
                tableName = selectedTable.value!!.tableName,
                billNumber = cancelOrderItem.value!!.billNumber,
                orderItemList = ArrayList<SaveOrderRequest.SaveOrderItem>().apply { add(item) },
                orderBy = sharedPreferenceStorage.loggedUserName ?: "",
                isDelete = true
            )

            val cancelOrderBody = CancelOrderBody(
                tableId = selectedTable.value!!.tableID,
                orderId = cancelOrderItem.value!!.orderId,
                deleteOrderItems = ArrayList<DeleteOrderItem>()
                    .apply {
                        add(
                            DeleteOrderItem(
                                tableId = selectedTable.value!!.tableID,
                                orderItemId = cancelOrderItem.value!!.orderId,
                                remarks = remarks
                            )
                        )
                    }
            )

            viewModelScope.launch {
                billingRepository.cancelTableOrder(
                    saveOrderRequestBody,
                    cancelOrderBody,
                    !itemA.isOrder
                )
                    .catch {
                        _isLoading.value = Event(false)
                        _errorMessage.value = Event(R.string.unable_cancel_table_order)
                    }
                    .collect {
                        when (it) {
                            is RequestStatus.Waiting -> {
                                _isLoading.value = Event(true)
                            }
                            is RequestStatus.Success -> {
                                _isLoading.value = Event(false)
                                getTableActiveOrdersAndCancelableOrderItems()
                                _toast.value = Event(R.string.order_has_cancelled)
                                _cancelOrderItem.value = null
                            }
                            is RequestStatus.Error -> {
                                _isLoading.value = Event(false)
                                _errorMessage.value = Event(it.message)
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
                    _isLoading.value = Event(false)
                    _errorMessage.value = Event(R.string.unable_search_sub_items)
                }
                .collect {
                    when (it) {
                        is RequestStatus.Waiting -> {
                            _isLoading.value = Event(true)
                        }
                        is RequestStatus.Success -> {
                            _isLoading.value = Event(false)
                            _searchResult.value = it.data?.data
                        }
                        is RequestStatus.Error -> {
                            _isLoading.value = Event(false)
                            _errorMessage.value = Event(it.message)
                        }
                    }
                }
        }
    }

    fun changeTableNumber(from: Table, to: Table) {
        viewModelScope.launch {
            billingRepository.changeTableNumber(from.tableID, to.tableID)
                .catch {
                    _isLoading.value = Event(false)
                    _errorMessage.value = Event(R.string.unable_change_table_number)
                }
                .collect {
                    when (it) {
                        is RequestStatus.Waiting -> {
                            _isLoading.value = Event(true)
                        }
                        is RequestStatus.Success -> {
                            _isLoading.value = Event(false)
                            from.apply { isBooked = false }
                            to.apply { isBooked = true }
                            val fromTableIndex = tables.value!!.indexOf(from)
                            val toTableIndex = tables.value!!.indexOf(to)
                            val selectedTableIndex = tables.value!!.indexOf(selectedTable.value)
                            tables.value?.apply {
                                set(fromTableIndex, from)
                                set(toTableIndex, to)
                            }
                            if (fromTableIndex == selectedTableIndex || toTableIndex == selectedTableIndex)
                                _selectedTable.value = to
                            _tableItemChanged.value =
                                TableItemChanged(fromTableIndex, from)
                            _tableItemChanged.value =
                                TableItemChanged(
                                    toTableIndex,
                                    to,
                                    fromTableIndex == selectedTableIndex || toTableIndex == selectedTableIndex
                                )
                        }
                        is RequestStatus.Error -> {
                            _isLoading.value = Event(false)
                            _errorMessage.value = Event(it.message)
                        }
                    }
                }
        }
    }

    fun mergeTable(from: Table, to: Table) {
        viewModelScope.launch {
            billingRepository.mergeTable(from.tableID, to.tableID)
                .catch {
                    _isLoading.value = Event(false)
                    _errorMessage.value = Event(R.string.unable_merge_tables)
                }
                .collect {
                    when (it) {
                        is RequestStatus.Waiting -> {
                            _isLoading.value = Event(true)
                        }
                        is RequestStatus.Success -> {
                            _isLoading.value = Event(false)
                            from.apply { isBooked = false }
                            val fromTableIndex = tables.value!!.indexOf(from)
                            val toTableIndex = tables.value!!.indexOf(to)
                            val selectedTableIndex = tables.value!!.indexOf(selectedTable.value)
                            tables.value?.apply {
                                set(fromTableIndex, from)
                            }
                            if (fromTableIndex == selectedTableIndex || toTableIndex == selectedTableIndex)
                                _selectedTable.value = to
                            _tableItemChanged.value =
                                TableItemChanged(fromTableIndex, from)
                            _tableItemChanged.value =
                                TableItemChanged(
                                    toTableIndex,
                                    to,
                                    fromTableIndex == selectedTableIndex || toTableIndex == selectedTableIndex
                                )

                        }
                        is RequestStatus.Error -> {
                            _isLoading.value = Event(false)
                            _errorMessage.value = Event(it.message)
                        }
                    }
                }
        }
    }

    fun changePassword(requestBody: ChangePasswordRequestBody) {
        viewModelScope.launch {
            billingRepository.changePassword(requestBody).collect {
                when (it) {
                    is RequestStatus.Waiting -> {
                        _isLoading.value = Event(true)
                    }
                    is RequestStatus.Success -> {
                        _isLoading.value = Event(false)
                        _isPasswordChanged.value = Event(true)
                        _toast.value = Event("Password updated successfully.")
                    }
                    is RequestStatus.Error -> {
                        _isLoading.value = Event(false)
                        _toast.value = Event(it.message)
                    }
                }
            }
        }
    }
}