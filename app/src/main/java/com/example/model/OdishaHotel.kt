package com.example.model

data class HotelRoomUnit(
    val id: String,
    val roomNumber: String, // e.g. "Room 101", "Room 204", "Lagoon Villa 12"
    val roomType: String, // "Deluxe Suite", "Executive Lagoon View", "Presidential Villa"
    val pricePerNight: Double,
    val maxGuests: Int = 2,
    val isAvailable: Boolean = true, // Live availability status requested by user!
    val bedType: String = "1 King Bed",
    val sizeSqFt: Int = 420,
    val floor: Int = 1,
    val amenities: List<String> = listOf("High-Speed Wi-Fi", "Air Conditioning", "Smart TV", "Mini Bar", "Room Service")
)

data class OdishaHotel(
    val id: String,
    val name: String,
    val district: String, // "Khordha", "Puri", "Cuttack", "Ganjam", "Sundargarh", "Sambalpur", "Balasore", "Mayurbhanj"
    val city: String, // "Bhubaneswar", "Puri", "Cuttack", "Gopalpur", "Rourkela", "Sambalpur", "Konark"
    val address: String,
    val isPremium: Boolean = true, // Premium flag for search filter
    val rating: Float = 4.8f,
    val reviewCount: Int = 1420,
    val startingPrice: Double,
    val imageUrl: String,
    val galleryUrls: List<String> = emptyList(),
    val description: String,
    val facilities: List<String> = listOf("Free Wi-Fi", "Swimming Pool", "Multi-cuisine Restaurant", "Free Parking", "24/7 Front Desk", "Conference Hall"),
    val rooms: List<HotelRoomUnit> = emptyList(),
    val isBookmarked: Boolean = false
) {
    val availableRoomsCount: Int
        get() = rooms.count { it.isAvailable }

    val totalRoomsCount: Int
        get() = rooms.size
}
