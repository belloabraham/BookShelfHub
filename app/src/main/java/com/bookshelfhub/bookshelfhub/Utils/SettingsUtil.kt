package com.bookshelfhub.bookshelfhub.Utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.bookshelfhub.bookshelfhub.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class SettingsUtil(private val context: Context) {

    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = context.getString(R.string.app_name))

    suspend fun getInt(key:String):Int{
        val prefKey = intPreferencesKey(key)
        val exampleCounterFlow: Flow<Int> = context.dataStore.data
            .map { preferences ->
               preferences[prefKey] ?: 0
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


}