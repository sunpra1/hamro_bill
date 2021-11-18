package com.hamrobill.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RVGridItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        (parent.layoutManager as GridLayoutManager)
        outRect.top = space / 2
        outRect.left = space
        outRect.right = space
        outRect.bottom = space / 2
    }
}