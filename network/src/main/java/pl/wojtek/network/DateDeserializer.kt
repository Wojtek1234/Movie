package pl.wojtek.network


import android.icu.util.TimeZone
import android.os.Build
import androidx.annotation.RequiresApi
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.ToJson
import java.text.ParseException
import java.util.*

/**
 *
 */


class MoshiDateDeserializer(listOfFormats: List<String>) {

    @RequiresApi(Build.VERSION_CODES.N)
    private val listOfSimpleDateFormats = listOfFormats.map {
        android.icu.text.SimpleDateFormat(it, Locale.getDefault()).apply {
            this.timeZone = TimeZone.getTimeZone("UTC")
        }
    }


    private val listOfSimpleDateFormatsOLD = listOfFormats.map {
        java.text.SimpleDateFormat(it, Locale.getDefault()).apply {
            this.timeZone = java.util.TimeZone.getTimeZone("UTC")
        }
    }


    @ToJson
    fun toJson(date: Date?): String? = when {
        date == null -> null
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
            listOfSimpleDateFormats[0].format(date)
        }
        else -> {
            listOfSimpleDateFormatsOLD[0].format(date)
        }
    }


    @FromJson
    fun fromJson(date: String?): Date? {
        return try {
            parseDate(date)
        } catch (e: ParseException) {
            throw JsonDataException(e.message, e)
        }
    }


    @Throws(ParseException::class)
    private fun parseDate(dateString: String?): Date? {
        return if (dateString != null && dateString.trim { it <= ' ' }.isNotEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                listOfSimpleDateFormats.forEach {
                    try {
                        return it.parse(dateString)
                    } catch (pe: ParseException) {

                    }
                }
            } else {
                listOfSimpleDateFormatsOLD.forEach {
                    try {
                        return it.parse(dateString)
                    } catch (pe: ParseException) {

                    }
                }
            }
            return null
        } else {
            null
        }
    }
}