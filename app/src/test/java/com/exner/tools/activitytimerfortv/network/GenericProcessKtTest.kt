package com.exner.tools.activitytimerfortv.network

import com.exner.tools.activitytimerfortv.data.persistence.TimerProcess
import org.junit.jupiter.api.Test

class GenericProcessKtTest {

    @Test
    fun createTimerProcessFrom() {
        val element1 = TimerProcess(
            name = "testName",
            info = "testInfo",
            uuid = "testUuid",
            processTime = 30,
            intervalTime = 10,
            hasAutoChain = true,
            gotoUuid = "gotoUuid",
            gotoName = "gotoName",
            categoryId = 0L,
            backgroundUri = "https://www.testurl.com/",
            uid = 0L
        )
        val element2 = GenericProcess(
            name = "testName",
            info = "testInfo",
            uuid = "testUuid",
            processTime = 30,
            intervalTime = 10,
            hasAutoChain = true,
            gotoUuid = "gotoUuid",
            gotoName = "gotoName",
            categoryId = 0L,
            backgroundUri = "https://www.testurl.com/",
        )

        assert(element1 == createTimerProcessFrom(element2))
    }
}