package eg.esperantgada.dailytodo.sharepreference

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

const val PREFERENCE_NAME = "Todo_Preference"

class TodoSharePreference (context: Context) {

    private val sharePreference : SharedPreferences =
        context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)


    fun saveSpinnerSelectedItems(itemKey : String, items : MutableList<String>){
        val gson = Gson()
        val listToGson = gson.toJson(items)
        val editor : SharedPreferences.Editor = sharePreference.edit()
        editor.putString(itemKey, listToGson)
        editor.apply()
    }

    fun getSpinnerSavedItems(itemKey : String) : MutableList<String>{
        val gson = Gson()
        val json = sharePreference.getString(itemKey, null)
        val jsonToList = object : TypeToken<ArrayList<String>>(){}.type

        return gson.fromJson(json, jsonToList)
    }

    fun saveRingtoneTitle(titleKey : String, title : String){
        val editor : SharedPreferences.Editor = sharePreference.edit()
        editor.putString(titleKey, title)
        editor.apply()
    }

    fun getRingtoneTitle(titleKey: String) : String? = sharePreference.getString(titleKey, null)
}