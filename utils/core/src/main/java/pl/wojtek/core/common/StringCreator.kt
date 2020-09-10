package pl.wojtek.core.common

import android.content.res.Resources
import java.text.SimpleDateFormat
import java.util.*

/**
 *
 */

interface StringCreator {
    fun createString(id: Int): String
    fun createString(id: Int, text: String): String
    fun createDateWithTime(date: Date): String
    fun createDate(date: Date): String
    fun createDateWithFullMonth(date: Date): String
    fun createStringFromArray(arrayId: Int, position: Int): String
}

internal class StringCreatorImp(private val resources: Resources) : StringCreator {
    override fun createString(id: Int) = resources.getString(id)

    override fun createString(id: Int, text: String) = resources.getString(id, text)

    override fun createDateWithTime(date: Date): String {
        return SimpleDateFormat("dd MMMM',' yyyy'|'HH:mm", Locale.getDefault()).format(date)

    }

    override fun createDate(date: Date): String {
        return SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(date)
    }

    override fun createDateWithFullMonth(date: Date): String {
        return SimpleDateFormat("dd MMMM yyyy' r.'", Locale.getDefault()).format(date)
    }

    override fun createStringFromArray(arrayId: Int, position: Int): String = resources.getStringArray(arrayId)[position]

}