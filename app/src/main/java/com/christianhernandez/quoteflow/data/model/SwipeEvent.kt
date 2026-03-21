package com.christianhernandez.quoteflow.data.model

import com.christianhernandez.quoteflow.util.SwipeDirection

data class SwipeEvent(
    val quoteId: String,
    val direction: SwipeDirection,
    val timestamp: Long = System.currentTimeMillis(),
)
