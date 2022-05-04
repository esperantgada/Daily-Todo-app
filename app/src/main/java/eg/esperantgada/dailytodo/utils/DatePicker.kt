package eg.esperantgada.dailytodo.utils

import android.app.DatePickerDialog
import android.content.Context
import android.widget.EditText
import com.google.android.material.textfield.TextInputEditText
import eg.esperantgada.dailytodo.model.Todo
import java.text.SimpleDateFormat
import java.util.*

fun EditText.getDatePicker(
    context: Context,
    format : String, maxDate: Date? = null
){
    isFocusableInTouchMode = false
    isFocusable = false
    isClickable = true

    val calendar = Calendar.getInstance()

    val datePickerListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)

        val selectedDate = SimpleDateFormat(format, Locale.UK)
        setText(selectedDate.format(calendar.time))
    }

    setOnClickListener {
        DatePickerDialog(
            context,
            datePickerListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).run {
            maxDate?.time?.also {
                datePicker.minDate = it
                show()
            }
        }
    }
}