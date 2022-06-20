package eg.esperantgada.dailytodo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import eg.esperantgada.dailytodo.databinding.ActivitySecondStartBinding

class SecondStartActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySecondStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySecondStartBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val thread: Thread = object : Thread() {
            override fun run() {
                try {
                    sleep(2970)
                } catch (exception: Exception) {
                    exception.printStackTrace()
                } finally {
                    val intent = Intent(this@SecondStartActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        };thread.start ()

    }

   /* override fun onStop() {
        super.onStop()

        startTodoAlarmService()
    }
    private fun startTodoAlarmService(){
        val serviceIntent = Intent(this, TodoAlarmService::class.java)
        applicationContext.startService(serviceIntent)

    }*/
}