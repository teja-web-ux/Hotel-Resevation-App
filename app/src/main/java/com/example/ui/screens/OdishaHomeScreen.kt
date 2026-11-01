package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.HotelRepository
import com.example.model.HotelRoomUnit
import com.example.model.OdishaHotel
import com.example.model.UserRole
import com.example.ui.components.StarRatingBadge
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OdishaHomeScreen(
    onSelectHotel: (OdishaHotel) -> Unit,
    onSelectSpecificRoom: (hotel: OdishaHotel, room: HotelRoomUnit) -> Unit,
    onOpenAdmin: () -> Unit
) {
    val hotels by HotelRepository.hotels.collectAsState()
    val currentUser by HotelRepository.currentUser.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var premiumOnly by remember { mutableStateOf(false) }

    // Quick District Search suggestions (single line, tap to search that district directly)
    val popularDistricts = listOf("All Odisha", "Puri", "Khordha", "Cuttack", "Ganjam", "Sundargarh", "Sambalpur", "Balasore")

    // Filter hotels directly by district or hotel name as requested
    val filteredHotels = remember(hotels, searchQuery, premiumOnly) {
        HotelRepository.searchHotelsByDistrict(
            query = if (searchQuery == "All Odisha") "" else searchQuery,
            premiumOnly = premiumOnly
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PureWhite)
            .testTag("odisha_home_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top Header: User Profile
            // ZERO Admin button for regular Customer (only visible if logged in as Admin)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, EmeraldPrimary, CircleShape)
                        ) {
                            AsyncImage(
                                model = currentUser?.avatarUrl ?: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400&q=80",
                                contentDescription = "Profile Avatar",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Namaskar, ${currentUser?.name?.substringBefore(" ") ?: "Guest"}!",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Reg: ${currentUser?.registrationNumber ?: "REG-OD-..."}",
                                style = MaterialTheme.typography.labelSmall,
                                color = EmeraldDark,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // STRICT ROLE SEGREGATION: Admin button is ONLY shown if user is ADMIN.
                    // Customer NEVER sees any admin elements!
                    if (currentUser?.role == UserRole.ADMIN) {
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { onOpenAdmin() }
                                .testTag("home_admin_btn"),
                            color = EmeraldPrimary,
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.AdminPanelSettings,
                                    contentDescription = "Admin Portal",
                                    tint = PureWhite,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Admin Portal",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = PureWhite
                                )
                            }
                        }
                    }
                }
            }

            // Streamlined District & Hotel Search Bar
            // "सर्च करने पर वही आएगा कि उस डिस्ट्रिक्ट का क्या-क्या होटल है। वह लाइन पर सर्च करने में आ जाएगा। डिस्ट्रिक्ट सर्च करने में आ जाएगा और ऑल होटल इन दैट डिस्ट्रिक्ट।"
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, BorderMedium, RoundedCornerShape(16.dp)),
                        color = SurfaceLight,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search",
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            TextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = {
                                    Text(
                                        "Search district (e.g. Puri, Khordha) or hotel...",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextMuted,
                                        fontSize = 13.sp
                                    )
                                },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    cursorColor = EmeraldPrimary
                                ),
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("search_hotel_input")
                            )
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick District Tap Line (Directly searches that district on line)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(popularDistricts) { district ->
                            val isSelected = (district == "All Odisha" && searchQuery.isBlank()) ||
                                    searchQuery.equals(district, ignoreCase = true)
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable {
                                        searchQuery = if (district == "All Odisha") "" else district
                                    },
                                color = if (isSelected) EmeraldPrimary else SurfaceLight,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) EmeraldPrimary else BorderLight
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (isSelected && district != "All Odisha") {
                                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = PureWhite, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }
                                    Text(
                                        text = district,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) PureWhite else TextPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Results Header with Live Count & 5-Star Filter
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (searchQuery.isNotBlank() && searchQuery != "All Odisha")
                                "Hotels in $searchQuery"
                            else
                                "All Odisha Hotels",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${filteredHotels.size} verified hotels found with live rooms",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }

                    // Premium 5-star toggle chip
                    FilterChip(
                        selected = premiumOnly,
                        onClick = { premiumOnly = !premiumOnly },
                        label = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = if (premiumOnly) PureWhite else YellowStar, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("5★ Luxury", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldPrimary,
                            selectedLabelColor = PureWhite,
                            containerColor = SurfaceLight,
                            labelColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.testTag("filter_premium_chip")
                    )
                }
            }

            // Empty State
            if (filteredHotels.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp, bottom = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.HotelClass, contentDescription = null, tint = TextMuted, modifier = Modifier.size(56.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No hotels found in '$searchQuery'", fontWeight = FontWeight.Bold, color = TextPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Try searching Puri, Khordha, Cuttack, Ganjam, or Rourkela", fontSize = 12.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { searchQuery = "" },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text("Show All Odisha Hotels")
                        }
                    }
                }
            }

            // List of Hotels with Live Room Availability Status
            items(filteredHotels) { hotel ->
                OdishaHotelCard(
                    hotel = hotel,
                    onSelectHotel = { onSelectHotel(hotel) },
                    onSelectSpecificRoom = { roomUnit -> onSelectSpecificRoom(hotel, roomUnit) },
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }
    }
}

