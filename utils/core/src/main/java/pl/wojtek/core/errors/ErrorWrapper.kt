package pl.wojtek.core.errors

import androidx.fragment.app.Fragment

/**
 *
 */


data class ErrorWrapper(val title: String,
                        val text: String,
                        val handle: ((Fragment) -> Unit)? = null)