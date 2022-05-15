package eg.esperantgada.dailytodo.sharepreference

import android.content.Context
import android.content.SharedPreferences

const val PREFERENCE_NAME = "Todo_Preference"

class TodoSharePreference (context: Context) {

    private val sharePreference : SharedPreferences =
        context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)


    fun saveSwitch(switchKey : String, switchState : Boolean){
        val editor : SharedPreferences.Editor = sharePreference.edit()
        editor.putBoolean(switchKey, switchState)
        editor.apply()
    }

    fun getSwitchState(switchKey : String) : Boolean{
        return sharePreference.getBoolean(switchKey, false)
    }
}