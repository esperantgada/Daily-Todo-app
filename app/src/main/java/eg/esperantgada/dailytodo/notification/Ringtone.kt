package eg.esperantgada.dailytodo.notification

import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri

object Ringtone {

    fun setNotificationRingtone(context: Context): Intent {
        val currentRingtone: Uri =
            RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_ALARM)

        val ringtoneIntent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
        ringtoneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE)
        ringtoneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select ringtone")
        ringtoneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, currentRingtone)
        ringtoneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
        ringtoneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)

        return ringtoneIntent
    }

}