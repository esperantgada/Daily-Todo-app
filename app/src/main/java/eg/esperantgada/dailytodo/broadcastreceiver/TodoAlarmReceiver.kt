package eg.esperantgada.dailytodo.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.net.toUri
import eg.esperantgada.dailytodo.notification.NotificationHelper
import eg.esperantgada.dailytodo.service.TodoRingtoneService
import eg.esperantgada.dailytodo.sharepreference.TodoSharePreference
import eg.esperantgada.dailytodo.utils.SET_ACTION
import java.util.*

@Suppress("DEPRECATION")
class TodoAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val mondayTuesday: List<String> = listOf("Monday", "Tuesday")
        val mondayWednesday: List<String> = listOf("Monday", "Wednesday")
        val mondayThursday: List<String> = listOf("Monday", "Thursday")
        val mondayFriday: List<String> = listOf("Monday", "Friday")
        val mondaySaturday: List<String> = listOf("Monday", "Saturday")
        val mondaySunday: List<String> = listOf("Monday", "Sunday")
        val tuesdayWednesday: List<String> = listOf("Tuesday", "Wednesday")
        val tuesdayThursday: List<String> = listOf("Tuesday", "Thursday")
        val tuesdayFriday: List<String> = listOf("Tuesday", "Friday")
        val tuesdaySaturday: List<String> = listOf("Tuesday", "Saturday")
        val tuesdaySunday: List<String> = listOf("Tuesday", "Sunday")
        val wednesdayThursday: List<String> = listOf("Wednesday", "Thursday")
        val wednesdayFriday: List<String> = listOf("Wednesday", "Friday")
        val wednesdaySaturday: List<String> = listOf("Wednesday", "Saturday")
        val wednesdaySunday: List<String> = listOf("Wednesday", "Sunday")
        val thursdayFriday: List<String> = listOf("Thursday", "Friday")
        val thursdaySaturday: List<String> = listOf("Thursday", "Saturday")
        val thursdaySunday: List<String> = listOf("Thursday", "Sunday")
        val fridaySaturday: List<String> = listOf("Friday", "Saturday")
        val fridaySunday: List<String> = listOf("Friday", "Sunday")
        val saturdaySunday: List<String> = listOf("Saturday", "Sunday")

        val mondayTuesdayWednesday: List<String> = listOf("Monday", "Tuesday", "Wednesday")
        val mondayTuesdayThursday: List<String> = listOf("Monday", "Tuesday", "Thursday")
        val mondayTuesdayFriday: List<String> = listOf("Monday", "Tuesday", "Friday")
        val mondayTuesdaySaturday: List<String> = listOf("Monday", "Tuesday", "Saturday")
        val mondayTuesdaySunday: List<String> = listOf("Monday", "Tuesday", "Sunday")
        val tuesdayWednesdayThursday: List<String> = listOf("Tuesday", "Wednesday", "Thursday")
        val tuesdayWednesdayFriday: List<String> = listOf("Tuesday", "Wednesday", "Friday")
        val tuesdayWednesdaySaturday: List<String> = listOf("Tuesday", "Wednesday", "Saturday")
        val tuesdayWednesdaySunday: List<String> = listOf("Tuesday", "Wednesday", "Sunday")
        val wednesdayThursdayFriday: List<String> = listOf("Wednesday", "Thursday", "Friday")
        val wednesdayThursdaySaturday: List<String> = listOf("Wednesday", "Thursday", "Saturday")
        val wednesdayThursdaySunday: List<String> = listOf("Wednesday", "Thursday", "Sunday")
        val thursdayFridaySaturday: List<String> = listOf("Thursday", "Friday", "Saturday")
        val thursdayFridaySunday: List<String> = listOf("Thursday", "Friday", "Sunday")
        val fridaySaturdaySunday: List<String> = listOf("Friday", "Saturday", "Sunday")


        val mondayTuesdayWednesdayThursday: List<String> =
            listOf("Monday", "Tuesday", "Wednesday", "Thursday")
        val mondayTuesdayWednesdayFriday: List<String> =
            listOf("Monday", "Tuesday", "Wednesday", "Friday")
        val mondayTuesdayWednesdaySaturday: List<String> =
            listOf("Monday", "Tuesday", "Wednesday", "Saturday")
        val mondayTuesdayWednesdaySunday: List<String> =
            listOf("Monday", "Tuesday", "Wednesday", "Sunday")
        val tuesdayWednesdayThursdayFriday: List<String> =
            listOf("Tuesday", "Wednesday", "Thursday", "Friday")
        val tuesdayWednesdayThursdaySaturday: List<String> =
            listOf("Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
        val tuesdayWednesdayThursdaySunday: List<String> =
            listOf("Tuesday", "Wednesday", "Thursday", "Friday", "Sunday")
        val wednesdayThursdayFridaySaturday: List<String> =
            listOf("Wednesday", "Thursday", "Friday", "Saturday")
        val wednesdayThursdayFridaySunday: List<String> =
            listOf("Wednesday", "Thursday", "Friday", "Sunday")
        val thursdayFridaySaturdaySunday: List<String> =
            listOf("Thursday", "Friday", "Saturday", "Sunday")


        val mondayTuesdayWednesdayThursdayFriday: List<String> =
            listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday")
        val mondayTuesdayWednesdayThursdaySaturday: List<String> =
            listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Saturday")
        val mondayTuesdayWednesdayThursdaySunday: List<String> =
            listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Sunday")
        val tuesdayWednesdayThursdayFridaySaturday: List<String> =
            listOf("Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
        val tuesdayWednesdayThursdayFridaySunday: List<String> =
            listOf("Tuesday", "Wednesday", "Thursday", "Friday", "Sunday")
        val wednesdayThursdayFridaySaturdaySunday: List<String> =
            listOf("Wednesday", "Thursday", "Friday", "Saturday")


        val mondayTuesdayWednesdayThursdayFridaySaturday: List<String> =
            listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")

        val mondayTuesdayWednesdayThursdayFridaySunday: List<String> =
            listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Sunday")

        val tuesdayWednesdayThursdayFridaySaturdaySunday: List<String> =
            listOf("Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

        val todoSharePreference = TodoSharePreference(context)



        val name = intent.getStringExtra("name")
        val date = intent.getStringExtra("date")
        val time = intent.getStringExtra("time")
        val uri = intent.getStringExtra("ringtoneUri")
        val dayList = intent.getStringArrayListExtra("days")

        val todoDateAndTime = "$date $time"
        val id = intent.getIntExtra("id", 0)
        val notificationHelper = NotificationHelper(context)
        val sound = RingtoneManager.getRingtone(context, uri?.toUri())

        val startServiceIntent = Intent(context, TodoRingtoneService::class.java)
        startServiceIntent.putExtra("ringtoneUri", uri)

        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_WEEK)


        if (SET_ACTION == intent.action) {
            if (id >= 0) {
                if (dayList != null) {
                    when (dayList.size) {

                        0 -> {
                            context.startService(startServiceIntent)
                            notificationHelper.onCreateNotification(name = name!!, todoDateAndTime)
                        }

                        1 -> {
                            if (dayList[0] == "Daily") {
                                if (day == Calendar.MONDAY || day == Calendar.TUESDAY ||
                                    day == Calendar.WEDNESDAY || day == Calendar.THURSDAY || day == Calendar.FRIDAY ||
                                    day == Calendar.SATURDAY || day == Calendar.SUNDAY
                                ) {
                                    context.startService(startServiceIntent)
                                    notificationHelper.onCreateNotification(name = name!!,
                                        todoDateAndTime)
                                }
                            } else {
                                if (dayList[0] == "Monday") {
                                    if (day == Calendar.MONDAY) {
                                        context.startService(startServiceIntent)
                                        notificationHelper.onCreateNotification(name = name!!,
                                            todoDateAndTime)
                                    }
                                }

                                if (dayList[0] == "Tuesday") {
                                    if (day == Calendar.TUESDAY) {
                                        context.startService(startServiceIntent)
                                        notificationHelper.onCreateNotification(name = name!!,
                                            todoDateAndTime)
                                    }
                                }

                                if (dayList[0] == "Wednesday") {
                                    if (day == Calendar.WEDNESDAY) {
                                        context.startService(startServiceIntent)
                                        notificationHelper.onCreateNotification(name = name!!,
                                            todoDateAndTime)
                                    }
                                }

                                if (dayList[0] == "Thursday") {
                                    if (day == Calendar.THURSDAY) {
                                        context.startService(startServiceIntent)
                                        notificationHelper.onCreateNotification(name = name!!,
                                            todoDateAndTime)
                                    }
                                }

                                if (dayList[0] == "Friday") {
                                    if (day == Calendar.FRIDAY) {
                                        context.startService(startServiceIntent)
                                        notificationHelper.onCreateNotification(name = name!!,
                                            todoDateAndTime)
                                    }
                                }

                                if (dayList[0] == "Saturday") {
                                    if (day == Calendar.SATURDAY) {
                                        context.startService(startServiceIntent)
                                        notificationHelper.onCreateNotification(name = name!!,
                                            todoDateAndTime)
                                    }
                                }

                                if (dayList[0] == "Sunday") {
                                    if (day == Calendar.SUNDAY) {
                                        context.startService(startServiceIntent)
                                        notificationHelper.onCreateNotification(name = name!!,
                                            todoDateAndTime)
                                    }
                                }
                            }
                        }


                        2 -> {
                            if (mondayTuesday.containsAll(dayList)) {
                                if (day == Calendar.MONDAY || day == Calendar.TUESDAY) {
                                    context.startService(startServiceIntent)
                                    notificationHelper.onCreateNotification(name = name!!,
                                        todoDateAndTime)
                                }
                            }


                            if (mondayWednesday.containsAll(dayList)) {
                                if (day == Calendar.MONDAY || day == Calendar.WEDNESDAY) {
                                    context.startService(startServiceIntent)
                                    notificationHelper.onCreateNotification(name = name!!,
                                        todoDateAndTime)
                                }
                            }

                            if (mondayThursday.containsAll(dayList)) {
                                if (day == Calendar.MONDAY || day == Calendar.THURSDAY) {
                                    context.startService(startServiceIntent)
                                    notificationHelper.onCreateNotification(name = name!!,
                                        todoDateAndTime)
                                }
                            }

                            if (mondayFriday.containsAll(dayList)) {
                                if (day == Calendar.MONDAY || day == Calendar.FRIDAY) {
                                    context.startService(startServiceIntent)
                                    notificationHelper.onCreateNotification(name = name!!,
                                        todoDateAndTime)
                                }
                            }

                            if (mondaySaturday.containsAll(dayList)) {
                                if (day == Calendar.MONDAY || day == Calendar.SATURDAY) {
                                    context.startService(startServiceIntent)
                                    notificationHelper.onCreateNotification(name = name!!,
                                        todoDateAndTime)
                                }
                            }

                            if (mondaySunday.containsAll(dayList)) {
                                if (day == Calendar.MONDAY || day == Calendar.SUNDAY) {
                                    context.startService(startServiceIntent)
                                    notificationHelper.onCreateNotification(name = name!!,
                                        todoDateAndTime)
                                }
                            }

                            if (tuesdayWednesday.containsAll(dayList)) {
                                if (day == Calendar.TUESDAY || day == Calendar.WEDNESDAY) {
                                    context.startService(startServiceIntent)
                                    notificationHelper.onCreateNotification(name = name!!,
                                        todoDateAndTime)
                                }
                            }

                            if (tuesdayThursday.containsAll(dayList)) {
                                if (day == Calendar.TUESDAY || day == Calendar.THURSDAY) {
                                    context.startService(startServiceIntent)
                                    notificationHelper.onCreateNotification(name = name!!,
                                        todoDateAndTime)
                                }
                            }

                            if (tuesdayFriday.containsAll(dayList)) {
                                if (day == Calendar.TUESDAY || day == Calendar.FRIDAY) {
                                    context.startService(startServiceIntent)
                                    notificationHelper.onCreateNotification(name = name!!,
                                        todoDateAndTime)
                                }
                            }

                            if (tuesdaySaturday.containsAll(dayList)) {
                                if (day == Calendar.TUESDAY || day == Calendar.SATURDAY) {
                                    context.startService(startServiceIntent)
                                    notificationHelper.onCreateNotification(name = name!!,
                                        todoDateAndTime)
                                }
                            }

                            if (tuesdaySunday.containsAll(dayList)) {
                                if (day == Calendar.TUESDAY || day == Calendar.SUNDAY) {
                                    context.startService(startServiceIntent)
                                    notificationHelper.onCreateNotification(name = name!!,
                                        todoDateAndTime)
                                }
                            }

                            if (wednesdayThursday.containsAll(dayList)) {
                                if (day == Calendar.WEDNESDAY || day == Calendar.THURSDAY) {
                                    context.startService(startServiceIntent)
                                    notificationHelper.onCreateNotification(name = name!!,
                                        todoDateAndTime)
                                }
                            }

                            if (wednesdayFriday.containsAll(dayList)) {
                                if (day == Calendar.WEDNESDAY || day == Calendar.FRIDAY) {
                                    context.startService(startServiceIntent)
                                    notificationHelper.onCreateNotification(name = name!!,
                                        todoDateAndTime)
                                }
                            }

                            if (wednesdaySaturday.containsAll(dayList)) {
                                if (day == Calendar.WEDNESDAY || day == Calendar.SATURDAY) {
                                    context.startService(startServiceIntent)
                                    notificationHelper.onCreateNotification(name = name!!,
                                        todoDateAndTime)
                                }
                            }

                            if (wednesdaySunday.containsAll(dayList)) {
                                if (day == Calendar.WEDNESDAY || day == Calendar.SUNDAY) {
                                    context.startService(startServiceIntent)
                                    notificationHelper.onCreateNotification(name = name!!,
                                        todoDateAndTime)
                                }
                            }

                            if (thursdayFriday.containsAll(dayList)) {
                                if (day == Calendar.THURSDAY || day == Calendar.FRIDAY) {
                                    context.startService(startServiceIntent)
                                    notificationHelper.onCreateNotification(name = name!!,
                                        todoDateAndTime)
                                }
                            }

                            if (thursdaySaturday.containsAll(dayList)) {
                                if (day == Calendar.THURSDAY || day == Calendar.SATURDAY) {
                                    context.startService(startServiceIntent)
                                    notificationHelper.onCreateNotification(name = name!!,
                                        todoDateAndTime)
                                }
                            }

                            if (thursdaySunday.containsAll(dayList)) {
                                if (day == Calendar.THURSDAY || day == Calendar.SUNDAY) {
                                    sound.play()
                                    notificationHelper.onCreateNotification(name = name!!,
                                        todoDateAndTime)
                                }
                            }

                            if (fridaySaturday.containsAll(dayList)) {
                                if (day == Calendar.FRIDAY || day == Calendar.SATURDAY) {
                                    context.startService(startServiceIntent)
                                    notificationHelper.onCreateNotification(name = name!!,
                                        todoDateAndTime)
                                }
                            }

                            if (fridaySunday.containsAll(dayList)) {
                                if (day == Calendar.FRIDAY || day == Calendar.SUNDAY) {
                                    context.startService(startServiceIntent)
                                    notificationHelper.onCreateNotification(name = name!!,
                                        todoDateAndTime)
                                }
                            }

                            if (saturdaySunday.containsAll(dayList)) {
                                if (day == Calendar.SATURDAY || day == Calendar.SUNDAY) {
                                    context.startService(startServiceIntent)
                                    notificationHelper.onCreateNotification(name = name!!,
                                        todoDateAndTime)
                                }
                            }
                        }


                        3 -> {
                            if (mondayTuesdayWednesday.containsAll(dayList)) {
                                if (day == Calendar.MONDAY || day == Calendar.TUESDAY ||
                                    day == Calendar.WEDNESDAY
                                ) {
                                    context.startService(startServiceIntent)
                                    notificationHelper.onCreateNotification(name = name!!,
                                        todoDateAndTime)
                                }
                            }

                            if (mondayTuesdayThursday.containsAll(dayList)) {
                                if (day == Calendar.MONDAY || day == Calendar.TUESDAY ||
                                    day == Calendar.THURSDAY
                                ) {
                                    context.startService(startServiceIntent)
                                    notificationHelper.onCreateNotification(name = name!!,
                                        todoDateAndTime)
                                }
                            }

                            if (mondayTuesdayFriday.containsAll(dayList)) {
                                if (day == Calendar.MONDAY || day == Calendar.TUESDAY ||
                                    day == Calendar.FRIDAY
                                ) {
                                    context.startService(startServiceIntent)
                                    notificationHelper.onCreateNotification(name = name!!,
                                        todoDateAndTime)
                                }
                            }

                            if (mondayTuesdaySaturday.containsAll(dayList)) {
                                if (day == Calendar.MONDAY || day == Calendar.TUESDAY ||
                                    day == Calendar.SATURDAY
                                ) {
                                    context.startService(startServiceIntent)
                                    notificationHelper.onCreateNotification(name = name!!,
                                        todoDateAndTime)
                                }
                            }

                            if (mondayTuesdaySunday.containsAll(dayList)) {
                                if (day == Calendar.MONDAY || day == Calendar.TUESDAY ||
                                    day == Calendar.SUNDAY
                                ) {
                                    context.startService(startServiceIntent)
                                    notificationHelper.onCreateNotification(name = name!!,
                                        todoDateAndTime)
                                }
                            }

                            if (tuesdayWednesdayThursday.containsAll(dayList)) {
                                if (day == Calendar.TUESDAY || day == Calendar.WEDNESDAY ||
                                    day == Calendar.THURSDAY
                                ) {
                                    context.startService(startServiceIntent)
                                    notificationHelper.onCreateNotification(name = name!!,
                                        todoDateAndTime)
                                }
                            }

                            if (tuesdayWednesdayFriday.containsAll(dayList)) {
                                if (day == Calendar.TUESDAY || day == Calendar.WEDNESDAY ||
                                    day == Calendar.FRIDAY
                                ) {
                                    context.startService(startServiceIntent)
                                    notificationHelper.onCreateNotification(name = name!!,
                                        todoDateAndTime)
                                }
                            }

                            if (tuesdayWednesdaySaturday.containsAll(dayList)) {
                                if (day == Calendar.TUESDAY || day == Calendar.WEDNESDAY ||
                                    day == Calendar.SATURDAY
                                ) {
                                    context.startService(startServiceIntent)
                                    notificationHelper.onCreateNotification(name = name!!,
                                        todoDateAndTime)
                                }
                            }

                            if (tuesdayWednesdaySunday.containsAll(dayList)) {
                                if (day == Calendar.TUESDAY || day == Calendar.WEDNESDAY ||
                                    day == Calendar.SUNDAY
                                ) {
                                    context.startService(startServiceIntent)
                                    notificationHelper.onCreateNotification(name = name!!,
                                        todoDateAndTime)
                                }
                            }

                            if (wednesdayThursdayFriday.containsAll(dayList)) {
                                if (day == Calendar.WEDNESDAY || day == Calendar.THURSDAY ||
                                    day == Calendar.FRIDAY
                                ) {
                                    context.startService(startServiceIntent)
                                    notificationHelper.onCreateNotification(name = name!!,
                                        todoDateAndTime)
                                }
                            }

                            if (wednesdayThursdaySaturday.containsAll(dayList)) {
                                if (day == Calendar.WEDNESDAY || day == Calendar.THURSDAY ||
                                    day == Calendar.SATURDAY
                                ) {
                                    context.startService(startServiceIntent)
                                    notificationHelper.onCreateNotification(name = name!!,
                                        todoDateAndTime)
                                }
                            }

                            if (wednesdayThursdaySunday.containsAll(dayList)) {
                                if (day == Calendar.WEDNESDAY || day == Calendar.THURSDAY ||
                                    day == Calendar.SUNDAY
                                ) {
                                    context.startService(startServiceIntent)
                                    notificationHelper.onCreateNotification(name = name!!,
                                        todoDateAndTime)
                                }
                            }

                            if (thursdayFridaySaturday.containsAll(dayList)) {
                                if (day == Calendar.THURSDAY || day == Calendar.FRIDAY ||
                                    day == Calendar.SATURDAY
                                ) {
                                    context.startService(startServiceIntent)
                                    notificationHelper.onCreateNotification(name = name!!,
                                        todoDateAndTime)
                                }
                            }

                            if (thursdayFridaySunday.containsAll(dayList)) {
                                if (day == Calendar.THURSDAY || day == Calendar.FRIDAY ||
                                    day == Calendar.SUNDAY
                                ) {
                                    context.startService(startServiceIntent)
                                    notificationHelper.onCreateNotification(name = name!!,
                                        todoDateAndTime)
                                }
                            }

                            if (fridaySaturdaySunday.containsAll(dayList)) {
                                if (day == Calendar.FRIDAY || day == Calendar.SATURDAY ||
                                    day == Calendar.SUNDAY
                                ) {
                                    context.startService(startServiceIntent)
                                    notificationHelper.onCreateNotification(name = name!!,
                                        todoDateAndTime)
                                }
                            }
                        }


                        4 -> {
                            if (mondayTuesdayWednesdayThursday.containsAll(dayList)) {
                                if (day == Calendar.MONDAY || day == Calendar.TUESDAY ||
                                    day == Calendar.WEDNESDAY || day == Calendar.THURSDAY
                                ) {
                                    context.startService(startServiceIntent)
                                    notificationHelper.onCreateNotification(name = name!!,
                                        todoDateAndTime)
                                }
                            }

                            if (mondayTuesdayWednesdayFriday.containsAll(dayList)) {
                                if (day == Calendar.MONDAY || day == Calendar.TUESDAY ||
                                    day == Calendar.WEDNESDAY || day == Calendar.FRIDAY
                                ) {
                                    context.startService(startServiceIntent)
                                    notificationHelper.onCreateNotification(name = name!!,
                                        todoDateAndTime)
                                }
                            }

                            if (mondayTuesdayWednesdaySaturday.containsAll(dayList)) {
                                if (day == Calendar.MONDAY || day == Calendar.TUESDAY ||
                                    day == Calendar.WEDNESDAY || day == Calendar.SATURDAY
                                ) {
                                    context.startService(startServiceIntent)
                                    notificationHelper.onCreateNotification(name = name!!,
                                        todoDateAndTime)
                                }
                            }

                            if (mondayTuesdayWednesdaySunday.containsAll(dayList)) {
                                if (day == Calendar.MONDAY || day == Calendar.TUESDAY ||
                                    day == Calendar.WEDNESDAY || day == Calendar.SUNDAY
                                ) {
                                    context.startService(startServiceIntent)
                                    notificationHelper.onCreateNotification(name = name!!,
                                        todoDateAndTime)
                                }
                            }

                            if (tuesdayWednesdayThursdayFriday.containsAll(dayList)) {
                                if (day == Calendar.TUESDAY || day == Calendar.WEDNESDAY ||
                                    day == Calendar.THURSDAY || day == Calendar.FRIDAY
                                ) {
                                    context.startService(startServiceIntent)
                                    notificationHelper.onCreateNotification(name = name!!,
                                        todoDateAndTime)
                                }
                            }

                            if (tuesdayWednesdayThursdaySaturday.containsAll(dayList)) {
                                if (day == Calendar.TUESDAY || day == Calendar.WEDNESDAY ||
                                    day == Calendar.THURSDAY || day == Calendar.SATURDAY
                                ) {
                                    context.startService(startServiceIntent)
                                    notificationHelper.onCreateNotification(name = name!!,
                                        todoDateAndTime)
                                }
                            }

                            if (tuesdayWednesdayThursdaySunday.containsAll(dayList)) {
                                if (day == Calendar.TUESDAY || day == Calendar.WEDNESDAY ||
                                    day == Calendar.THURSDAY || day == Calendar.SUNDAY
                                ) {
                                    context.startService(startServiceIntent)
                                    notificationHelper.onCreateNotification(name = name!!,
                                        todoDateAndTime)
                                }
                            }

                            if (wednesdayThursdayFridaySaturday.containsAll(dayList)) {
                                if (day == Calendar.WEDNESDAY || day == Calendar.THURSDAY ||
                                    day == Calendar.FRIDAY || day == Calendar.SATURDAY
                                ) {
                                    context.startService(startServiceIntent)
                                    notificationHelper.onCreateNotification(name = name!!,
                                        todoDateAndTime)
                                }
                            }

                            if (wednesdayThursdayFridaySunday.containsAll(dayList)) {
                                if (day == Calendar.WEDNESDAY || day == Calendar.THURSDAY ||
                                    day == Calendar.FRIDAY || day == Calendar.SUNDAY
                                ) {
                                    context.startService(startServiceIntent)
                                    notificationHelper.onCreateNotification(name = name!!,
                                        todoDateAndTime)
                                }
                            }

                            if (thursdayFridaySaturdaySunday.containsAll(dayList)) {
                                if (day == Calendar.THURSDAY || day == Calendar.FRIDAY ||
                                    day == Calendar.SATURDAY || day == Calendar.SUNDAY
                                ) {
                                    context.startService(startServiceIntent)
                                    notificationHelper.onCreateNotification(name = name!!,
                                        todoDateAndTime)
                                }
                            }
                        }


                        5 -> {
                            if (mondayTuesdayWednesdayThursdayFriday.containsAll(dayList)) {
                                if (day == Calendar.MONDAY || day == Calendar.TUESDAY ||
                                    day == Calendar.WEDNESDAY || day == Calendar.THURSDAY ||
                                    day == Calendar.FRIDAY
                                ) {
                                    context.startService(startServiceIntent)
                                    notificationHelper.onCreateNotification(name = name!!,
                                        todoDateAndTime)
                                }
                            }

                            if (mondayTuesdayWednesdayThursdaySaturday.containsAll(dayList)) {
                                if (day == Calendar.MONDAY || day == Calendar.TUESDAY ||
                                    day == Calendar.WEDNESDAY || day == Calendar.THURSDAY ||
                                    day == Calendar.SATURDAY
                                ) {
                                    context.startService(startServiceIntent)
                                    notificationHelper.onCreateNotification(name = name!!,
                                        todoDateAndTime)
                                }
                            }

                            if (mondayTuesdayWednesdayThursdaySunday.containsAll(dayList)) {
                                if (day == Calendar.MONDAY || day == Calendar.TUESDAY ||
                                    day == Calendar.WEDNESDAY || day == Calendar.THURSDAY ||
                                    day == Calendar.SUNDAY
                                ) {
                                    context.startService(startServiceIntent)
                                    notificationHelper.onCreateNotification(name = name!!,
                                        todoDateAndTime)
                                }
                            }

                            if (tuesdayWednesdayThursdayFridaySaturday.containsAll(dayList)) {
                                if (day == Calendar.TUESDAY ||
                                    day == Calendar.WEDNESDAY || day == Calendar.THURSDAY ||
                                    day == Calendar.FRIDAY ||
                                    day == Calendar.SATURDAY
                                ) {
                                    context.startService(startServiceIntent)
                                    notificationHelper.onCreateNotification(name = name!!,
                                        todoDateAndTime)
                                }
                            }

                            if (tuesdayWednesdayThursdayFridaySunday.containsAll(dayList)) {
                                if (day == Calendar.TUESDAY ||
                                    day == Calendar.WEDNESDAY || day == Calendar.THURSDAY ||
                                    day == Calendar.FRIDAY ||
                                    day == Calendar.SUNDAY
                                ) {
                                    context.startService(startServiceIntent)
                                    notificationHelper.onCreateNotification(name = name!!,
                                        todoDateAndTime)
                                }
                            }

                            if (wednesdayThursdayFridaySaturdaySunday.containsAll(dayList)) {
                                if (day == Calendar.WEDNESDAY || day == Calendar.THURSDAY ||
                                    day == Calendar.FRIDAY ||
                                    day == Calendar.SATURDAY || day == Calendar.SUNDAY
                                ) {
                                    context.startService(startServiceIntent)
                                    notificationHelper.onCreateNotification(name = name!!,
                                        todoDateAndTime)
                                }
                            }
                        }


                        6 -> {
                            if (mondayTuesdayWednesdayThursdayFridaySaturday.containsAll(dayList)) {
                                if (day == Calendar.MONDAY || day == Calendar.TUESDAY ||
                                    day == Calendar.WEDNESDAY || day == Calendar.THURSDAY ||
                                    day == Calendar.FRIDAY ||
                                    day == Calendar.SATURDAY
                                ) {
                                    context.startService(startServiceIntent)
                                    notificationHelper.onCreateNotification(name = name!!,
                                        todoDateAndTime)
                                }
                            }

                            if (mondayTuesdayWednesdayThursdayFridaySunday.containsAll(dayList)) {
                                if (day == Calendar.MONDAY || day == Calendar.TUESDAY ||
                                    day == Calendar.WEDNESDAY || day == Calendar.THURSDAY ||
                                    day == Calendar.FRIDAY ||
                                    day == Calendar.SATURDAY
                                ) {
                                    context.startService(startServiceIntent)
                                    notificationHelper.onCreateNotification(name = name!!,
                                        todoDateAndTime)
                                }
                            }


                            if (tuesdayWednesdayThursdayFridaySaturdaySunday.containsAll(dayList)) {
                                if (day == Calendar.TUESDAY || day == Calendar.WEDNESDAY ||
                                    day == Calendar.THURSDAY || day == Calendar.FRIDAY ||
                                    day == Calendar.SATURDAY || day == Calendar.SUNDAY
                                ) {
                                    context.startService(startServiceIntent)
                                    notificationHelper.onCreateNotification(name = name!!,
                                        todoDateAndTime)
                                }
                            }
                        }
                    }
                }else{
                    context.startService(startServiceIntent)
                    notificationHelper.onCreateNotification(name = name!!,
                        todoDateAndTime)
                }
            }
        }
    }
}