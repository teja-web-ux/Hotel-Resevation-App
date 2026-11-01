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
import com.example.model.HotelRoomUnit
import com.example.model.OdishaHotel
import com.example.ui.components.EmeraldPillButton
import com.example.ui.components.StarRatingBadge
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelRoomsScreen(
    hotel: OdishaHotel,
    onBack: () -> Unit,
    onSelectRoomToBook: (hotel: OdishaHotel, room: HotelRoomUnit) -> Unit
) {
    var selectedRoomForDetails by remember { mutableStateOf<HotelRoomUnit?>(null) }
    var filterAvailableOnly by remember { mutableStateOf(false) }

    val displayedRooms = remember(hotel.rooms, filterAvailableOnly) {
        if (filterAvailableOnly) hotel.rooms.filter { it.isAvailable } else hotel.rooms
    }

    Scaffold(
        containerColor = PureWhite,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = hotel.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Share or bookmark */ }) {
                        Icon(Icons.Outlined.Share, contentDescription = "Share", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PureWhite)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 40.dp)
        ) {
            // Hotel Hero Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                ) {
                    AsyncImage(
                        model = hotel.imageUrl,
                        contentDescription = hotel.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // District & City Overlay Tag
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp),
                        color = TextPrimary.copy(alpha = 0.85f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${hotel.city}, ${hotel.district} (Odisha)",
                                color = PureWhite,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Hotel Info & Overview
            item {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = hotel.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        StarRatingBadge(rating = hotel.rating)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = hotel.address,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = hotel.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Hotel General Facilities
                    Text(
                        text = "Hotel Facilities & Highlights",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(hotel.facilities) { facility ->
                            Surface(
                                color = SurfaceLight,
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(facility, style = MaterialTheme.typography.labelSmall, color = TextPrimary)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Specific Room Availability Header with Live Counts
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Room Inventory & Availability",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Live status: Available (खाली) vs Booked (बुक)",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                        }

                        // Availability Filter Toggle
                        FilterChip(
                            selected = filterAvailableOnly,
                            onClick = { filterAvailableOnly = !filterAvailableOnly },
                            label = {
                                Text(
                                    text = if (filterAvailableOnly) "Only खाली" else "All Rooms",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EmeraldPrimary,
                                selectedLabelColor = PureWhite,
                                containerColor = SurfaceLight,
                                labelColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                    }
                }
            }

            // Room Units List with Detail & Booking Options
            items(displayedRooms) { roomUnit ->
                RoomUnitCard(
                    room = roomUnit,
                    onViewDetails = { selectedRoomForDetails = roomUnit },
                    onBookNow = { onSelectRoomToBook(hotel, roomUnit) },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }
        }
    }

    // "व्यू ऑप्शन" - ROOM DETAILS MODAL DIALOG
    // Displays full amenities checklist ("क्या-क्या अवेलेबल है उस रूम पर"), specifications, and book action.
    selectedRoomForDetails?.let { room ->
        AlertDialog(
            onDismissRequest = { selectedRoomForDetails = null },
            containerColor = PureWhite,
            shape = RoundedCornerShape(24.dp),
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = room.roomNumber,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = room.roomType,
                            style = MaterialTheme.typography.bodyMedium,
                            color = EmeraldDark,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Surface(
                        color = if (room.isAvailable) EmeraldLight else RedCancelBg,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (room.isAvailable) "खाली / AVAILABLE" else "बुक / OCCUPIED",
                            color = if (room.isAvailable) EmeraldDark else RedCancel,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Pricing & specs box
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = SurfaceLight,
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Price / Night", fontSize = 11.sp, color = TextMuted)
                                Text("₹${String.format("%,.0f", room.pricePerNight)}", fontWeight = FontWeight.Bold, color = EmeraldPrimary, fontSize = 15.sp)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Room Size", fontSize = 11.sp, color = TextMuted)
                                Text("${room.sizeSqFt} sq.ft", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Floor", fontSize = 11.sp, color = TextMuted)
                                Text("Floor ${room.floor}", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Max Guests", fontSize = 11.sp, color = TextMuted)
                                Text("${room.maxGuests} Guests", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                            }
                        }
                    }

                    // "क्या-क्या अवेलेबल है उस रूम पर" (Included Room Amenities)
                    Text(
                        text = "Included Room Amenities & Features:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    val amenitiesList = if (room.amenities.isNotEmpty()) {
                        room.amenities
                    } else {
                        listOf(
                            "Air Conditioning (AC)",
                            "High-Speed Free Wi-Fi",
                            "Smart 4K LED TV with OTT",
                            "Private Balcony / Scenic View",
                            "Attached Bathroom with 24/7 Geyser",
                            "Mini Refrigerator & Kettle",
                            "Complimentary Odia Breakfast",
                            "24/7 Room Service & Housekeeping"
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        amenitiesList.forEach { amenity ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = amenity,
                                    fontSize = 12.sp,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                if (room.isAvailable) {
                    Button(
                        onClick = {
                            selectedRoomForDetails = null
                            onSelectRoomToBook(hotel, room)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.ConfirmationNumber, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Book This Room", fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedRoomForDetails = null }) {
                    Text("Close", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
fun RoomUnitCard(
    room: HotelRoomUnit,
    onViewDetails: () -> Unit,
    onBookNow: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(
                1.5.dp,
                if (room.isAvailable) EmeraldPrimary.copy(alpha = 0.35f) else BorderMedium,
                RoundedCornerShape(18.dp)
            )
            .testTag("room_unit_${room.roomNumber.replace(" ", "_")}"),
        color = if (room.isAvailable) PureWhite else SurfaceLight,
        shadowElevation = if (room.isAvailable) 2.dp else 0.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Room Title and Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = room.roomNumber,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (room.isAvailable) TextPrimary else TextMuted
                    )
                    Text(
                        text = room.roomType,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (room.isAvailable) EmeraldDark else TextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Live Availability Status Badge (खाली vs बुक)
                Surface(
                    color = if (room.isAvailable) EmeraldLight else RedCancelBg,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (room.isAvailable) EmeraldPrimary else RedCancel)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (room.isAvailable) "खाली (AVAILABLE)" else "बुक (BOOKED)",
                            color = if (room.isAvailable) EmeraldDark else RedCancel,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Room Spec Tags
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("• ${room.bedType}", style = MaterialTheme.typography.bodySmall, color = TextSecondary, fontSize = 12.sp)
                Text("• ${room.sizeSqFt} sq.ft", style = MaterialTheme.typography.bodySmall, color = TextSecondary, fontSize = 12.sp)
                Text("• Floor ${room.floor}", style = MaterialTheme.typography.bodySmall, color = TextSecondary, fontSize = 12.sp)
                Text("• Max ${room.maxGuests} Guests", style = MaterialTheme.typography.bodySmall, color = TextSecondary, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // "क्या-क्या अवेलेबल है उस रूम पर" (Key Amenities Chips)
            Text(
                text = "Room Amenities:",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            val displayAmenities = if (room.amenities.isNotEmpty()) {
                room.amenities.take(4)
            } else {
                listOf("Air Conditioning", "Free Wi-Fi", "Smart TV", "Room Service")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                displayAmenities.forEach { amenity ->
                    Surface(
                        color = SurfaceLight,
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                    ) {
                        Text(
                            text = amenity,
                            fontSize = 10.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Price and Dual Action Buttons: View Details & Book Room
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "₹${String.format("%,.0f", room.pricePerNight)}",
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

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // "व्यू ऑप्शन" (View Details Button)
                    OutlinedButton(
                        onClick = onViewDetails,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderMedium),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Outlined.Info, contentDescription = null, modifier = Modifier.size(15.dp), tint = EmeraldDark)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("View Details", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }

                    // "बुकिंग ऑप्शन" (Book Room Button)
                    Button(
                        onClick = onBookNow,
                        enabled = room.isAvailable,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldPrimary,
                            contentColor = PureWhite,
                            disabledContainerColor = BorderLight,
                            disabledContentColor = TextMuted
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("book_room_btn_${room.roomNumber.replace(" ", "_")}")
                    ) {
                        Icon(
                            imageVector = if (room.isAvailable) Icons.Default.CheckCircle else Icons.Default.Block,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (room.isAvailable) "Book Room" else "Not Available",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
