package pl.wojtek.preferences

import android.content.SharedPreferences
import com.google.gson.Gson

/**
 *
 */


interface PreferencesFacade {
    suspend fun saveString(text: String, key: String)
    suspend fun retrieveString(key: String): String?
    suspend fun saveInt(number: Int, key: String)
    suspend fun saveLong(value: Long, key: String)
    suspend fun retrieveLong(key: String, defValue: Long = -1): Long
    suspend fun retrieveInt(key: String): Int
    suspend fun saveBoolean(value: Boolean, key: String)
    suspend fun retrieveBoolean(key: String): Boolean
    suspend fun putObject(key: String, any: Any)
    suspend fun <T> getObject(key: String, type: Class<T>): T?
    suspend fun removeKey(key: String)
    suspend fun containsKey(key: String):Boolean
}


internal class PreferencesFacadeImp(private val sharedPreferences: SharedPreferences) : PreferencesFacade {
    override suspend fun saveString(text: String, key: String) {
        sharedPreferences.edit().putString(key, text).apply()
    }

    override suspend fun retrieveString(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    override suspend fun saveInt(number: Int, key: String) {
        sharedPreferences.edit().putInt(key, number).apply()
    }

    override suspend fun retrieveInt(key: String): Int {
        return sharedPreferences.getInt(key, -1000)
    }

    override suspend fun saveBoolean(value: Boolean, key: String) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    override suspend fun retrieveBoolean(key: String) = sharedPreferences.getBoolean(key, false)

    override suspend fun saveLong(value: Long, key: String) {
        sharedPreferences.edit().putLong(key, value).apply()
    }

    override suspend fun retrieveLong(key: String, defValue: Long): Long {
        return sharedPreferences.getLong(key, defValue)
    }

    override suspend fun putObject(key: String, any: Any) {
        val gson = Gson()
        saveString(gson.toJson(any), key)
    }

    override suspend fun <T> getObject(key: String, type: Class<T>): T? {
        val gson = Gson()
        return gson.fromJson(retrieveString(key), type)
    }

    override suspend fun removeKey(key: String) = sharedPreferences.edit().remove(key).apply()
    override suspend fun containsKey(key: String) = sharedPreferences.contains(key)

}