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

@Database(entities = [Todo::class], version = 1, exportSchema = false)
abstract class TodoDatabase : RoomDatabase(){

    abstract fun todoDao() :TodoDao

    class Callback @Inject constructor(
        private val database: Provider<TodoDatabase>,
    @ApplicationScope private val applicationScope : CoroutineScope) : RoomDatabase.Callback(){
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val dao = database.get().todoDao()

            applicationScope.launch {
                dao.insert(Todo("Do exercises"))
                dao.insert(Todo("Wash TV", false))
                dao.insert(Todo("Create a small application", true, true))
                dao.insert(Todo("Write a program in Python", true, true))
                dao.insert(Todo("Do Sport", true, false))
                dao.insert(Todo("Go to market", false, false))
                dao.insert(Todo("Take bath"))
                dao.insert(Todo("Watch courses video"))
                dao.insert(Todo("Go to church meeting"))
                dao.insert(Todo("Clear room"))
                dao.insert(Todo("Wash clothes"))
                dao.insert(Todo("Laugh"))
                dao.insert(Todo("Break up"))
                dao.insert(Todo("Wake up"))
                dao.insert(Todo("Wake up"))
                dao.insert(Todo("Wake up"))
                dao.insert(Todo("Wake up"))
            }
        }
    }
}