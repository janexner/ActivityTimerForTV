package com.exner.tools.activitytimerfortv.data.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.exner.tools.activitytimerfortv.data.persistence.TimerDataDAO
import com.exner.tools.activitytimerfortv.data.persistence.TimerDataRoomDatabase
import com.exner.tools.activitytimerfortv.data.persistence.TimerProcess
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppComponent {

    @Singleton
    @Provides
    fun provideDao(ftDatabase: TimerDataRoomDatabase): TimerDataDAO =
        ftDatabase.processDAO()

    @Singleton
    @Provides
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        provider: Provider<TimerDataDAO>
    ): TimerDataRoomDatabase =
        Room.databaseBuilder(
            context.applicationContext,
            TimerDataRoomDatabase::class.java,
            "activity_timer_process_database"
        ).fallbackToDestructiveMigration().addCallback(ProcessDatabaseCallback(provider)).build()

    class ProcessDatabaseCallback(
        private val provider: Provider<TimerDataDAO>
    ) : RoomDatabase.Callback() {

        private val applicationScope = CoroutineScope(SupervisorJob())

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            applicationScope.launch(Dispatchers.IO) {
                populateDatabaseWithSampleProcesses()
            }
        }

        private suspend fun populateDatabaseWithSampleProcesses() {
            // Add sample words.
            var meditationTimerProcess =
                TimerProcess(
                    "Test Process 1",
                    "Info",
                    30,
                    10,
                    true,
                    2L,
                    -1L
                )
            provider.get().insert(meditationTimerProcess)
            meditationTimerProcess =
                TimerProcess(
                    "Test Process 2",
                    "Info",
                    15,
                    5,
                    false,
                    0,
                    -1L
                )
            provider.get().insert(meditationTimerProcess)
        }
    }
}