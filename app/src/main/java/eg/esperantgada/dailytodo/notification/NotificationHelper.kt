package eg.esperantgada.dailytodo.notification


import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import eg.esperantgada.dailytodo.MainActivity
import eg.esperantgada.dailytodo.R
import eg.esperantgada.dailytodo.utils.CHANNEL_ID
import eg.esperantgada.dailytodo.utils.DESCRIPTION_TEXT
import eg.esperantgada.dailytodo.utils.NAME
import eg.esperantgada.dailytodo.utils.NOTIFICATION_ID

class NotificationHelper(private val context: Context) {

    fun onCreateNotification(name : String, todoDateAndTime : String){
        onCreateNotificationChannel()

        //Intent to allow the user to navigate back to the app when receiving a notification
        /*val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)*/

        val pendingIntent = NavDeepLinkBuilder(context)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.todoFragment)
            .createPendingIntent()

        //Creates a default sound for the notification
        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)


        //The notification that will be sent to the user
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Todo name : ${name.uppercase()}")
            .setContentText("Due time : ${todoDateAndTime.uppercase()}")
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)
            .setSound(defaultSound)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAllowSystemGeneratedContextualActions(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
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

            val notificationManager : NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }
}

