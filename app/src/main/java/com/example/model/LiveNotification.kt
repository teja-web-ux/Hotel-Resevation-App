package com.example.model

enum class NotificationType {
    PAYMENT_RECEIVED,
    NEW_BOOKING,
    BANK_UPDATED,
    BOOKING_CANCELLED,
    SYSTEM_ALERT
}

data class LiveNotification(
    val id: String,
    val title: String,
    val message: String,
    val amount: Double = 0.0,
    val type: NotificationType = NotificationType.PAYMENT_RECEIVED,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
