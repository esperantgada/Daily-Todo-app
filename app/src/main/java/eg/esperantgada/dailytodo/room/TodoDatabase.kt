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

@Database(entities = [Todo::class, Note::class], version = 10, exportSchema = false)
abstract class TodoDatabase : RoomDatabase(){

    abstract fun todoDao() : TodoDao
    abstract fun noteDao() : NoteDao

    class Callback @Inject constructor(
        private val database: Provider<TodoDatabase>,
    @ApplicationScope private val applicationScope : CoroutineScope) : RoomDatabase.Callback(){
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val noteDao = database.get().noteDao()

            applicationScope.launch {

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