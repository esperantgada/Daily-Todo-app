package eg.esperantgada.dailytodo.service

import android.app.Service
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.IBinder
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import eg.esperantgada.dailytodo.fragment.todo.AddEditTodoFragment
import eg.esperantgada.dailytodo.room.TodoDatabase
import java.security.Provider


class TodoRingtoneService : Service() {

    private  var sound : Ringtone? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val ringtoneUri = intent?.getStringExtra("ringtoneUri")
        sound = RingtoneManager.getRingtone(applicationContext, ringtoneUri?.toUri())
        sound?.play()

        return START_STICKY
    }


    override fun onDestroy() {
        super.onDestroy()
        if (sound?.isPlaying  == true){
            sound?.stop()
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}

