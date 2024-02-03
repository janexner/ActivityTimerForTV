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
import java.util.UUID
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
            val secondUuid = UUID.randomUUID().toString()
            var meditationTimerProcess =
                TimerProcess(
                    name = "Test Process 1",
                    info = "A test process that runs for 30 minutes, then leads directly into 'Test Process 2'",
                    uuid = UUID.randomUUID().toString(),
                    processTime = 30,
                    intervalTime = 10,
                    hasAutoChain = true,
                    gotoUuid = secondUuid,
                    gotoName = "Test Process 2",
                    uid = 0L,
                )
            provider.get().insert(meditationTimerProcess)
            meditationTimerProcess =
                TimerProcess(
                    name = "Test Process 2",
                    info = "Test process that is launched by 'Test Process 2'. It runs for 15 minutes.",
                    uuid = secondUuid,
                    processTime = 15,
                    intervalTime = 5,
                    hasAutoChain = false,
                    gotoUuid = null,
                    gotoName = null,
                    uid = 0L,
                )
            provider.get().insert(meditationTimerProcess)
            meditationTimerProcess =
                TimerProcess(
                    "Test Process 3",
                    "Info",
                    uuid = UUID.randomUUID().toString(),
                    10,
                    10,
                    false,
                    null,
                    null,
                    0L
                )
            provider.get().insert(meditationTimerProcess)
            meditationTimerProcess =
                TimerProcess(
                    "Test Process 4",
                    "Info",
                    uuid = UUID.randomUUID().toString(),
                    45,
                    10,
                    false,
                    null,
                    null,
                    0L
                )
            provider.get().insert(meditationTimerProcess)
            meditationTimerProcess =
                TimerProcess(
                    "Test Process 5",
                    "Info",
                    uuid = UUID.randomUUID().toString(),
                    60,
                    30,
                    false,
                    null,
                    null,
                    0L
                )
            provider.get().insert(meditationTimerProcess)
        }
    }
}