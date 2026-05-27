package edu.saracasas.fichajesapp.data.remote

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import edu.saracasas.fichajesapp.viewmodels.MainViewModel
import kotlinx.coroutines.flow.firstOrNull

/**
 * TokenManager es una clase que se encarga de gestionar el token de autenticación utilizando DataStore.
 * Permite guardar, obtener y eliminar el token de forma segura.
 */
class TokenManager(private val context: Context) {
    val TOKEN_KEY = stringPreferencesKey("auth_token")
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    suspend fun saveAuthToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    suspend fun getAuthToken(): String? {
        val preferences = context.dataStore.data.firstOrNull()
        return preferences?.get(TOKEN_KEY)
    }

    suspend fun clearToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
        }
    }
}