package com.example.model

enum class UserRole(val label: String) {
    GUEST("Customer / Guest"),
    ADMIN("Odisha Hotel Admin")
}

data class UserProfile(
    val id: String = "U-001",
    val name: String = "Subhashree Mohapatra",
    val registrationNumber: String = "REG-OD-2026-9842",
    val email: String = "guest.odisha@hotel.com",
    val passwordHash: String = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855", // Encrypted security hash
    val phone: String = "+91 94370 12345",
    val district: String = "Khordha",
    val city: String = "Bhubaneswar",
    val role: UserRole = UserRole.GUEST,
    val nickname: String = "Subhashree",
    val dateOfBirth: String = "1995-12-27",
    val country: String = "India",
    val gender: String = "Female",
    val registeredDate: String = "2026-01-15",
    val totalBookings: Int = 2,
    val totalSpent: Double = 12500.0,
    val avatarUrl: String = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400&q=80"
)
