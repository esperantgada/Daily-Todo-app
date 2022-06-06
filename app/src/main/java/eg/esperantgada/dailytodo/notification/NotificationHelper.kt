package eg.esperantgada.dailytodo.notification


import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import eg.esperantgada.dailytodo.MainActivity
import eg.esperantgada.dailytodo.R
import eg.esperantgada.dailytodo.broadcastreceiver.CancelNotificationReceiver
import eg.esperantgada.dailytodo.broadcastreceiver.StopRingtoneReceiver
import eg.esperantgada.dailytodo.utils.*

@Suppress("DEPRECATION")
class NotificationHelper(private val context: Context) {

    @SuppressLint("InlinedApi")
    fun onCreateNotification(name: String, todoDateAndTime: String){
        onCreateNotificationChannel()

        //Intent to allow the user to navigate back to the app when receiving a notification
       /* val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
*/
        val pendingIntent = NavDeepLinkBuilder(context)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.todoFragment)
            .createPendingIntent()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE
        }

        /*val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )*/


        //Cancel notification pending intent
        val cancelIntent = Intent(context, CancelNotificationReceiver::class.java)
        val cancelPendingIntent : PendingIntent =
            PendingIntent.getBroadcast(context, 0, cancelIntent, PendingIntent.FLAG_IMMUTABLE)


        //Stop notification ringtone pending intent
        val stopIntent = Intent(context, StopRingtoneReceiver::class.java)
        val stopRingtonePendingIntent : PendingIntent =
            PendingIntent.getBroadcast(context, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE)



        //The notification that will be sent to the user
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Todo name : ${name.uppercase()}")
            .setContentText("Due time : ${todoDateAndTime.uppercase()}")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("You have to start ${name.uppercase()} now. You scheduled " +
                        "it for $todoDateAndTime. Please, don't miss it. Take it in account"))
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(
                R.drawable.todo_icon,
                context.getString(R.string.cancel_notification),
                cancelPendingIntent
            )
            .addAction(
                R.drawable.todo_icon,
                context.getString(R.string.stop_ringtone),
                stopRingtonePendingIntent
            )
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }


    @SuppressLint("NewApi")
    private fun onCreateNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, NAME, importance).apply {
                description = DESCRIPTION_TEXT
            }

            channel.enableLights(true)
            channel.lightColor = Color.parseColor(ColorPicker.getColors())

            val notificationManager : NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }

    }
}

