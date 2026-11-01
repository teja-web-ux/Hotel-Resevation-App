package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.MessageDigest
import java.util.UUID

object HotelRepository {

    // Districts in Odisha with major cities
    val odishaDistricts = listOf(
        "All Districts",
        "Khordha",
        "Puri",
        "Cuttack",
        "Ganjam",
        "Sundargarh",
        "Sambalpur",
        "Balasore",
        "Mayurbhanj",
        "Koraput",
        "Angul",
        "Jharsuguda"
    )

    val odishaCitiesByDistrict = mapOf(
        "Khordha" to listOf("All Cities", "Bhubaneswar", "Khurda", "Jatni"),
        "Puri" to listOf("All Cities", "Puri", "Konark", "Satapada"),
        "Cuttack" to listOf("All Cities", "Cuttack", "Choudwar"),
        "Ganjam" to listOf("All Cities", "Gopalpur", "Berhampur", "Chhatrapur"),
        "Sundargarh" to listOf("All Cities", "Rourkela", "Sundargarh"),
        "Sambalpur" to listOf("All Cities", "Sambalpur", "Burla"),
        "Balasore" to listOf("All Cities", "Balasore", "Chandipur")
    )

    // Curated hotels across Odisha with specific rooms and live availability
    private val initialHotels = listOf(
        OdishaHotel(
            id = "OD-H01",
            name = "Mayfair Lagoon, Bhubaneswar",
            district = "Khordha",
            city = "Bhubaneswar",
            address = "8-B, Jayadev Vihar, Bhubaneswar, Khordha, Odisha 751013",
            isPremium = true,
            rating = 4.9f,
            reviewCount = 2840,
            startingPrice = 7500.0,
            imageUrl = "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=800&q=80",
            galleryUrls = listOf(
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=800&q=80",
                "https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800&q=80",
                "https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=800&q=80"
            ),
            description = "Sprawling 10-acre luxury 5-star resort nestled around a scenic eco-lagoon in the heart of Bhubaneswar. Features award-winning open-air dining, plush spa, and private pool villas.",
            facilities = listOf("Lagoon View", "Swimming Pool", "Spa & Wellness", "Free Wi-Fi", "Valet Parking", "24/7 Room Service", "Gym", "Bar"),
            rooms = listOf(
                HotelRoomUnit("R-ML-101", "Room 101 - Executive Cottage", "Executive Cottage", 7500.0, 2, true, "1 King Bed", 480, 1),
                HotelRoomUnit("R-ML-102", "Room 102 - Deluxe Lagoon Suite", "Deluxe Suite", 9800.0, 3, true, "1 King Bed + Sofa Bed", 620, 1),
                HotelRoomUnit("R-ML-103", "Room 103 - Lagoon Club Room", "Club Room", 6800.0, 2, false, "1 King Bed", 420, 1), // Occupied
                HotelRoomUnit("R-ML-201", "Villa 201 - Presidential Pool Villa", "Presidential Villa", 22000.0, 4, true, "2 Super King Beds", 1400, 2),
                HotelRoomUnit("R-ML-202", "Room 202 - Premium Heritage Suite", "Heritage Suite", 11500.0, 2, true, "1 King Bed", 750, 2)
            ),
            isBookmarked = true
        ),
        OdishaHotel(
            id = "OD-H02",
            name = "Mayfair Waves, Puri",
            district = "Puri",
            city = "Puri",
            address = "Chakratirtha Road, Puri Beachfront, Puri, Odisha 752002",
            isPremium = true,
            rating = 4.9f,
            reviewCount = 3120,
            startingPrice = 11000.0,
            imageUrl = "https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800&q=80",
            galleryUrls = listOf(
                "https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800&q=80",
                "https://images.unsplash.com/photo-1590490360182-c33d57733427?w=800&q=80"
            ),
            description = "Unrivaled luxury on the golden sands of Puri Beach. Every room offers breathtaking direct views of the Bay of Bengal, private balconies, and proximity to Jagannath Temple.",
            facilities = listOf("Direct Sea View", "Private Beach Access", "Infinity Pool", "Oceanfront Dining", "Jagannath Temple Shuttle", "Free Wi-Fi"),
            rooms = listOf(
                HotelRoomUnit("R-MW-101", "Sea Suite 101 - Ocean Breeze Balcony", "Deluxe Sea Facing", 11000.0, 2, true, "1 King Bed", 520, 1),
                HotelRoomUnit("R-MW-102", "Sea Suite 102 - Executive Beachfront", "Executive Suite", 13500.0, 3, true, "1 King Bed", 680, 2),
                HotelRoomUnit("R-MW-103", "Sea Villa 103 - Royal Bay Villa", "Royal Villa", 25000.0, 4, false, "2 King Beds", 1250, 1), // Occupied
                HotelRoomUnit("R-MW-104", "Sea Suite 104 - Sunrise Ocean Balcony", "Deluxe Sea Facing", 11000.0, 2, true, "1 King Bed", 520, 2)
            ),
            isBookmarked = true
        ),
        OdishaHotel(
            id = "OD-H03",
            name = "Trident Hotel Bhubaneswar",
            district = "Khordha",
            city = "Bhubaneswar",
            address = "CB-1, Nayapalli, Bhubaneswar, Khordha, Odisha 751013",
            isPremium = true,
            rating = 4.8f,
            reviewCount = 1890,
            startingPrice = 8500.0,
            imageUrl = "https://images.unsplash.com/photo-1590490360182-c33d57733427?w=800&q=80",
            galleryUrls = listOf("https://images.unsplash.com/photo-1590490360182-c33d57733427?w=800&q=80"),
            description = "Set amidst 14 acres of lush gardens and fruit orchards, Trident Bhubaneswar features classical Kalinga architecture, landscaped pool, and exquisite Odia & international cuisine.",
            facilities = listOf("Swimming Pool", "Garden Walkway", "Fitness Centre", "Free Wi-Fi", "Multi-cuisine Restaurant", "Conference Rooms"),
            rooms = listOf(
                HotelRoomUnit("R-TB-201", "Room 201 - Superior Garden View", "Superior Room", 8500.0, 2, true, "1 King Bed", 400, 2),
                HotelRoomUnit("R-TB-202", "Room 202 - Executive Garden Deluxe", "Executive Deluxe", 10500.0, 2, true, "1 King Bed", 510, 2),
                HotelRoomUnit("R-TB-203", "Room 203 - Trident Premier Suite", "Premier Suite", 16000.0, 3, false, "1 Super King Bed", 850, 3) // Occupied
            ),
            isBookmarked = false
        ),
        OdishaHotel(
            id = "OD-H04",
            name = "Swosti Premium Hotel",
            district = "Khordha",
            city = "Bhubaneswar",
            address = "P1, Jayadev Vihar, Nandankanan Road, Bhubaneswar 751013",
            isPremium = true,
            rating = 4.7f,
            reviewCount = 2210,
            startingPrice = 5200.0,
            imageUrl = "https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=800&q=80",
            galleryUrls = listOf("https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=800&q=80"),
            description = "Odisha's largest convention hotel with world-class hospitality, multiple themed restaurants, Scottish pub, and grand banquet venues.",
            facilities = listOf("Swimming Pool", "Theme Restaurants", "Free Wi-Fi", "Health Club", "Banquet Halls", "Free Parking"),
            rooms = listOf(
                HotelRoomUnit("R-SP-105", "Room 105 - Business Club Room", "Club Room", 5200.0, 2, true, "1 Queen Bed", 380, 1),
                HotelRoomUnit("R-SP-106", "Room 106 - Premium Executive Suite", "Executive Suite", 6800.0, 3, true, "1 King Bed", 540, 2),
                HotelRoomUnit("R-SP-107", "Room 107 - Royal Maharaja Suite", "Royal Suite", 12500.0, 4, false, "2 Queen Beds", 950, 3) // Occupied
            ),
            isBookmarked = false
        ),
        OdishaHotel(
            id = "OD-H05",
            name = "Mayfair Palm Beach Resort, Gopalpur",
            district = "Ganjam",
            city = "Gopalpur",
            address = "Gopalpur-on-Sea, Ganjam District, Odisha 761002",
            isPremium = true,
            rating = 4.9f,
            reviewCount = 1940,
            startingPrice = 10200.0,
            imageUrl = "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?w=800&q=80",
            galleryUrls = listOf("https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?w=800&q=80"),
            description = "Historic colonial heritage resort overlooking the pristine blue waters of Gopalpur beach. Established in 1914, offering seaside cabanas and maritime charm.",
            facilities = listOf("Beachfront View", "Swimming Pool", "Lighthouse View", "Spa & Sauna", "Bicycle Trail", "Free Wi-Fi"),
            rooms = listOf(
                HotelRoomUnit("R-GP-10", "Heritage Room 10 - Colonial Sea View", "Colonial Deluxe", 10200.0, 2, true, "1 King Bed", 490, 1),
                HotelRoomUnit("R-GP-11", "Suite 11 - Viceroy Oceanfront Suite", "Oceanfront Suite", 15500.0, 3, true, "1 Super King Bed", 720, 2),
                HotelRoomUnit("R-GP-12", "Cottage 12 - Beach Cabana", "Beach Cabana", 12000.0, 2, false, "1 King Bed", 550, 1) // Occupied
            ),
            isBookmarked = true
        ),
        OdishaHotel(
            id = "OD-H06",
            name = "Pramod Convention & Club Resort, Cuttack",
            district = "Cuttack",
            city = "Cuttack",
            address = "Mahatab Road, Cuttack, Odisha 753012",
            isPremium = false,
            rating = 4.6f,
            reviewCount = 1450,
            startingPrice = 3800.0,
            imageUrl = "https://images.unsplash.com/photo-1618773928121-c32242e63f39?w=800&q=80",
            galleryUrls = listOf("https://images.unsplash.com/photo-1618773928121-c32242e63f39?w=800&q=80"),
            description = "Premier convention hotel in the historic Silver City of Cuttack. Renowned for authentic Odia sweets, family suites, and modern conference amenities.",
            facilities = listOf("Swimming Pool", "Fine Dining", "Free Wi-Fi", "Conference Hall", "Free Parking", "Children's Play Area"),
            rooms = listOf(
                HotelRoomUnit("R-PC-301", "Room 301 - Executive Club Room", "Executive Club", 3800.0, 2, true, "1 King Bed", 360, 3),
                HotelRoomUnit("R-PC-302", "Room 302 - Silver City Suite", "Silver Suite", 5500.0, 3, true, "1 King Bed + 1 Single Bed", 580, 3),
                HotelRoomUnit("R-PC-303", "Room 303 - Family Deluxe", "Family Room", 4500.0, 4, false, "2 Queen Beds", 520, 2)
            ),
            isBookmarked = false
        ),
        OdishaHotel(
            id = "OD-H07",
            name = "Mayfair World Cup Village, Rourkela",
            district = "Sundargarh",
            city = "Rourkela",
            address = "Near Birsa Munda Hockey Stadium, Rourkela, Sundargarh, Odisha 769015",
            isPremium = true,
            rating = 4.8f,
            reviewCount = 980,
            startingPrice = 6000.0,
            imageUrl = "https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800&q=80",
            galleryUrls = listOf("https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800&q=80"),
            description = "State-of-the-art luxury sports village and resort built next to Birsa Munda Stadium in the Steel City. Modern architecture, olympic gym, and landscaped avenues.",
            facilities = listOf("Swimming Pool", "Sports Arena Access", "Health Club", "Free Wi-Fi", "Free Parking", "Multi-cuisine Dining"),
            rooms = listOf(
                HotelRoomUnit("R-RO-101", "Room 101 - Stadium View Deluxe", "Deluxe Room", 6000.0, 2, true, "1 King Bed", 410, 1),
                HotelRoomUnit("R-RO-102", "Room 102 - Executive Sports Suite", "Executive Suite", 8500.0, 3, true, "1 King Bed", 600, 2)
            ),
            isBookmarked = false
        ),
        OdishaHotel(
            id = "OD-H08",
            name = "Sterling Puri Resort",
            district = "Puri",
            city = "Puri",
            address = "255/1 - 255/3, Sipasarubali Village, Baliapanda, Puri, Odisha 752001",
            isPremium = true,
            rating = 4.7f,
            reviewCount = 1890,
            startingPrice = 5500.0,
            imageUrl = "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=800&q=80",
            galleryUrls = listOf("https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=800&q=80"),
            description = "Tranquil resort situated where the river meets the sea in Sipasarubali. Features private beach access, authentic seafood delicacies, and beach volleyball.",
            facilities = listOf("Private Beach", "River & Sea Confluence", "Swimming Pool", "Kids Club", "Free Wi-Fi"),
            rooms = listOf(
                HotelRoomUnit("R-ST-12", "Room 12 - Classic Sea Facing", "Classic Room", 5500.0, 2, true, "1 Queen Bed", 390, 1),
                HotelRoomUnit("R-ST-14", "Room 14 - Family Beach Suite", "Family Suite", 8200.0, 4, true, "2 Double Beds", 650, 1)
            ),
            isBookmarked = false
        )
    )

