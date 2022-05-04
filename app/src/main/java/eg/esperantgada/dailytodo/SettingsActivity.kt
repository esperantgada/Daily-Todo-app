package eg.esperantgada.dailytodo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager

const val TAG = "SettingsActivity"

class SettingsActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener,
Preference.SummaryProvider<ListPreference>{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.preference_content, SettingsFragment())
                .commit()
        }

        supportActionBar?.setTitle(R.string.settings)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        PreferenceManager.getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        val appThemeString = getString(R.string.theme)
        key?.let {
            if (it == appThemeString) sharedPreferences?.let { preference ->
                val darkModeValues = resources.getStringArray(R.array.theme_entry_values)
                when (preference.getString(appThemeString, darkModeValues[0])) {
                    darkModeValues[0] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    darkModeValues[1] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    darkModeValues[2] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    darkModeValues[3] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
                }
            }
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home){
            super.onBackPressed()
            startActivity(Intent(this, MainActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(this)
            .unregisterOnSharedPreferenceChangeListener(this)
    }


    class SettingsFragment : PreferenceFragmentCompat(){

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.main_preferences, rootKey)

            val feedbackPreference : Preference? = findPreference("feedback")


            feedbackPreference?.setOnPreferenceClickListener {
                sendFeedback(requireContext())
                true
            }

        }


        private fun sendFeedback(context: Context) {
            var body: String? = context
                .packageManager.getPackageInfo(context.packageName, 0).versionName
            body = """

-----------------------------
Please don't remove this information
 Device OS: Android 
 Device OS version: ${Build.VERSION.RELEASE}
 App Version: $body
 Device Brand: ${Build.BRAND}
 Device Model: ${Build.MODEL}
 Device Manufacturer: ${Build.MANUFACTURER}"""
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "message/rfc822"
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("esperantgada@gmail.com"))
            intent.putExtra(Intent.EXTRA_SUBJECT, "Query from android app user")
            intent.putExtra(Intent.EXTRA_TEXT, body)
            context.startActivity(Intent.createChooser(intent, "Choose email client"))
        }

    }

    override fun provideSummary(preference: ListPreference): CharSequence? =
        if (preference.key == getString(R.string.theme)) preference.entry
        else "Unknown preference"

}