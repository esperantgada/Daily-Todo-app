package eg.esperantgada.dailytodo.repository

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.emptyPreferences
import androidx.datastore.preferences.preferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

enum class SortOrder{BY_NAME, BY_DATE, BY_TITLE}
private const val TAG = "PreferenceRepository"

@Singleton
class PreferenceRepository @Inject constructor(@ApplicationContext context : Context) {

    //The dataStore itself with its name
    private val dataStore = context.createDataStore(name = "User_preferences")

    val preferenceFlow = dataStore.data
        .catch { exception ->
            if (exception is IOException){
                Log.e(TAG, "Error reading preferences", exception)
                emit(emptyPreferences())
            }else{
                throw exception
            }
        }
        .map { preference ->
            val sortOrder = SortOrder.valueOf(
                preference[PreferenceKeys.SORT_ORDER]?: SortOrder.BY_DATE.name
            )

            val hideCompleted = preference[PreferenceKeys.HIDE_COMPLETED] ?: false
            FilterPreferences(sortOrder, hideCompleted)
        }

    //Keys of the values we want to store in the dataStore
    private  object PreferenceKeys{
        val SORT_ORDER = preferencesKey<String>("sort_order")
        val HIDE_COMPLETED = preferencesKey<Boolean>("hide_completed")
    }


    //Updates the preferences in the dataStore
    suspend fun updateSortOrder(sortOrder: SortOrder){
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.SORT_ORDER] = sortOrder.name
        }
    }

    suspend fun updateHideCompleted(hideCompleted : Boolean){
        dataStore.edit { preference ->
            preference[PreferenceKeys.HIDE_COMPLETED] = hideCompleted
        }
    }

}