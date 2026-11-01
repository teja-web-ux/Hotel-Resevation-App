package com.example.model

enum class TransactionStatus(val label: String) {
    SETTLED("Directly Settled to Bank"),
    PENDING("Processing Settlement"),
    FAILED("Failed")
}

data class PaymentTransaction(
    val id: String,
    val transactionRef: String, // e.g. "TXN-IMPS-8921840"
    val bookingRef: String,
    val guestName: String,
    val guestEmail: String,
    val amount: Double,
    val paymentMethod: String, // "UPI", "Net Banking", "Card", "Cash"
    val destinationBank: String,
    val destinationAccount: String,
    val destinationUpi: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: TransactionStatus = TransactionStatus.SETTLED,
    val settlementType: String = "Instant IMPS/UPI Payout"
)
