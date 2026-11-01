package com.example.model

enum class BookingStatus(val label: String) {
    CONFIRMED("Confirmed"),
    PENDING("Pending Payment"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled")
}

enum class PaymentStatus(val label: String) {
    PAID("Paid & Settled"),
    PENDING("Awaiting Payment"),
    CASH_ON_ARRIVAL("Pay at Hotel"),
    REFUNDED("Refunded")
}

data class Booking(
    val id: String,
    val bookingRef: String, // e.g. "OD-8294"
    val guestName: String,
    val registrationNumber: String, // Customer Registration Number (mandatory as per user brief!)
    val guestEmail: String,
    val guestPhone: String,
    val idProofType: String = "Aadhaar Card",
    val idProofNumber: String = "XXXX-XXXX-8924",
    val customerAddress: String = "Bhubaneswar, Odisha",
    val emergencyContact: String = "+91 94370 00000",
    val hotelId: String,
    val hotelName: String,
    val district: String,
    val city: String,
    val roomNumber: String, // e.g. "Room 101"
    val roomType: String, // e.g. "Deluxe Suite"
    val checkInDate: String, // e.g. "05 Nov 2026"
    val checkOutDate: String,
    val nights: Int = 1,
    val guestsCount: Int = 2,
    val specialRequests: String = "",
    val basePrice: Double,
    val taxAmount: Double, // 12% GST
    val totalAmount: Double,
    val paymentMethod: String = "Google Pay Scanner", // "Google Pay Scanner", "UPI ID", "Net Banking", "Card"
    val paymentStatus: PaymentStatus = PaymentStatus.PENDING,
    val bookingStatus: BookingStatus = BookingStatus.PENDING,
    val transactionId: String = "",
    val creditedToBankName: String = "",
    val creditedToAccount: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    val roomName: String
        get() = if (roomNumber.isNotBlank()) "$roomNumber ($roomType)" else roomType
}
