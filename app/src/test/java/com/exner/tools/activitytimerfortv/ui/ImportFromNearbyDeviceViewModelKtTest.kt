package com.exner.tools.activitytimerfortv.ui

import com.exner.tools.activitytimerfortv.data.persistence.TimerProcess
import com.google.android.gms.nearby.connection.Payload
import kotlin.text.Charsets.UTF_8

class ImportFromNearbyDeviceViewModelKtTest {

    @org.junit.jupiter.api.Test
    fun toTimerProcess() {
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
        val element2 = "testNAme|testInfo|testUuid|30|10|true|gotoUuid|gotoName|0|https://www.testurl.com/|0"
        val element2AsPayload = Payload.fromBytes(element2.toByteArray(UTF_8))

        assert(element1 == element2AsPayload.toTimerProcess())
    }
}