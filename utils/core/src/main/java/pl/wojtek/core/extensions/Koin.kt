package pl.wojtek.core.extensions

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel

/**
 *
 */


@SuppressLint("UseRequireInsteadOfGet")
inline fun <reified T : ViewModel> Fragment.viewModelFromNavParent() = parentFragment!!.parentFragment!!.getViewModel<T>()

inline fun <reified T : ViewModel> Fragment.viewModelFromParent() = parentFragment!!.getViewModel<T>()
