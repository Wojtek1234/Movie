package pl.wojtek.core.extensions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pl.wojtek.core.R

class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

fun <T> itemCallback(function: ItemCallback<T>.() -> Unit) = ItemCallback<T>()
    .apply(function)

fun <T : HasKey> itemCallback() = itemCallback<T> {
    areItemsTheSame { t, t2 -> t.key == t2.key }
}

interface HasKey {
    var key: String
}

class ItemCallback<T> : DiffUtil.ItemCallback<T>() {

    private var _areItemsTheSame: (T, T) -> Boolean = { _, _ -> throw NotImplementedError() }
    private var _areContentsTheSame: (T, T) -> Boolean = { t1, t2 -> t1 == t2 }

    fun areItemsTheSame(function: (T, T) -> Boolean) {
        _areItemsTheSame = function
    }

    fun areContentsTheSame(function: (T, T) -> Boolean) {
        _areContentsTheSame = function
    }

    override fun areItemsTheSame(oldItem: T, newItem: T) = _areItemsTheSame(oldItem, newItem)

    override fun areContentsTheSame(oldItem: T, newItem: T) = _areContentsTheSame(oldItem, newItem)
}

inline fun <T> listAdapter(vhResourceId: Int, diffCallback: ItemCallback<T>, crossinline onBindView: View.(position: Int, item: T) -> Unit) =
    object : ListAdapter<T, ViewHolder>(diffCallback) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(LayoutInflater.from(parent.context).inflate(vhResourceId, parent, false))

        override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.itemView.onBindView(position, getItem(position))
    }




interface ListElement{
    abstract val layoutRes: Int
    abstract val id: String
    fun isTheSame(obj: ListElement): Boolean = obj.id == this.id
    fun isContentTheSame(obj: ListElement): Boolean = obj == this
}


fun RecyclerView.scheduleAnimationIfEmptyAdapter(horizontal: Boolean = false) {
    if (this.adapter?.itemCount == 0) {
        val controller =
            AnimationUtils.loadLayoutAnimation(context, if (!horizontal) R.anim.layout_animation else R.anim.layout_animation_horizontal)
        layoutAnimation = controller
        scheduleLayoutAnimation()
    }
}