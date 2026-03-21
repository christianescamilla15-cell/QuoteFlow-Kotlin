package com.christianhernandez.quoteflow.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

object HapticService {
    private fun getVibrator(context: Context): Vibrator {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            manager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    fun lightTap(context: Context) {
        vibrate(context, 20, VibrationEffect.EFFECT_TICK)
    }

    fun mediumTap(context: Context) {
        vibrate(context, 40, VibrationEffect.EFFECT_CLICK)
    }

    fun heavyTap(context: Context) {
        vibrate(context, 60, VibrationEffect.EFFECT_HEAVY_CLICK)
    }

    fun success(context: Context) {
        vibrate(context, 30, VibrationEffect.EFFECT_CLICK)
    }

    private fun vibrate(context: Context, durationMs: Long, effect: Int) {
        val vibrator = getVibrator(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(VibrationEffect.createPredefined(effect))
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(durationMs)
        }
    }
}
