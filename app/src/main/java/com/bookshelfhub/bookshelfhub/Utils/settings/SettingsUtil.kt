package com.bookshelfhub.bookshelfhub.Utils.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.bookshelfhub.bookshelfhub.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsUtil @Inject constructor  (private val context: Context) {

        companion object{
            private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("BookshelfHub")
        }

    suspend fun getLong(key:String, defaultValue:Long):Long{
        val prefKey = longPreferencesKey(key)
        val exampleCounterFlow: Flow<Long> = context.dataStore.data
            .map { preferences ->
                preferences[prefKey] ?: defaultValue
            }
        return exampleCounterFlow.first()
    }

    suspend fun setLong(key: String, value: Long){
        val prefKey = longPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences[prefKey] = value
        }
    }

    suspend fun getInt(key:String, defaultValue:Int):Int{
        val prefKey = intPreferencesKey(key)
        val exampleCounterFlow: Flow<Int> = context.dataStore.data
            .map { preferences ->
               preferences[prefKey] ?: defaultValue
            }
       return exampleCounterFlow.first()
    }

    suspend fun setInt(key: String, value: Int){
        val prefKey = intPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences[prefKey] = value
        }
    }

    suspend fun getString(key:String):String?{
        val prefKey = stringPreferencesKey(key)
        val exampleCounterFlow: Flow<String?> = context.dataStore.data
            .map { preferences ->
                preferences[prefKey]
            }
        return exampleCounterFlow.first()
    }

    suspend fun setString(key: String, value: String){
        val prefKey = stringPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences[prefKey] = value
        }
    }

    suspend fun getBoolean(key:String, defaultValue: Boolean):Boolean{
        val prefKey = booleanPreferencesKey(key)
        val exampleCounterFlow: Flow<Boolean?> = context.dataStore.data
            .map { preferences ->
                preferences[prefKey]
            }
        return exampleCounterFlow.first()?:defaultValue
    }

    suspend fun setBoolean(key: String, value: Boolean){
        val prefKey = booleanPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences[prefKey] = value
        }
    }

}