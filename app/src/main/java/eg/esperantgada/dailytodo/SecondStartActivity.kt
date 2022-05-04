package eg.esperantgada.dailytodo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
}