@Composable
fun OdishaHotelCard(
    hotel: OdishaHotel,
    onSelectHotel: () -> Unit,
    onSelectSpecificRoom: (HotelRoomUnit) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, BorderLight, RoundedCornerShape(20.dp))
            .clickable { onSelectHotel() }
            .testTag("odisha_hotel_card_${hotel.id}"),
        color = PureWhite,
        shadowElevation = 2.dp,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column {
            // Hotel Image & Overlay Tags
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                AsyncImage(
                    model = hotel.imageUrl,
                    contentDescription = hotel.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Premium Badge
                if (hotel.isPremium) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp),
                        color = PureWhite.copy(alpha = 0.95f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = YellowStar, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("5★ Luxury", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = TextPrimary)
                        }
                    }
                }

                // Bookmark icon
                IconButton(
                    onClick = { HotelRepository.toggleBookmark(hotel.id) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Surface(shape = CircleShape, color = PureWhite.copy(alpha = 0.9f)) {
                        Icon(
                            imageVector = if (hotel.isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (hotel.isBookmarked) EmeraldPrimary else TextPrimary,
                            modifier = Modifier
                                .padding(6.dp)
                                .size(20.dp)
                        )
                    }
                }

                // Live Room Availability Badge on top of image
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp),
                    color = if (hotel.availableRoomsCount > 0) EmeraldPrimary else RedCancel,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(PureWhite)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (hotel.availableRoomsCount > 0) "${hotel.availableRoomsCount} Rooms Available (खाली)" else "Sold Out (बुक)",
                            color = PureWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Card Body
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = hotel.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    StarRatingBadge(rating = hotel.rating)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${hotel.city}, ${hotel.district} (Odisha)",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // List of Available Rooms in this Hotel
                Text(
                    text = "Live Room Inventory:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    hotel.rooms.take(3).forEach { roomUnit ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (roomUnit.isAvailable) EmeraldLight else SurfaceLight)
                                .clickable(enabled = roomUnit.isAvailable) { onSelectSpecificRoom(roomUnit) }
                                .padding(horizontal = 10.dp, vertical = 7.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${roomUnit.roomNumber} - ${roomUnit.roomType}",
                                fontSize = 12.sp,
                                fontWeight = if (roomUnit.isAvailable) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (roomUnit.isAvailable) TextPrimary else TextMuted
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (roomUnit.isAvailable) "Available ₹${String.format("%,.0f", roomUnit.pricePerNight)}" else "Booked (बुक)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (roomUnit.isAvailable) EmeraldDark else RedCancel
                                )
                                if (roomUnit.isAvailable) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Price and Full Room List Action Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "₹${String.format("%,.0f", hotel.startingPrice)}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary
                        )
                        Text(
                            text = " /night",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }

                    Button(
                        onClick = onSelectHotel,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Text("View Rooms & Book", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
