package eg.esperantgada.dailytodo.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import eg.esperantgada.dailytodo.notification.NotificationHelper
import eg.esperantgada.dailytodo.utils.SET_ACTION

class TodoAlarmReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context, intent: Intent) {

        val name = intent.getStringExtra("name")
        val date = intent.getStringExtra("date")
        val time = intent.getStringExtra("time")

        val todoDateAndTime = "$date $time"
        val id = intent.getIntExtra("id", 0)

        if (SET_ACTION == intent.action){
            if (id >= 0){
                val notificationHelper = NotificationHelper(context)
                notificationHelper.onCreateNotification(name = name!!, todoDateAndTime)
            }
        }
    }


}