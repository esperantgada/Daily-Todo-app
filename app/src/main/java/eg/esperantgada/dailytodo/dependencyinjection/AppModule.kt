package eg.esperantgada.dailytodo.dependencyinjection

import android.app.Application
import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import eg.esperantgada.dailytodo.repository.CategoryRepository
import eg.esperantgada.dailytodo.repository.NoteRepository
import eg.esperantgada.dailytodo.repository.TodoRepository
import eg.esperantgada.dailytodo.room.TodoDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Dependency injection for classes
 */

@InstallIn(SingletonComponent::class)
@Module
object AppModule{

    @Provides
    @Singleton
    fun provideDatabase(application: Application, callback : TodoDatabase.Callback) =
        Room.databaseBuilder(application, TodoDatabase::class.java, "todo_database")
            .fallbackToDestructiveMigration()
            .addCallback(callback)
            .build()

    @Singleton
    @Provides
    fun provideTodoDao(todoDatabase: TodoDatabase) = todoDatabase.getTodoDao()

    @Provides
    @Singleton
    fun provideNoteDao(todoDatabase: TodoDatabase) = todoDatabase.getNoteDao()

    @Provides
    @Singleton
    fun provideCategoryDao(todoDatabase: TodoDatabase) = todoDatabase.getCategoryDao()



    @Provides
    @Singleton
    fun provideTodoRepository(
        todoDatabase: TodoDatabase
    ) = TodoRepository(todoDatabase.getTodoDao())



    @Provides
    @Singleton
    fun provideNoteRepository(
        todoDatabase: TodoDatabase
    ) = NoteRepository(todoDatabase.getNoteDao())

    @Provides
    @Singleton
    fun provideCategoryRepository(
        todoDatabase: TodoDatabase
    ) = CategoryRepository(todoDatabase.getCategoryDao())


    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())

    @Provides
    @Singleton
    fun provideContext(application: Application) : Context = application.applicationContext
}

/**
 * Create an [Annotation]
 */
@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope