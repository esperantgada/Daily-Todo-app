package eg.esperantgada.dailytodo

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home){
            super.onBackPressed()
            startActivity(Intent(this, MainActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val feedbackPreference : Preference? = findPreference("feedback")

            feedbackPreference?.setOnPreferenceClickListener {
                sendFeedback(requireContext())
                true
            }

        }

        private fun sendFeedback(context: Context) {
            var body: String? = context.packageManager.getPackageInfo(context.packageName, 0).versionName
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
}