package com.example.model

data class AdminBankDetails(
    val bankName: String = "HDFC Bank",
    val accountHolderName: String = "Star Hotel Escrow & Property A/C",
    val accountNumber: String = "50200084920194",
    val ifscCode: String = "HDFC0001234",
    val branchName: String = "Colaba Flagship Branch, Mumbai",
    val upiId: String = "starhotel.owner@hdfcbank",
    val accountType: String = "Business Current Account",
    val isVerified: Boolean = true,
    val currentBalance: Double = 184500.0,
    val totalSettledRevenue: Double = 345000.0,
    val pendingPayouts: Double = 0.0,
    val lastSettledAmount: Double = 14560.0,
    val lastSettledTime: Long = System.currentTimeMillis() - 3600000L
)
