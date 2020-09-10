package pl.wojtek.core.extensions

import android.view.View

fun View.setVisibleOrGone(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}

fun View.setVisibleOrInvisible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.INVISIBLE
}


fun View.getString(id: Int) = resources.getString(id)
fun View.getString(id: Int,vararg data:Any) = resources.getString(id,data)