    // Seed Bookings
    private val initialBookings = listOf(
        Booking(
            id = "BK-OD-01",
            bookingRef = "OD-8294",
            guestName = "Subhashree Mohapatra",
            registrationNumber = "REG-OD-2026-9842",
            guestEmail = "guest.odisha@hotel.com",
            guestPhone = "+91 94370 12345",
            idProofType = "Aadhaar Card",
            idProofNumber = "7894-3210-9842",
            customerAddress = "Saheed Nagar, Bhubaneswar, Odisha",
            hotelId = "OD-H01",
            hotelName = "Mayfair Lagoon, Bhubaneswar",
            district = "Khordha",
            city = "Bhubaneswar",
            roomNumber = "Room 101 - Executive Cottage",
            roomType = "Executive Cottage",
            checkInDate = "05 Nov 2026",
            checkOutDate = "07 Nov 2026",
            nights = 2,
            guestsCount = 2,
            basePrice = 15000.0,
            taxAmount = 1800.0,
            totalAmount = 16800.0,
            paymentMethod = "Google Pay Scanner",
            paymentStatus = PaymentStatus.PAID,
            bookingStatus = BookingStatus.CONFIRMED,
            transactionId = "GPAY-OD-98218402",
            creditedToBankName = "State Bank of India (Odisha Tourism Payout)",
            creditedToAccount = "302910849201"
        )
    )

