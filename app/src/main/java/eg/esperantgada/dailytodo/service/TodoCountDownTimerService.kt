package eg.esperantgada.dailytodo.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import eg.esperantgada.dailytodo.fragment.todo.TAG3
import eg.esperantgada.dailytodo.fragment.todo.TodoFragment
import java.util.concurrent.TimeUnit

const val TAG2 = "TodoCountDownTimerService"
class TodoCountDownTimerService : Service() {

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val timeLength  = intent?.getLongExtra("timeLength", 0)
        val countDownTimer = object : CountDownTimer(timeLength!!, 1000) {
          private lateinit var dayHourMinuteSecond : String
            @SuppressLint("LongLogTag")
            override fun onTick(p0: Long) {

                val millisecond: Long = p0
                 dayHourMinuteSecond = String.format(
                    "%02d Days :%02d Hours :%02d Min :%02d Sec",
                    TimeUnit.MILLISECONDS.toDays(millisecond),
                    (TimeUnit.MILLISECONDS.toHours(millisecond) - TimeUnit.DAYS.toHours(
                        TimeUnit.MILLISECONDS.toDays(millisecond))),

                    (TimeUnit.MILLISECONDS.toMinutes(millisecond) - TimeUnit.HOURS.toMinutes(
                        TimeUnit.MILLISECONDS.toHours(millisecond))),

                    (TimeUnit.MILLISECONDS.toSeconds(millisecond) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(millisecond)))
                )

                val timerIntent = Intent()
                timerIntent.action = "com.my.app"
                timerIntent.putExtra("timer", dayHourMinuteSecond)
                sendBroadcast(timerIntent)

                Log.d(TAG2, "SERVICE STARTED")
                Log.d(TAG2, "COUNTDOWNTIMER  IN SERVICE: $dayHourMinuteSecond")

            }


            override fun onFinish() {
                dayHourMinuteSecond = "00:00:00:00"
            }
        }
        countDownTimer.start()


        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d(TAG3, "SERVICE DESTROYED")
    }
}