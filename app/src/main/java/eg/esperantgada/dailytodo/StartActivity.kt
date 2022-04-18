package eg.esperantgada.dailytodo

import android.content.Intent
import android.content.Intent.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import androidx.core.app.NotificationCompat.getAction
import androidx.core.view.accessibility.AccessibilityEventCompat.getAction

@Suppress("DEPRECATION")
class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 1800)

    }

}