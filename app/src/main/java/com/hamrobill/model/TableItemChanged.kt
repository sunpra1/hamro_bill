package com.hamrobill.model

import com.hamrobill.data.pojo.Table

data class TableItemChanged(val updatedTableItemPosition: Int, val updatedTableItem: Table, val isSelected: Boolean = false)