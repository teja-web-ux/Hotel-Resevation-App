package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.model.Booking
import com.example.model.BookingStatus
import com.example.ui.components.EmeraldPillButton
import com.example.ui.components.StarRatingBadge
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBookingsScreen(
    onViewTicket: (Booking) -> Unit
) {
    val bookings by HotelRepository.bookings.collectAsState()
    var selectedTab by remember { mutableStateOf("Ongoing") } // "Ongoing", "Completed", "Canceled"

    var bookingToCancel by remember { mutableStateOf<Booking?>(null) }
    var showRefundModal by remember { mutableStateOf(false) }
    var showCancelSuccessModal by remember { mutableStateOf(false) }
    var selectedRefundMethod by remember { mutableStateOf("Google Pay") }

    val filteredBookings = remember(bookings, selectedTab) {
        when (selectedTab) {
            "Ongoing" -> bookings.filter { it.bookingStatus == BookingStatus.CONFIRMED || it.bookingStatus == BookingStatus.PENDING }
            "Completed" -> bookings.filter { it.bookingStatus == BookingStatus.COMPLETED }
            "Canceled" -> bookings.filter { it.bookingStatus == BookingStatus.CANCELLED }
            else -> bookings
        }
    }

    Scaffold(
        containerColor = PureWhite,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Booking",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PureWhite)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Pill Tabs: Ongoing, Completed, Canceled (Matching Screen 16)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf("Ongoing", "Completed", "Canceled").forEach { tab ->
                    val isSelected = selectedTab == tab
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .clip(RoundedCornerShape(50))
                            .border(
                                1.dp,
                                if (isSelected) EmeraldPrimary else BorderMedium,
                                RoundedCornerShape(50)
                            )
                            .clickable { selectedTab = tab },
                        color = if (isSelected) EmeraldPrimary else PureWhite,
                        shape = RoundedCornerShape(50)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = tab,
                                color = if (isSelected) PureWhite else TextSecondary,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bookings List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                contentPadding = PaddingValues(bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredBookings) { booking ->
                    BookingCardItem(
                        booking = booking,
                        selectedTab = selectedTab,
                        onViewTicket = { onViewTicket(booking) },
                        onCancelBooking = { bookingToCancel = booking }
                    )
                }
            }
        }

        // Cancel Confirmation Dialog (Screen 17 in Mockup)
        bookingToCancel?.let { b ->
            AlertDialog(
                onDismissRequest = { bookingToCancel = null },
                containerColor = PureWhite,
                shape = RoundedCornerShape(24.dp),
                title = {
                    Text(
                        text = "Cancel Hotel Booking",
                        color = TextPrimary,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Are you sure you want to cancel your hotel booking?",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Only 80% of the money you can refund from your payment according to our policy",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { bookingToCancel = null },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = EmeraldContainer,
                                    contentColor = EmeraldDark
                                ),
                                shape = RoundedCornerShape(50)
                            ) {
                                Text("Cancel", fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    showRefundModal = true
                                },
                                modifier = Modifier
                                    .weight(1.3f)
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = EmeraldPrimary,
                                    contentColor = PureWhite
                                ),
                                shape = RoundedCornerShape(50)
                            ) {
                                Text("Yes, Continue", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                },
                confirmButton = { }
            )
        }

        // Refund Method Selector Modal (Screen 18 in Mockup)
        if (showRefundModal && bookingToCancel != null) {
            val b = bookingToCancel!!
            val refundAmount = b.totalAmount * 0.80

            AlertDialog(
                onDismissRequest = {
                    showRefundModal = false
                    bookingToCancel = null
                },
                containerColor = PureWhite,
                shape = RoundedCornerShape(24.dp),
                title = {
                    Text("Cancel Hotel Booking", color = TextPrimary, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                },
                text = {
                    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Please select a payment refund method (only 80% will be refunded):",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )

                        listOf("Paypal", "Google Pay", "Apple Pay", "Card **** 4679").forEach { m ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, if (selectedRefundMethod == m) EmeraldPrimary else BorderMedium, RoundedCornerShape(12.dp))
                                    .clickable { selectedRefundMethod = m },
                                shape = RoundedCornerShape(12.dp),
                                color = PureWhite
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = selectedRefundMethod == m,
                                        onClick = { selectedRefundMethod = m },
                                        colors = RadioButtonDefaults.colors(selectedColor = EmeraldPrimary)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(m, color = TextPrimary, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Paid: $${String.format("%.1f", b.totalAmount)}", color = TextMuted, style = MaterialTheme.typography.labelSmall)
                            Text("Refund: $${String.format("%.1f", refundAmount)}", color = EmeraldDark, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        EmeraldPillButton(
                            text = "Confirm Cancellation",
                            onClick = {
                                HotelRepository.cancelBooking(b.id)
                                showRefundModal = false
                                showCancelSuccessModal = true
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = { }
            )
        }

        // Cancel Success Modal (Screen 18 Right)
        if (showCancelSuccessModal) {
            AlertDialog(
                onDismissRequest = {
                    showCancelSuccessModal = false
                    bookingToCancel = null
                },
                containerColor = PureWhite,
                shape = RoundedCornerShape(24.dp),
                title = null,
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(EmeraldPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Check, contentDescription = "Success", tint = PureWhite, modifier = Modifier.size(42.dp))
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Text("Successful!", style = MaterialTheme.typography.headlineSmall, color = TextPrimary, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "You have successfully canceled your order. 80% funds will be returned to your account.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        EmeraldPillButton(
                            text = "OK",
                            onClick = {
                                showCancelSuccessModal = false
                                bookingToCancel = null
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = { }
            )
        }
    }
}

@Composable
private fun BookingCardItem(
    booking: Booking,
    selectedTab: String,
    onViewTicket: () -> Unit,
    onCancelBooking: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, BorderLight, RoundedCornerShape(20.dp)),
        color = PureWhite,
        shadowElevation = 2.dp,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Photo
                val photoUrl = when {
                    booking.roomName.contains("Bulgari", ignoreCase = true) -> "https://images.unsplash.com/photo-1618773928121-c32242e63f39?w=400&q=80"
                    booking.roomName.contains("Palms", ignoreCase = true) -> "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?w=400&q=80"
                    booking.roomName.contains("Monte-Carlo", ignoreCase = true) -> "https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=400&q=80"
                    booking.roomName.contains("Martinez", ignoreCase = true) -> "https://images.unsplash.com/photo-1590490360182-c33d57733427?w=400&q=80"
                    else -> "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=400&q=80"
                }

                AsyncImage(
                    model = photoUrl,
                    contentDescription = booking.roomName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(14.dp))
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (booking.hotelName.isNotBlank()) booking.hotelName else booking.roomName,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Text(
                        text = "${booking.roomNumber} (${booking.roomType})",
                        style = MaterialTheme.typography.bodySmall,
                        color = EmeraldDark,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Reg No: ${booking.registrationNumber}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "${booking.checkInDate} - ${booking.checkOutDate} • ₹${String.format("%,.0f", booking.totalAmount)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Status Pill
                    when (booking.bookingStatus) {
                        BookingStatus.COMPLETED -> {
                            Surface(color = EmeraldContainer, shape = RoundedCornerShape(6.dp)) {
                                Text("Completed", color = EmeraldDark, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                            }
                        }
                        BookingStatus.CANCELLED -> {
                            Surface(color = RedCancelBg, shape = RoundedCornerShape(6.dp)) {
                                Text("Canceled & Refunded", color = RedCancel, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                            }
                        }
                        else -> {
                            Surface(color = EmeraldContainer, shape = RoundedCornerShape(6.dp)) {
                                Text("Paid & Confirmed", color = EmeraldDark, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                            }
                        }
                    }
                }
            }

            // Bottom Buttons for Ongoing Bookings (Matching Screen 16 & 17)
            if (selectedTab == "Ongoing") {
                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = BorderLight)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onCancelBooking,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderMedium),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text("Cancel Booking", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onViewTicket,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text("View Ticket", color = PureWhite, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
