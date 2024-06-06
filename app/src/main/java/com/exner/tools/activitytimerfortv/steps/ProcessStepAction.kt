package com.exner.tools.activitytimerfortv.steps

open class ProcessStepAction(
    val processName: String
)

class ProcessStartAction(
    processName: String,
    val processUuid: String
): ProcessStepAction(processName)

class ProcessDisplayStepAction(
    processName: String,
    val processInfo: String,
    val processParameters: String,
    val currentRound: Int,
    val totalRounds: Int,
    val currentProcessTime: Int,
    val currentIntervalTime: Int
) : ProcessStepAction(processName)

class ProcessSoundAction(
    processName: String,
    val soundId: Long
) : ProcessStepAction(processName)

class ProcessGotoAction(
    processName: String,
    val gotoUuid: String
): ProcessStepAction(processName)

class ProcessJumpbackAction(
    processName: String,
    val stepNumber: Int
): ProcessStepAction(processName)