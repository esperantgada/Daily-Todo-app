package eg.esperantgada.dailytodo.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import eg.esperantgada.dailytodo.service.TodoRingtoneService

class StopRingtoneReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context?, p1: Intent?) {

        val serviceIntent = Intent(context, TodoRingtoneService::class.java)

        context?.stopService(serviceIntent)
    }
}