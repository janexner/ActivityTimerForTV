package com.exner.tools.activitytimerfortv.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


private val Context.dataStore by preferencesDataStore("preferences")

@Singleton
class UserPreferencesManager @Inject constructor(
    @ApplicationContext appContext: Context
) {

    private val userDataStorePreferences = appContext.dataStore

    fun beforeCountingWait(): Flow<Boolean> {
        return userDataStorePreferences.data.catch {
            emit(emptyPreferences())
        }.map { preferences ->
            preferences[KEY_BEFORE_COUNTING_WAIT] ?: false
        }
    }

    suspend fun setBeforeCountingWait(newBeforeCountingWait: Boolean) {
        userDataStorePreferences.edit { preferences ->
            preferences[KEY_BEFORE_COUNTING_WAIT] = newBeforeCountingWait
        }
    }

    fun howLongToWaitBeforeCounting(): Flow<Int> {
        return userDataStorePreferences.data.catch {
            emit(emptyPreferences())
        }.map { preferences ->
            preferences[KEY_HOW_LONG_TO_WAIT_BEFORE_COUNTING] ?: 5
        }
    }

    suspend fun setHowLongToWaitBeforeCounting(newHowLongToWaitBeforeCounting: Int) {
        userDataStorePreferences.edit { preferences ->
            preferences[KEY_HOW_LONG_TO_WAIT_BEFORE_COUNTING] = newHowLongToWaitBeforeCounting
        }
    }

    fun countBackwards(): Flow<Boolean> {
        return userDataStorePreferences.data.catch {
            emit(emptyPreferences())
        }.map { preferences ->
            preferences[KEY_COUNT_BACKWARDS] ?: false
        }
    }

    suspend fun setCountBackwards(newCountBackwards: Boolean) {
        userDataStorePreferences.edit { preferences ->
            preferences[KEY_COUNT_BACKWARDS] = newCountBackwards
        }
    }

    fun noSounds(): Flow<Boolean> {
        return userDataStorePreferences.data.catch {
            emit(emptyPreferences())
        }.map { preferences ->
            preferences[KEY_NO_SOUNDS] ?: false
        }
    }

    suspend fun setNoSounds(newNoSounds: Boolean) {
        userDataStorePreferences.edit { preferences ->
            preferences[KEY_NO_SOUNDS] = newNoSounds
        }
    }

    fun importAndUploadRestOfChainAutomatically(): Flow<Boolean> {
        return userDataStorePreferences.data.catch {
            emit(emptyPreferences())
        }.map { preferences ->
            preferences[KEY_IMPORT_AND_UPLOAD_REST_OF_CHAIN_AUTOMATICALLY] ?: false
        }
    }

    suspend fun setImportAndUploadRestOfChainAutomatically(doThemAll: Boolean) {
        userDataStorePreferences.edit { preferences ->
            preferences[KEY_IMPORT_AND_UPLOAD_REST_OF_CHAIN_AUTOMATICALLY] = doThemAll
        }
    }

    private companion object {
        val KEY_BEFORE_COUNTING_WAIT = booleanPreferencesKey(name = "before_counting_wait")
        val KEY_HOW_LONG_TO_WAIT_BEFORE_COUNTING =
            intPreferencesKey(name = "how_long_to_wait_before_counting")
        val KEY_COUNT_BACKWARDS = booleanPreferencesKey(name = "count_backwards")
        val KEY_NO_SOUNDS = booleanPreferencesKey(name = "no_sounds")
        val KEY_IMPORT_AND_UPLOAD_REST_OF_CHAIN_AUTOMATICALLY = booleanPreferencesKey(name = "import_and_upload_rest_of_chain_automatically")
    }
}