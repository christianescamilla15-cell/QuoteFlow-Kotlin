package com.christianhernandez.quoteflow

import android.app.Application
import com.christianhernandez.quoteflow.data.local.QuoteDatabase
import com.christianhernandez.quoteflow.data.repository.QuoteRepository

class QuoteFlowApp : Application() {

    val database: QuoteDatabase by lazy { QuoteDatabase.getDatabase(this) }
    val repository: QuoteRepository by lazy { QuoteRepository(database.quoteDao()) }
}
