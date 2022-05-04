package eg.esperantgada.dailytodo.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import eg.esperantgada.dailytodo.dependencyinjection.ApplicationScope
import eg.esperantgada.dailytodo.model.Note
import eg.esperantgada.dailytodo.model.Todo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

/**
 * Todo database
 * This database is created or provided by dependency injection
 */

@Database(entities = [Todo::class, Note::class], version = 7, exportSchema = false)
abstract class TodoDatabase : RoomDatabase(){

    abstract fun todoDao() : TodoDao
    abstract fun noteDao() : NoteDao

    class Callback @Inject constructor(
        private val database: Provider<TodoDatabase>,
    @ApplicationScope private val applicationScope : CoroutineScope) : RoomDatabase.Callback(){
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val tododao = database.get().todoDao()
            val noteDao = database.get().noteDao()

            applicationScope.launch {

                tododao.insert(Todo(
                    name = "Watch TV",
                    date = "12/03/2020",
                    time = "10:25",
                    duration = "2 hours",
                    ringtoneUri = "",
                createdAt = "22/02/2020 12:25:36"))

                tododao.insert(Todo(
                    name = "Take Google Android Certification Exam",
                    important = true,
                    date = "23/03/1999",
                    time = "00:00",
                    duration = "2 hours",
                    ringtoneUri = "",
                    createdAt = "22/5/2022 04:25:13"
                ))

                tododao.insert(Todo(
                    name = "Take Android Development courses",
                    important = true,
                    completed = true,
                    date = "30/11/2020",
                    time = "01:45",
                    duration = "2 hours",
                    ringtoneUri = "",
                    createdAt = "23/4/2022"
                ))

                noteDao.insert(Note(
                    title = "Android",
                    description = "Android development"
                ))

                noteDao.insert(Note(
                    title = "Kotlin",
                    description = "Android development language"
                ))

            }
        }
    }
}