package com.exner.tools.activitytimerfortv.audio

import android.content.Context
import android.media.SoundPool
import com.exner.tools.activitytimerfortv.R

object SoundIDs {
    const val SOUND_ID_PROCESS_START: Long = 1
    const val SOUND_ID_PROCESS_END: Long = 2
    const val SOUND_ID_INTERVAL: Long = 3
}

object SoundPoolHolder {
    private lateinit var soundPool: SoundPool
    private val soundMap: MutableMap<Long, Int> = HashMap()
    private var isReady: Boolean = false

    fun loadSounds(context: Context) {
        soundPool = SoundPool.Builder().build()
        // load all default sounds
        soundMap[SoundIDs.SOUND_ID_PROCESS_START] = soundPool.load(context, R.raw.start, 1)
        soundMap[SoundIDs.SOUND_ID_PROCESS_END] = soundPool.load(context, R.raw.stop, 1)
        soundMap[SoundIDs.SOUND_ID_INTERVAL] = soundPool.load(context, R.raw.ping, 1)
        // sounds are loaded, so we are ready to play!
        isReady = true
    }

    fun release() {
        soundPool.release()
    }

    fun playSound(soundId: Long) {
        if (isReady) {
            val soundPoolId = soundMap[soundId]
            if (null != soundPoolId && 0 < soundPoolId) {
                soundPool.play(soundPoolId, 1F, 1F, 0, 0, 1F)
            }
        }
    }
}