package eg.esperantgada.dailytodo.service

import android.app.Service
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.widget.SearchView
import androidx.core.net.toUri
import eg.esperantgada.dailytodo.viewmodel.TAG
import java.util.*


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

