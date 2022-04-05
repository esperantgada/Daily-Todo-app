package eg.esperantgada.dailytodo.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Provides
import eg.esperantgada.dailytodo.dependencyinjection.ApplicationScope
import eg.esperantgada.dailytodo.model.TodoEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

/**
 * Todo database
 * This database is created or provided by dependency injection
 */

@Database(entities = [TodoEntity::class], version = 1, exportSchema = false)
abstract class TodoDatabase : RoomDatabase(){

    abstract fun todoDao() :TodoDao

    class Callback @Inject constructor(
        private val database: Provider<TodoDatabase>,
    @ApplicationScope private val applicationScope : CoroutineScope) : RoomDatabase.Callback(){
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val dao = database.get().todoDao()

            applicationScope.launch {
                dao.insert(TodoEntity("Do exercises"))
                dao.insert(TodoEntity("Wash TV", false))
                dao.insert(TodoEntity("Create a small application", true, true))
                dao.insert(TodoEntity("Write a program in Python", true, true))
                dao.insert(TodoEntity("Do Sport", true, false))
                dao.insert(TodoEntity("Go to market", false, false))
                dao.insert(TodoEntity("Take bath"))
                dao.insert(TodoEntity("Watch courses video"))
                dao.insert(TodoEntity("Go to church meeting"))
                dao.insert(TodoEntity("Clear room"))
                dao.insert(TodoEntity("Wash clothes"))
                dao.insert(TodoEntity("Laugh"))
                dao.insert(TodoEntity("Break up"))
                dao.insert(TodoEntity("Wake up"))
                dao.insert(TodoEntity("Wake up"))
                dao.insert(TodoEntity("Wake up"))
                dao.insert(TodoEntity("Wake up"))
            }
        }
    }
}