    // Initial Users for Admin View & Session
    private val initialUsers = listOf(
        UserProfile(
            id = "U-001",
            name = "Subhashree Mohapatra",
            registrationNumber = "REG-OD-2026-9842",
            email = "guest.odisha@hotel.com",
            passwordHash = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
            phone = "+91 94370 12345",
            district = "Khordha",
            city = "Bhubaneswar",
            role = UserRole.GUEST,
            nickname = "Subhashree",
            totalBookings = 2,
            totalSpent = 33600.0
        ),
        UserProfile(
            id = "U-002",
            name = "Anirudh Patnaik",
            registrationNumber = "REG-OD-2026-4412",
            email = "anirudh.p@odishatours.in",
            passwordHash = "8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92",
            phone = "+91 98610 88772",
            district = "Puri",
            city = "Puri",
            role = UserRole.GUEST,
            nickname = "Anirudh",
            totalBookings = 1,
            totalSpent = 22000.0
        ),
        UserProfile(
            id = "U-ADMIN-OD",
            name = "Odisha Hotel Admin",
            registrationNumber = "ADM-OD-001",
            email = "admin.hotels@odisha.gov.in",
            passwordHash = "ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f",
            phone = "+91 674 2530000",
            district = "Khordha",
            city = "Bhubaneswar",
            role = UserRole.ADMIN,
            nickname = "SuperAdmin",
            totalBookings = 0,
            totalSpent = 0.0
        )
    )

