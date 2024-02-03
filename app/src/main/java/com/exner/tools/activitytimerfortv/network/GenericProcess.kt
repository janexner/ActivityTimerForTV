package com.exner.tools.activitytimerfortv.network

import com.exner.tools.activitytimerfortv.data.persistence.TimerProcess

data class GenericProcess (
    var name: String,
    var info: String,

    var categoryId: Long?,

    var processTime: Int,
    var intervalTime: Int,

    var hasAutoChain: Boolean,
    var gotoUuid: String?,
    var gotoName: String?,

    var uuid: String
)

fun createTimerProcessFrom(genericProcess: GenericProcess): TimerProcess {
    val result = TimerProcess(
        name = genericProcess.name,
        info = genericProcess.info,
        uuid = genericProcess.uuid,
        processTime = genericProcess.processTime / 60, // minutes!
        intervalTime = genericProcess.intervalTime / 60,
        hasAutoChain = genericProcess.hasAutoChain,
        gotoUuid = genericProcess.gotoUuid,
        gotoName = genericProcess.gotoName,
        uid = 0
    )
    return result
}