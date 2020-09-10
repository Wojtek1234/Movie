package pl.wojtek.core.extensions

import androidx.core.widget.NestedScrollView

/**
 *
 */


fun NestedScrollView.scrollToBottom() {
    val lastChild = getChildAt(childCount - 1)
    val bottom = lastChild.bottom + paddingBottom
    val delta = bottom - (scrollY + height)
    smoothScrollBy(0, delta)
}