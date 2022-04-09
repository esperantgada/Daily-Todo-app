package eg.esperantgada.dailytodo.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import eg.esperantgada.dailytodo.dependencyinjection.ApplicationScope
import eg.esperantgada.dailytodo.model.Todo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

/**
 * Todo database
 * This database is created or provided by dependency injection
 */

@Database(entities = [Todo::class], version = 2, exportSchema = false)
abstract class TodoDatabase : RoomDatabase(){

    abstract fun todoDao() :TodoDao

    class Callback @Inject constructor(
        private val database: Provider<TodoDatabase>,
    @ApplicationScope private val applicationScope : CoroutineScope) : RoomDatabase.Callback(){
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val tododao = database.get().todoDao()

            applicationScope.launch {

                tododao.insert(Todo(
                    name = "Watch TV",
                    date = "Mon, October 3, 2022",
                    time = "10:50_"))

                tododao.insert(Todo(
                    name = "Take Google Android Certification Exam",
                    isImportant = true,
                    date = "Fri, April 19, 2022",
                    time = "00:00"
                ))

                tododao.insert(Todo(
                    name = "Take Android Development courses",
                    isImportant = true,
                    isCompleted = true,
                    date = "june 2021",
                    time = "200 hours"
                ))
            }
        }
    }
}