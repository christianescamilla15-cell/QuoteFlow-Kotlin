package com.christianhernandez.quoteflow

import android.app.Application
import android.content.Context
import com.christianhernandez.quoteflow.data.local.QuoteDatabase
import com.christianhernandez.quoteflow.data.remote.ApiClient
import com.christianhernandez.quoteflow.data.repository.QuoteRepository
import java.util.UUID

class QuoteFlowApp : Application() {

    val database: QuoteDatabase by lazy { QuoteDatabase.getDatabase(this) }
    val repository: QuoteRepository by lazy { QuoteRepository(database.quoteDao(), ApiClient.service) }

    override fun onCreate() {
        super.onCreate()
        initDeviceId()
    }

    private fun initDeviceId() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        var deviceId = prefs.getString(KEY_DEVICE_ID, null)
        if (deviceId == null) {
            deviceId = UUID.randomUUID().toString()
            prefs.edit().putString(KEY_DEVICE_ID, deviceId).apply()
        }
        ApiClient.setDeviceId(deviceId)
    }

    companion object {
        private const val PREFS_NAME = "quoteflow_device_prefs"
        private const val KEY_DEVICE_ID = "device_id"
    }
}