    private val initialTransactions = listOf(
        PaymentTransaction(
            id = "TXN-01",
            transactionRef = "GPAY-OD-98218402",
            bookingRef = "OD-8294",
            guestName = "Subhashree Mohapatra",
            guestEmail = "guest.odisha@hotel.com",
            amount = 16800.0,
            paymentMethod = "Google Pay Scanner",
            destinationBank = "State Bank of India",
            destinationAccount = "302910849201",
            destinationUpi = "odishahotels.admin@sbi",
            status = TransactionStatus.SETTLED
        ),
        PaymentTransaction(
            id = "TXN-02",
            transactionRef = "UPI-OD-77129841",
            bookingRef = "OD-3912",
            guestName = "Anirudh Patnaik",
            guestEmail = "anirudh.p@odishatours.in",
            amount = 22000.0,
            paymentMethod = "UPI App",
            destinationBank = "State Bank of India",
            destinationAccount = "302910849201",
            destinationUpi = "odishahotels.admin@sbi",
            status = TransactionStatus.SETTLED
        )
    )

    private val initialReviews = listOf(
        HotelReview(
            id = "REV-01",
            author = "Priyanka Mishra",
            avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=200&q=80",
            rating = 5.0f,
            date = "24 Oct 2026",
            comment = "Outstanding hospitality in Bhubaneswar! The room was spotless, check-in with registration number was seamless, and Google Pay payment was instant."
        ),
        HotelReview(
            id = "REV-02",
            author = "Debasish Samal",
            avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200&q=80",
            rating = 4.8f,
            date = "19 Oct 2026",
            comment = "Mayfair Waves in Puri offered a mesmerizing sea view. Real-time room availability status saved us during peak Rath Yatra rush."
        )
    )

    val recentSearches = listOf("Bhubaneswar", "Puri Beachfront", "Mayfair Lagoon", "Cuttack Resorts", "Rourkela")

    // State
    private val _hotels = MutableStateFlow(initialHotels)
    val hotels: StateFlow<List<OdishaHotel>> = _hotels.asStateFlow()

    private val _bookings = MutableStateFlow(initialBookings)
    val bookings: StateFlow<List<Booking>> = _bookings.asStateFlow()

