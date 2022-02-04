package eg.esperantgada.dailytodo.dependencyinjection

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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

    @Provides
    fun provideTodoDao(todoDatabase: TodoDatabase) = todoDatabase.todoDao()

    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())
}

/**
 * Create an [Annotation]
 */
@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope