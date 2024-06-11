package com.exner.tools.activitytimerfortv.network

class TimerEndpoint(
    val endpointId: String,
    val userName: String
) {
    override fun equals(other: Any?): Boolean {
        if (other is TimerEndpoint) {
            return other.endpointId == endpointId && other.userName == userName
        }
        return false
    }
}