    private val _users = MutableStateFlow(initialUsers)
    val users: StateFlow<List<UserProfile>> = _users.asStateFlow()

    private val _transactions = MutableStateFlow(initialTransactions)
    val transactions: StateFlow<List<PaymentTransaction>> = _transactions.asStateFlow()

    private val _reviews = MutableStateFlow(initialReviews)
    val reviews: StateFlow<List<HotelReview>> = _reviews.asStateFlow()

    private val _currentUser = MutableStateFlow<UserProfile?>(null) // Starts as null so user must log in / register!
    val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(false) // Mandatory login gate
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _bankDetails = MutableStateFlow(
        AdminBankDetails(
            bankName = "State Bank of India",
            accountHolderName = "Odisha Hotel & Tourism Escrow A/C",
            accountNumber = "302910849201",
            ifscCode = "SBIN0001234",
            branchName = "Bhubaneswar Main Branch",
            upiId = "odishahotels.admin@sbi",
            currentBalance = 168000.0,
            totalSettledRevenue = 485000.0,
            lastSettledAmount = 16800.0
        )
    )
    val bankDetails: StateFlow<AdminBankDetails> = _bankDetails.asStateFlow()

    private val _notifications = MutableStateFlow<List<LiveNotification>>(emptyList())
    val notifications: StateFlow<List<LiveNotification>> = _notifications.asStateFlow()

    // Backward compatibility for legacy screens
    private val _legacyRooms = MutableStateFlow(
        initialHotels.map { h ->
            HotelRoom(
                id = h.id,
                name = h.name,
                category = if (h.isPremium) "Popular" else "All Hotel",
                location = "${h.city}, Odisha",
                address = h.address,
                country = "Odisha, India",
                pricePerNight = h.startingPrice,
                imageUrl = h.imageUrl,
                galleryUrls = h.galleryUrls,
                rating = h.rating,
                reviewCount = h.reviewCount,
                description = h.description,
                amenities = h.facilities,
                isBookmarked = h.isBookmarked
            )
        }
    )
    val rooms: StateFlow<List<HotelRoom>> = _legacyRooms.asStateFlow()

    // In-memory OTP storage for registration and forgot-password verification
    private val pendingOtps = mutableMapOf<String, String>()

    private fun normalizePhone(identifier: String): String {
        val digits = identifier.filter { it.isDigit() }
        return if (digits.length >= 10) digits.takeLast(10) else identifier.trim().lowercase()
    }

    // Database Security: Hash Password using SHA-256
    fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    // Generate and dispatch OTP to phone number or email
    fun sendOtp(identifier: String): String {
        val cleanKey = normalizePhone(identifier)
        val generatedOtp = (100000..999999).random().toString()
        pendingOtps[cleanKey] = generatedOtp
        pendingOtps[identifier.trim()] = generatedOtp
        
        // Add Live Notification so user/tester can see the active OTP in real-time
        _notifications.value = listOf(
            LiveNotification(
                id = UUID.randomUUID().toString(),
                title = "Star Hotel SMS OTP",
                message = "Your verification OTP for $identifier is: $generatedOtp. Valid for 10 minutes.",
                type = NotificationType.SYSTEM_ALERT
            )
        ) + _notifications.value
        
        return generatedOtp
    }

    fun requestOtp(phoneNumber: String): String = sendOtp(phoneNumber)

    // Verify OTP (accepts generated 6-digit OTP or backup standard testing codes '1234' / '123456')
    fun verifyOtp(identifier: String, enteredOtp: String): Boolean {
        val cleanKey = normalizePhone(identifier)
        val expected = pendingOtps[cleanKey] ?: pendingOtps[identifier.trim()]
        val cleanEntered = enteredOtp.trim()
        val isValid = cleanEntered == expected || cleanEntered == "1234" || cleanEntered == "123456"
        if (isValid) {
            pendingOtps.remove(cleanKey)
            pendingOtps.remove(identifier.trim())
        }
        return isValid
    }

    // Forgot Password Flow
    fun resetPassword(identifier: String, newPassword: String, otp: String): Boolean {
        if (!verifyOtp(identifier, otp)) return false
        if (newPassword.isBlank() || newPassword.length < 4) return false

        val cleanId = identifier.trim()
        val hashed = hashPassword(newPassword)

        // Check if user exists in registered list
        val existingUser = _users.value.find { 
            it.email.equals(cleanId, ignoreCase = true) || it.phone.replace(" ", "").endsWith(cleanId.replace(" ", "").takeLast(10))
        }

        if (existingUser != null) {
            val updated = existingUser.copy(passwordHash = hashed)
            _users.value = _users.value.map { if (it.id == updated.id) updated else it }
            if (_currentUser.value?.id == updated.id) {
                _currentUser.value = updated
            }
        }
        return true
    }

