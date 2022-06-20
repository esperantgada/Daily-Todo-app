package eg.esperantgada.dailytodo

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

@Suppress("DEPRECATION")
class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        Handler().postDelayed({
            startActivity(Intent(this, SecondStartActivity::class.java))
            finish()
        }, 1800)

    }

   /* override fun onStop() {
        super.onStop()

        startTodoAlarmService()
    }

    private fun startTodoAlarmService(){
        val serviceIntent = Intent(this, TodoAlarmService::class.java)
        applicationContext.startService(serviceIntent)

    }
*/
}