package eg.esperantgada.dailytodo.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import eg.esperantgada.dailytodo.model.Todo
import eg.esperantgada.dailytodo.service.TodoCountDownTimerService
import eg.esperantgada.dailytodo.service.TodoRingtoneService
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

const val TAG = "TodoTimer"

class TodoTimer(private val context: Context, private val todo: Todo) {

    @SuppressLint("SimpleDateFormat")

    var dayHourMinuteSecond: String? = null

    val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a")
    var todoCreatedDateAndTime = dateTimeFormat.parse(todo.createdAt)

    var todoDueDateAndTime = dateTimeFormat.parse("${todo.date} ${todo.time}")


    val createdDateAndTime = todoCreatedDateAndTime?.time
    val dueDateAndTime = todoDueDateAndTime?.time
    val countTimeLength = dueDateAndTime?.minus(createdDateAndTime!!)

    val startCountDownTimerServiceIntent = Intent(context, TodoCountDownTimerService::class.java)



    val countDownTimer = object : CountDownTimer(countTimeLength!!, 1000) {
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

            startCountDownTimerServiceIntent.putExtra("timeLength", countTimeLength)
            context.startService(startCountDownTimerServiceIntent)
            Log.d(TAG, "COUNTDOWNTIMER : $dayHourMinuteSecond")

        }


        override fun onFinish() {
            dayHourMinuteSecond = "00:00:00:00"
        }
    }
}