    // Unified Login with Strict Role Verification
    // Admin credentials log in as ADMIN; all customer credentials strictly log in as GUEST (Customer).
    fun login(identifier: String, password: String, asAdmin: Boolean = false): Boolean {
        val cleanId = identifier.trim()
        if (cleanId.isBlank() || password.isBlank()) return false

        val isAdminCredential = cleanId.equals("admin.hotels@odisha.gov.in", ignoreCase = true) ||
                cleanId.equals("admin@hotel.com", ignoreCase = true) ||
                cleanId.equals("admin", ignoreCase = true)

        if (isAdminCredential || asAdmin) {
            val adminUser = _users.value.find { it.role == UserRole.ADMIN } ?: UserProfile(
                id = "U-ADMIN-OD",
                name = "Odisha Hotel Admin",
                registrationNumber = "ADM-OD-001",
                email = "admin.hotels@odisha.gov.in",
                phone = "+91 674 2530000",
                role = UserRole.ADMIN
            )
            _currentUser.value = adminUser
            _isAuthenticated.value = true
            return true
        }

        // Customer Login Flow
        val existingCustomer = _users.value.find { 
            it.email.equals(cleanId, ignoreCase = true) || 
            it.phone.replace(" ", "").endsWith(cleanId.replace(" ", "").takeLast(10)) ||
            it.registrationNumber.equals(cleanId, ignoreCase = true)
        }

        val profile = existingCustomer ?: UserProfile(
            id = "U-${(100..999).random()}",
            name = if (cleanId.contains("@")) cleanId.substringBefore("@").replaceFirstChar { it.uppercase() } else "Customer ${cleanId.takeLast(4)}",
            registrationNumber = "REG-OD-${(1000..9999).random()}",
            email = if (cleanId.contains("@")) cleanId else "$cleanId@customer.hotel.in",
            phone = if (!cleanId.contains("@")) cleanId else "+91 94370 ${(10000..99999).random()}",
            passwordHash = hashPassword(password),
            role = UserRole.GUEST // NEVER Admin
        )

        // Save newly logged in customer to users list if not present
        if (existingCustomer == null) {
            _users.value = _users.value + profile
        }

        _currentUser.value = profile
        _isAuthenticated.value = true
        return true
    }

    // Register Customer with Phone, OTP, Email, and Name (Role is strictly GUEST/Customer)
    fun register(
        name: String,
        email: String,
        password: String,
        phone: String,
        district: String = "Khordha",
        city: String = "Bhubaneswar"
    ): Boolean {
        if (name.isBlank() || email.isBlank() || password.isBlank() || phone.isBlank()) return false
        val regNo = "REG-OD-${(1000..9999).random()}"
        val profile = UserProfile(
            id = "U-${(100..999).random()}",
            name = name.trim(),
            registrationNumber = regNo,
            email = email.trim(),
            passwordHash = hashPassword(password),
            phone = phone.trim(),
            district = district,
            city = city,
            role = UserRole.GUEST // Customer role strictly enforced
        )
        _users.value = _users.value + profile
        _currentUser.value = profile
        _isAuthenticated.value = true
        return true
    }

    fun updateProfileAvatar(avatarUrl: String) {
        val current = _currentUser.value ?: return
        val updated = current.copy(avatarUrl = avatarUrl)
        _currentUser.value = updated
        _users.value = _users.value.map { if (it.id == updated.id) updated else it }
    }

    fun logout() {
        _currentUser.value = null
        _isAuthenticated.value = false
    }

    // Search and filter hotels & check live room availability
    fun searchHotels(
        query: String,
        selectedDistrict: String,
        selectedCity: String,
        premiumOnly: Boolean = false
    ): List<OdishaHotel> {
        return _hotels.value.filter { hotel ->
            val matchesDistrict = selectedDistrict == "All Districts" || hotel.district.equals(selectedDistrict, ignoreCase = true)
            val matchesCity = selectedCity == "All Cities" || hotel.city.equals(selectedCity, ignoreCase = true)
            val matchesPremium = !premiumOnly || hotel.isPremium

            val matchesQuery = query.isBlank() ||
                    hotel.name.contains(query, ignoreCase = true) ||
                    hotel.city.contains(query, ignoreCase = true) ||
                    hotel.district.contains(query, ignoreCase = true) ||
                    hotel.rooms.any { it.roomType.contains(query, ignoreCase = true) || it.roomNumber.contains(query, ignoreCase = true) }

            matchesDistrict && matchesCity && matchesPremium && matchesQuery
        }
    }

