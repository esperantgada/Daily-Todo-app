package eg.esperantgada.dailytodo.repository

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import eg.esperantgada.dailytodo.MainActivity
import eg.esperantgada.dailytodo.model.Todo
import eg.esperantgada.dailytodo.room.TodoDao
import eg.esperantgada.dailytodo.utils.TODO_ALARM_TAG
import eg.esperantgada.dailytodo.worker.TodoWorker
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TodoRepository @Inject constructor(private val todoDao: TodoDao) {

    suspend fun insert(context: Context, todo : Todo) {
        todoDao.insert(todo)
        setWorkRequest(context, todo)
    }

    suspend fun update(context: Context, todo: Todo){
        todoDao.update(todo)
        setWorkRequest(context, todo)
    }

    suspend fun delete(todo: Todo){
        todoDao.delete(todo)
    }

    //fun getUri(todoId : Int) = todoDao.getUri(todoId)

    fun getTodoList(
        searchQuery : String,
        sortOrder: SortOrder,
        hideCompleted : Boolean
    ) = Pager(
        PagingConfig(
            pageSize = 20,
            maxSize = 500,
            enablePlaceholders = true
        ), pagingSourceFactory = {todoDao.getTodoList(searchQuery, sortOrder, hideCompleted)}
        ).flow

    fun getAllTodo() = todoDao.getAllTodo()

    fun deleteAllCompletedTodo(){
        todoDao.deleteAllCompletedTodo()
    }

    private fun setWorkRequest(context: Context, todo: Todo){

        val timeFormat = SimpleDateFormat("hh:mm a")
        val parsedTime = timeFormat.parse(todo.time)
        val expectedFormat = SimpleDateFormat("hh:mm")
        val newTime = expectedFormat.format(parsedTime!!)

        val dateAndTimeReminder = "${todo.date} ${todo.time}"
        Log.d(TODO_ALARM_TAG, "Date and Time $dateAndTimeReminder")

        val format = SimpleDateFormat("dd/MM/yyyy h:mm a")
        val todoTime = format.parse(dateAndTimeReminder)
        val currentTime = Calendar.getInstance()

        val (todoHour, todoMinute) = newTime.split(":").map { it }
        Log.d(eg.esperantgada.dailytodo.viewmodel.TAG, "NEW FORMATTED TIME IS $newTime")

        val todoDueDate = Calendar.getInstance()
        todoDueDate.set(Calendar.HOUR_OF_DAY, todoHour.toInt())
        todoDueDate.set(Calendar.MINUTE, todoMinute.toInt())
        todoDueDate.set(Calendar.SECOND, 0)

        if (todoDueDate.before(currentTime)){
            todoDueDate.add(Calendar.HOUR_OF_DAY, 24)
        }

        val dueTime = todoDueDate.timeInMillis.minus(currentTime.timeInMillis)

        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        val workManager = WorkManager.getInstance(context)
        val sentData = todo.repeatFrequency?.let {
            Data.Builder()
                .putString("name", todo.name)
                .putString("date", todo.date)
                .putString("time", todo.time)
                .putStringArray("days", it.toTypedArray())
                .putString("ringtoneUri", todo.ringtoneUri)
                .putInt("todoId", todo.id)
                .build()
        }

        val repeatWorkRequest = sentData?.let {
            OneTimeWorkRequestBuilder<TodoWorker>()
                .setConstraints(constraints)
                .setInputData(it)
                .setInitialDelay(dueTime, TimeUnit.MILLISECONDS)
                .addTag("OUT PUT")
        }?.build()

        val onTimeData = todo.repeatFrequency?.let {
            Data.Builder()
                .putString("name", todo.name)
                .putString("date", todo.date)
                .putString("time", todo.time)
                .putString("ringtoneUri", todo.ringtoneUri)
                .putInt("todoId", todo.id)
                .build()
        }


        val onTimeWorkRequest = onTimeData?.let {
            OneTimeWorkRequestBuilder<TodoWorker>()
                .setConstraints(constraints)
                .setInputData(it)
                .addTag("OUT PUT")
        }?.build()


        if (todo.repeatFrequency!!.isNotEmpty()){
            if (repeatWorkRequest != null) {
                workManager.enqueue(repeatWorkRequest)
            }
        }else{
            if (onTimeWorkRequest != null) {
                workManager.enqueue(onTimeWorkRequest)
            }

        }
    }
}