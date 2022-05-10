@file:Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package eg.esperantgada.dailytodo.alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.google.android.material.timepicker.TimeFormat
import eg.esperantgada.dailytodo.TAG
import eg.esperantgada.dailytodo.broadcastreceiver.TodoAlarmReceiver
import eg.esperantgada.dailytodo.model.Todo
import eg.esperantgada.dailytodo.utils.SET_ACTION
import eg.esperantgada.dailytodo.utils.TODO_ALARM_TAG
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

//Sets alarm for the broadcast receiver
object TodoAlarm {

    @SuppressLint("SimpleDateFormat", "InlinedApi")
    fun setTodoAlarmReminder(context: Context, todo: Todo, dayList: List<String>?) {
        val alarmManager: AlarmManager? =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?

        val timeFormat = SimpleDateFormat("hh:mm a")
        val parsedTime = timeFormat.parse(todo.time)
        val expectedFormat = SimpleDateFormat("hh:mm")
        val newTime = expectedFormat.format(parsedTime!!)
        Log.d(TODO_ALARM_TAG, "NEW FORMATTED TIME IS $newTime")

        val (todoHour, todoMinute) = newTime.split(":").map { it }

        Log.d(TODO_ALARM_TAG, "THE HOUR IS $todoHour and minute is $todoMinute")


        val intent = Intent(context, TodoAlarmReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            todo.id,
            intent.apply {
                action = SET_ACTION
                putExtra("name", todo.name)
                putExtra("date", todo.date)
                putExtra("time", todo.time)
                putExtra("id", todo.id)
                putExtra("ringtoneUri", todo.ringtoneUri)
                putStringArrayListExtra("days", dayList?.let { ArrayList(it) })
                Log.d(TODO_ALARM_TAG, "${todo.id}")
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT
        )

        val dateAndTimeReminder = "${todo.date} ${todo.time}"
        Log.d(TODO_ALARM_TAG, "Date and Time $dateAndTimeReminder")

        val format = SimpleDateFormat("dd/MM/yyyy h:mm a")
        val todoTime = format.parse(dateAndTimeReminder)
        val currentTime = Calendar.getInstance().time

        if (currentTime <= todoTime) {
            alarmManager?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setInexactRepeating(
                        AlarmManager.RTC_WAKEUP,
                        todoTime.time,
                        AlarmManager.INTERVAL_DAY,
                        pendingIntent
                    )
                } else {
                    alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        todoTime.time,
                        AlarmManager.INTERVAL_DAY,
                        pendingIntent
                    )
                }
            }
        } else {
            alarmManager?.cancel(pendingIntent)
        }
    }
}