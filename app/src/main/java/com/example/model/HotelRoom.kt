package com.example.model

data class HotelRoom(
    val id: String,
    val name: String,
    val category: String, // "All Hotel", "Recommended", "Popular", "Trending"
    val location: String, // "Paris, France"
    val address: String = "79 Place de La Madeleine, Paris, 75008, France",
    val country: String = "France",
    val pricePerNight: Double,
    val maxGuests: Int = 3,
    val bedType: String = "King Bed",
    val bedrooms: Int = 4,
    val bathrooms: Int = 2,
    val sizeSqFt: Int = 4000,
    val description: String = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in cillum pariatur.",
    val amenities: List<String> = listOf("Swimming Pool", "WiFi", "Restaurant", "Parking", "Meeting Room", "Elevator", "Fitness Center", "24-hours Open"),
    val imageUrl: String,
    val galleryUrls: List<String> = emptyList(),
    val rating: Float = 4.8f,
    val reviewCount: Int = 4372,
    val isAvailable: Boolean = true,
    val floorNumber: Int = 4,
    val isBookmarked: Boolean = false
)