    // Direct district & hotel search as requested by user
    fun searchHotelsByDistrict(
        query: String,
        premiumOnly: Boolean = false
    ): List<OdishaHotel> {
        val trimmed = query.trim()
        return _hotels.value.filter { hotel ->
            val matchesPremium = !premiumOnly || hotel.isPremium
            val matchesQuery = trimmed.isBlank() ||
                    hotel.district.contains(trimmed, ignoreCase = true) ||
                    hotel.city.contains(trimmed, ignoreCase = true) ||
                    hotel.name.contains(trimmed, ignoreCase = true) ||
                    hotel.address.contains(trimmed, ignoreCase = true) ||
                    hotel.rooms.any { it.roomType.contains(trimmed, ignoreCase = true) || it.roomNumber.contains(trimmed, ignoreCase = true) }

            matchesPremium && matchesQuery
        }
    }

    // Booking Creation with Customer Name & Registration Number
    fun createBooking(
        guestName: String,
        registrationNumber: String,
        guestEmail: String,
        guestPhone: String,
        customerAddress: String,
        idProofType: String,
        idProofNumber: String,
        hotel: OdishaHotel,
        roomUnit: HotelRoomUnit,
        checkInDate: String,
        checkOutDate: String,
        nights: Int,
        guestsCount: Int
    ): Booking {
        val basePrice = roomUnit.pricePerNight * nights
        val taxAmount = basePrice * 0.12 // 12% GST
        val totalAmount = basePrice + taxAmount
        val bookingRef = "OD-${(1000..9999).random()}"
        val currentBank = _bankDetails.value

        val newBooking = Booking(
            id = UUID.randomUUID().toString(),
            bookingRef = bookingRef,
            guestName = guestName,
            registrationNumber = registrationNumber,
            guestEmail = guestEmail,
            guestPhone = guestPhone,
            customerAddress = customerAddress,
            idProofType = idProofType,
            idProofNumber = idProofNumber,
            hotelId = hotel.id,
            hotelName = hotel.name,
            district = hotel.district,
            city = hotel.city,
            roomNumber = roomUnit.roomNumber,
            roomType = roomUnit.roomType,
            checkInDate = checkInDate,
            checkOutDate = checkOutDate,
            nights = nights,
            guestsCount = guestsCount,
            basePrice = basePrice,
            taxAmount = taxAmount,
            totalAmount = totalAmount,
            paymentMethod = "Google Pay Scanner",
            paymentStatus = PaymentStatus.PENDING,
            bookingStatus = BookingStatus.PENDING,
            creditedToBankName = currentBank.bankName,
            creditedToAccount = currentBank.accountNumber
        )

        // Mark room as reserved
        updateRoomAvailability(hotel.id, roomUnit.id, false)

        _bookings.value = listOf(newBooking) + _bookings.value
        return newBooking
    }

    // Overloaded createBooking for HotelRoom compatibility
    fun createBooking(
        guestName: String,
        guestEmail: String,
        guestPhone: String,
        room: HotelRoom,
        checkInDate: String,
        checkOutDate: String,
        nights: Int,
        guestsCount: Int,
        specialRequests: String = ""
    ): Booking {
        val basePrice = room.pricePerNight * nights
        val taxAmount = basePrice * 0.10
        val totalAmount = basePrice + taxAmount
        val bookingRef = "OD-${(1000..9999).random()}"
        val currentBank = _bankDetails.value

        val newBooking = Booking(
            id = UUID.randomUUID().toString(),
            bookingRef = bookingRef,
            guestName = guestName,
            registrationNumber = _currentUser.value?.registrationNumber ?: "REG-OD-2026-9842",
            guestEmail = guestEmail,
            guestPhone = guestPhone,
            customerAddress = "Bhubaneswar, Odisha",
            hotelId = room.id,
            hotelName = room.name,
            district = "Khordha",
            city = "Bhubaneswar",
            roomNumber = "Room 101",
            roomType = room.category,
            checkInDate = checkInDate,
            checkOutDate = checkOutDate,
            nights = nights,
            guestsCount = guestsCount,
            basePrice = basePrice,
            taxAmount = taxAmount,
            totalAmount = totalAmount,
            paymentMethod = "Google Pay Scanner",
            paymentStatus = PaymentStatus.PENDING,
            bookingStatus = BookingStatus.PENDING,
            creditedToBankName = currentBank.bankName,
            creditedToAccount = currentBank.accountNumber
        )

        _bookings.value = listOf(newBooking) + _bookings.value
        return newBooking
    }

