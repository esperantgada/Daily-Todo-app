package eg.esperantgada.dailytodo.broadcastreceiver

import android.app.Activity
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import eg.esperantgada.dailytodo.service.TodoRingtoneService
import eg.esperantgada.dailytodo.utils.NOTIFICATION_ID

class CancelNotificationReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {

        val notificationManager = ContextCompat
            .getSystemService(context!!, NotificationManager::class.java) as NotificationManager


        notificationManager.cancel(NOTIFICATION_ID)

    }
}