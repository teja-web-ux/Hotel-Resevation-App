package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.HotelRepository
import com.example.model.Booking
import com.example.model.BookingStatus
import com.example.ui.components.PaymentStatusBadge
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminBookingsScreen(
    onBack: () -> Unit
) {
    val bookings by HotelRepository.bookings.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf<BookingStatus?>(null) }
    var activeBookingForAction by remember { mutableStateOf<Booking?>(null) }

    val filteredBookings = remember(bookings, searchQuery, selectedStatus) {
        bookings.filter { b ->
            val matchesSearch = searchQuery.isBlank() ||
                    b.bookingRef.contains(searchQuery, ignoreCase = true) ||
                    b.guestName.contains(searchQuery, ignoreCase = true) ||
                    b.roomName.contains(searchQuery, ignoreCase = true)
            val matchesStatus = selectedStatus == null || b.bookingStatus == selectedStatus
            matchesSearch && matchesStatus
        }
    }

    Scaffold(
        containerColor = PureWhite,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Manage Bookings",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PureWhite)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .testTag("admin_bookings_screen"),
            contentPadding = PaddingValues(top = 10.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                FormInputItem(
                    label = "Search by ID, Guest name, Hotel...",
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    trailingIcon = Icons.Default.Search
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedStatus == null,
                        onClick = { selectedStatus = null },
                        label = { Text("All (${bookings.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldPrimary,
                            selectedLabelColor = PureWhite
                        )
                    )
                    FilterChip(
                        selected = selectedStatus == BookingStatus.CONFIRMED,
                        onClick = { selectedStatus = BookingStatus.CONFIRMED },
                        label = { Text("Confirmed") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldPrimary,
                            selectedLabelColor = PureWhite
                        )
                    )
                    FilterChip(
                        selected = selectedStatus == BookingStatus.PENDING,
                        onClick = { selectedStatus = BookingStatus.PENDING },
                        label = { Text("Pending") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldPrimary,
                            selectedLabelColor = PureWhite
                        )
                    )
                }
            }

            items(filteredBookings) { booking ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, BorderLight, RoundedCornerShape(16.dp)),
                    color = PureWhite,
                    shadowElevation = 1.dp,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(booking.bookingRef, style = MaterialTheme.typography.titleMedium, color = EmeraldDark, fontWeight = FontWeight.Bold)
                                Text(booking.guestName, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                                Text(booking.guestPhone, style = MaterialTheme.typography.bodySmall, color = TextMuted)
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                StatusBadge(status = booking.bookingStatus)
                                Spacer(modifier = Modifier.height(4.dp))
                                PaymentStatusBadge(status = booking.paymentStatus)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = BorderLight)
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "${booking.roomName} • ${booking.nights} Nights (${booking.checkInDate} to ${booking.checkOutDate})",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )

                        if (booking.creditedToBankName.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Settled to: ${booking.creditedToBankName} (*${booking.creditedToAccount.takeLast(4)})",
                                style = MaterialTheme.typography.labelSmall,
                                color = EmeraldDark,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$${String.format("%.2f", booking.totalAmount)}",
                                style = MaterialTheme.typography.titleLarge,
                                color = EmeraldPrimary,
                                fontWeight = FontWeight.Bold
                            )

                            Button(
                                onClick = { activeBookingForAction = booking },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldContainer, contentColor = EmeraldDark),
                                shape = RoundedCornerShape(50)
                            ) {
                                Text("Change Status", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Status changer modal
        activeBookingForAction?.let { b ->
            AlertDialog(
                onDismissRequest = { activeBookingForAction = null },
                containerColor = PureWhite,
                shape = RoundedCornerShape(20.dp),
                title = {
                    Text("Update Status ${b.bookingRef}", color = TextPrimary, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Guest: ${b.guestName}", color = TextSecondary)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    HotelRepository.updateBookingStatus(b.id, BookingStatus.CONFIRMED)
                                    activeBookingForAction = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                            ) {
                                Text("Confirm", color = PureWhite)
                            }
                            Button(
                                onClick = {
                                    HotelRepository.updateBookingStatus(b.id, BookingStatus.COMPLETED)
                                    activeBookingForAction = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark)
                            ) {
                                Text("Complete", color = PureWhite)
                            }
                            Button(
                                onClick = {
                                    HotelRepository.cancelBooking(b.id)
                                    activeBookingForAction = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = RedCancel)
                            ) {
                                Text("Cancel", color = PureWhite)
                            }
                        }
                    }
                },
                confirmButton = { }
            )
        }
    }
}
