package com.exner.tools.activitytimerfortv.data.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.exner.tools.activitytimerfortv.data.persistence.TimerDataDAO
import com.exner.tools.activitytimerfortv.data.persistence.TimerDataRoomDatabase
import com.exner.tools.activitytimerfortv.data.persistence.TimerProcess
import com.exner.tools.activitytimerfortv.data.persistence.TimerProcessCategory
import com.exner.tools.activitytimerfortv.ui.tools.CategoryListDefinitions
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
            val noCategory = TimerProcessCategory(
                name = "None",
                backgroundUri = "https://fototimer.net/assets/activitytimer/bg-none.png",
                uid = CategoryListDefinitions.CATEGORY_UID_NONE
            )
            provider.get().insertCategory(noCategory)
            val breathingCategory = TimerProcessCategory(
                name = "Breathing",
                backgroundUri = "https://fototimer.net/assets/activitytimer/bg-breathing.png",
                uid = 1L
            )
            provider.get().insertCategory(breathingCategory)
            var meditationTimerProcess =
                TimerProcess(
                    name = "Basic 1 - Arriving",
                    info = "5 minutes to help you arrive. After that, 'Basic 1 - Mindful Breathing' will be started.",
                    uuid = UUID.randomUUID().toString(),
                    processTime = 300,
                    intervalTime = 300,
                    hasAutoChain = true,
                    gotoUuid = secondUuid,
                    gotoName = "Basic 1 - Mindful Breathing",
                    categoryId = breathingCategory.uid,
                    backgroundUri = null,
                    uid = 0L,
                )
            provider.get().insert(meditationTimerProcess)
            meditationTimerProcess =
                TimerProcess(
                    name = "Basic 1 - Mindful Breathing",
                    info = "15 minutes for mindful breathing.",
                    uuid = secondUuid,
                    processTime = 15 * 60,
                    intervalTime = 15 * 60,
                    hasAutoChain = false,
                    gotoUuid = null,
                    gotoName = null,
                    categoryId = breathingCategory.uid,
                    backgroundUri = null,
                    uid = 0L,
                )
            provider.get().insert(meditationTimerProcess)
            meditationTimerProcess =
                TimerProcess(
                    "Test Process 3",
                    "Not much to say. 10 seconds, just one interval.",
                    uuid = UUID.randomUUID().toString(),
                    10,
                    10,
                    false,
                    null,
                    null,
                    categoryId = noCategory.uid,
                    backgroundUri = null,
                    uid = 0L
                )
            provider.get().insert(meditationTimerProcess)
            val fourthUuid = UUID.randomUUID().toString()
            meditationTimerProcess =
                TimerProcess(
                    "Test Process 4",
                    "A self-referential process, in that it loops back to itself once it has finished. This process runs for 45 seconds, with an interval every 10 seconds.",
                    uuid = fourthUuid,
                    45,
                    10,
                    hasAutoChain = true,
                    gotoUuid = fourthUuid,
                    gotoName = "Test Process 4",
                    categoryId = noCategory.uid,
                    backgroundUri = "https://fototimer.net/assets/activitytimer/bg-test.png",
                    uid = 0L
                )
            provider.get().insert(meditationTimerProcess)
            meditationTimerProcess =
                TimerProcess(
                    "Test Process 5",
                    "A 1-minute process with two 30-second intervals.",
                    uuid = UUID.randomUUID().toString(),
                    60,
                    30,
                    false,
                    null,
                    null,
                    categoryId = noCategory.uid,
                    backgroundUri = null,
                    uid = 0L
                )
            provider.get().insert(meditationTimerProcess)
        }
    }
}
