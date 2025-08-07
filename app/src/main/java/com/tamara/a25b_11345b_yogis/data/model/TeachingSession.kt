package com.tamara.a25b_11345b_yogis.data.model

data class TeachingSession(
    val sessionId: String = "",
    val userId: String = "",
    val planId: String = "",
    val startedAt: Long = 0L,
    val endedAt: Long? = null,
    val currentElementOrder: Int = 0
)