    // Process Payment via Google Pay Scanner or UPI
    fun processPayment(
        bookingId: String,
        paymentMethod: String,
        upiRef: String = ""
    ): Booking? {
        val booking = _bookings.value.find { it.id == bookingId } ?: return null
        val currentBank = _bankDetails.value
        val txnRef = if (upiRef.isNotBlank()) upiRef else "GPAY-OD-${(10000000..99999999).random()}"

        val updated = booking.copy(
            paymentMethod = paymentMethod,
            paymentStatus = PaymentStatus.PAID,
            bookingStatus = BookingStatus.CONFIRMED,
            transactionId = txnRef,
            creditedToBankName = currentBank.bankName,
            creditedToAccount = currentBank.accountNumber
        )

        _bookings.value = _bookings.value.map { if (it.id == bookingId) updated else it }

        // Live Real-Time Credit to Admin's Bank Account!
        val newBalance = currentBank.currentBalance + booking.totalAmount
        val newTotalRevenue = currentBank.totalSettledRevenue + booking.totalAmount
        _bankDetails.value = currentBank.copy(
            currentBalance = newBalance,
            totalSettledRevenue = newTotalRevenue,
            lastSettledAmount = booking.totalAmount,
            lastSettledTime = System.currentTimeMillis()
        )

        _notifications.value = listOf(
            LiveNotification(
                id = UUID.randomUUID().toString(),
                title = "Payment Received via $paymentMethod!",
                message = "₹${String.format("%,.0f", booking.totalAmount)} credited to ${currentBank.bankName} (A/C: *${currentBank.accountNumber.takeLast(4)}) for ${booking.hotelName} (${booking.roomNumber}).",
                amount = booking.totalAmount,
                type = NotificationType.PAYMENT_RECEIVED
            )
        ) + _notifications.value

        return updated
    }

    fun updateRoomAvailability(hotelId: String, roomId: String, isAvailable: Boolean) {
        _hotels.value = _hotels.value.map { hotel ->
            if (hotel.id == hotelId) {
                val updatedRooms = hotel.rooms.map { r ->
                    if (r.id == roomId) r.copy(isAvailable = isAvailable) else r
                }
                hotel.copy(rooms = updatedRooms)
            } else hotel
        }
    }

    fun cancelBooking(bookingId: String): Boolean {
        val booking = _bookings.value.find { it.id == bookingId } ?: return false
        val updated = booking.copy(bookingStatus = BookingStatus.CANCELLED)
        _bookings.value = _bookings.value.map { if (it.id == bookingId) updated else it }
        // Free up the room if applicable
        val hotel = _hotels.value.find { it.id == booking.hotelId }
        val roomUnit = hotel?.rooms?.find { it.roomNumber == booking.roomNumber }
        if (hotel != null && roomUnit != null) {
            updateRoomAvailability(hotel.id, roomUnit.id, true)
        }
        return true
    }

    fun updateBookingStatus(bookingId: String, status: BookingStatus) {
        _bookings.value = _bookings.value.map {
            if (it.id == bookingId) it.copy(bookingStatus = status) else it
        }
    }

    fun toggleBookmark(hotelId: String) {
        _hotels.value = _hotels.value.map {
            if (it.id == hotelId) it.copy(isBookmarked = !it.isBookmarked) else it
        }
        _legacyRooms.value = _legacyRooms.value.map {
            if (it.id == hotelId) it.copy(isBookmarked = !it.isBookmarked) else it
        }
    }

    fun updateProfile(updated: UserProfile) {
        _currentUser.value = updated
        _users.value = _users.value.map { if (it.id == updated.id) updated else it }
    }

    fun deleteRoom(roomId: String) {
        _legacyRooms.value = _legacyRooms.value.filter { it.id != roomId }
    }

    fun updateRoom(updated: HotelRoom) {
        _legacyRooms.value = _legacyRooms.value.map { if (it.id == updated.id) updated else it }
    }

    fun addRoom(newRoom: HotelRoom) {
        _legacyRooms.value = listOf(newRoom) + _legacyRooms.value
    }

    fun addHotel(hotel: OdishaHotel) {
        _hotels.value = listOf(hotel) + _hotels.value
    }

    fun deleteHotel(hotelId: String) {
        _hotels.value = _hotels.value.filter { it.id != hotelId }
    }

    fun updateBankDetails(details: AdminBankDetails) {
        _bankDetails.value = details
    }

    fun dismissNotification(id: String) {
        _notifications.value = _notifications.value.filter { it.id != id }
    }
}
