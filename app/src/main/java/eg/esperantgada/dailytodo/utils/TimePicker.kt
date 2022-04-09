package eg.esperantgada.dailytodo.utils

import android.app.TimePickerDialog
import android.content.Context
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

fun TextInputEditText.getTimePicker(context: Context, format : String){

    isFocusableInTouchMode = false
    isFocusable = false
    isClickable = true

    val calendar = Calendar.getInstance()

    val timePickListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)

        val selectTime = SimpleDateFormat(format, Locale.ENGLISH)
        setText(selectTime.format(calendar.time))
    }

    setOnClickListener {
        TimePickerDialog(
            context, timePickListener,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE), false
        ).show()
    }
}