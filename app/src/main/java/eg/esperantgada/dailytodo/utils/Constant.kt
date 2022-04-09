package eg.esperantgada.dailytodo.utils

import android.app.Activity

const val ADD_TODO_RESULT_OK = Activity.RESULT_FIRST_USER
const val EDIT_TODO_RESULT_OK = Activity.RESULT_FIRST_USER + 1
const val REQUEST_KEY = "add_edit_request_key"
const val ADD_EDIT_RESULT_KEY = "add_edit_result_key"
const val INVALID_INPUT_MESSAGE = "Name cannot be empty"
const val TODO_ADDED_MESSAGE = "Todo saved"
const val TODO_UPDATED_MESSAGE = "Todo updated"

//for notification
const val CHANNEL_ID ="channel_id"
const val NOTIFICATION_ID = 1
const val NAME = "Notification title"
const val DESCRIPTION_TEXT = "Notification description"

const val SET_ACTION = "eg.esperantgada.dailytodo.broadcastreceiver.TodoBroadcastReceiver"
const val TODO_ALARM_TAG = "TodoAlarm"