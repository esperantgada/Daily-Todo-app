package eg.esperantgada.dailytodo.utils

import android.annotation.SuppressLint
import android.os.CountDownTimer
import android.util.Log
import eg.esperantgada.dailytodo.model.Todo
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

const val TAG = "TodoTimer"

class TodoTimer(private val todo: Todo) {

    @SuppressLint("SimpleDateFormat")

    var dayHourMinuteSecond: String? = null

    val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a")
    var todoCreatedDateAndTime = dateTimeFormat.parse(todo.createdAt)

    var todoDueDateAndTime = dateTimeFormat.parse("${todo.date} ${todo.time}")


    val createdDateAndTime = todoCreatedDateAndTime?.time
    val dueDateAndTime = todoDueDateAndTime?.time
    val countTimeLength = dueDateAndTime?.minus(createdDateAndTime!!)


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

            fun setResult(): String? = dayHourMinuteSecond

            Log.d(TAG, "COUNTDOWNTIMER : $dayHourMinuteSecond")


        }


        override fun onFinish() {
            dayHourMinuteSecond = "00:00:00:00"
        }

    }
    //Log.d(TAG, "COUNTDOWNTIMER IN METHOD: $dayHourMinuteSecond")
}
