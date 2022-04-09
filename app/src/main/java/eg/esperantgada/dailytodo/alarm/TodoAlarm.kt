package eg.esperantgada.dailytodo.alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import eg.esperantgada.dailytodo.broadcastreceiver.TodoAlarmReceiver
import eg.esperantgada.dailytodo.model.Todo
import eg.esperantgada.dailytodo.utils.SET_ACTION
import eg.esperantgada.dailytodo.utils.TODO_ALARM_TAG
import java.text.SimpleDateFormat
import java.util.*

//Sets alarm for the broadcast receiver
object TodoAlarm {

    @SuppressLint("SimpleDateFormat", "InlinedApi")
    fun setTodoAlarmReminder(context: Context, todo: Todo){
        val alarmManager : AlarmManager? =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

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
                Log.d(TODO_ALARM_TAG, "${todo.id}")
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT
        )

        val dateAndTimeReminder = "${todo.date} ${todo.time}"
        Log.d(TODO_ALARM_TAG, "Date and Time $dateAndTimeReminder")

        val format = SimpleDateFormat("dd/MM/yyyy h:mm a")
        val todoTime = format.parse(dateAndTimeReminder)
        val currentTime = Calendar.getInstance().time

        if (currentTime < todoTime){
            alarmManager?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        todoTime.time,
                        pendingIntent
                    )
                }else{
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        todoTime.time,
                        pendingIntent
                    )
                }
            }
        }else{
            alarmManager?.cancel(pendingIntent)
        }


    }
}