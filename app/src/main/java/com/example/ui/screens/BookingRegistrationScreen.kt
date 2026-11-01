package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.HotelRepository
import com.example.model.HotelRoomUnit
import com.example.model.OdishaHotel
import com.example.ui.components.EmeraldPillButton
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingRegistrationScreen(
    hotel: OdishaHotel,
    room: HotelRoomUnit,
    onBack: () -> Unit,
    onProceedToCustomerDetails: (
        guestName: String,
        registrationNumber: String,
        guestEmail: String,
        guestPhone: String,
        checkInDate: String,
        checkOutDate: String,
        nights: Int,
        guestsCount: Int
    ) -> Unit
) {
    val currentUser by HotelRepository.currentUser.collectAsState()

    var guestName by remember { mutableStateOf(currentUser?.name ?: "Subhashree Mohapatra") }
    var registrationNumber by remember { mutableStateOf(currentUser?.registrationNumber ?: "REG-OD-2026-9842") }
    var guestEmail by remember { mutableStateOf(currentUser?.email ?: "guest.odisha@hotel.com") }
    var guestPhone by remember { mutableStateOf(currentUser?.phone ?: "+91 94370 12345") }

    var checkInDay by remember { mutableIntStateOf(10) }
    var checkOutDay by remember { mutableIntStateOf(12) }
    var guestCount by remember { mutableIntStateOf(2) }

    val nights = (checkOutDay - checkInDay).coerceAtLeast(1)
    val checkInDate = "$checkInDay Nov 2026"
    val checkOutDate = "$checkOutDay Nov 2026"

    val basePrice = room.pricePerNight * nights
    val gstTax = basePrice * 0.12 // 12% GST
    val grandTotal = basePrice + gstTax

    var validationError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = PureWhite,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Booking & Registration",
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
        },
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                color = PureWhite,
                shadowElevation = 16.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
            ) {
                Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Total Payable", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                            Text(
                                text = "₹${String.format("%,.0f", grandTotal)}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary
                            )
                        }

                        EmeraldPillButton(
                            text = "Next: Customer Details →",
                            onClick = {
                                if (guestName.isBlank()) {
                                    validationError = "User's name is required for booking."
                                } else if (registrationNumber.isBlank()) {
                                    validationError = "Customer Registration Number is required."
                                } else {
                                    onProceedToCustomerDetails(
                                        guestName.trim(),
                                        registrationNumber.trim(),
                                        guestEmail.trim(),
                                        guestPhone.trim(),
                                        checkInDate,
                                        checkOutDate,
                                        nights,
                                        guestCount
                                    )
                                }
                            },
                            modifier = Modifier.weight(1f).padding(start = 16.dp),
                            testTag = "proceed_to_customer_details_btn"
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hotel & Room Chosen Card
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = SurfaceLight,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(EmeraldLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Hotel, contentDescription = null, tint = EmeraldPrimary)
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = hotel.name,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "${room.roomNumber} • ${room.roomType}",
                                style = MaterialTheme.typography.bodySmall,
                                color = EmeraldDark,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${hotel.city}, ${hotel.district} • ₹${String.format("%,.0f", room.pricePerNight)} / night",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            // Error prompt
            if (validationError != null) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = RedCancelBg,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = validationError ?: "",
                            color = RedCancel,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // User's Name and Registration Number Section (User Requirement)
            item {
                Text(
                    text = "Customer Identification",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Name and Registration Number are mandatory for booking",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }

            item {
                OutlinedTextField(
                    value = guestName,
                    onValueChange = {
                        guestName = it
                        validationError = null
                    },
                    label = { Text("User's Full Name *") },
                    leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null, tint = EmeraldPrimary) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("booking_guest_name_input"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = BorderMedium
                    )
                )
            }

            item {
                OutlinedTextField(
                    value = registrationNumber,
                    onValueChange = {
                        registrationNumber = it
                        validationError = null
                    },
                    label = { Text("Customer Registration Number *") },
                    placeholder = { Text("e.g. REG-OD-2026-9842") },
                    leadingIcon = { Icon(Icons.Outlined.Badge, contentDescription = null, tint = EmeraldPrimary) },
                    supportingText = { Text("Official Odisha hotel customer registration ID") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("booking_registration_number_input"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = BorderMedium
                    )
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = guestEmail,
                        onValueChange = { guestEmail = it },
                        label = { Text("Email") },
                        leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null, tint = EmeraldPrimary) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = BorderMedium
                        )
                    )

                    OutlinedTextField(
                        value = guestPhone,
                        onValueChange = { guestPhone = it },
                        label = { Text("Phone") },
                        leadingIcon = { Icon(Icons.Outlined.Phone, contentDescription = null, tint = EmeraldPrimary) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = BorderMedium
                        )
                    )
                }
            }

            // Dates & Guests Selector
            item {
                Text(
                    text = "Reservation Schedule",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = SurfaceLight,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Check-In Date", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                Text(checkInDate, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 15.sp)
                            }
                            Icon(Icons.Default.ArrowForward, contentDescription = null, tint = EmeraldPrimary)
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Check-Out Date", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                Text(checkOutDate, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 15.sp)
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = BorderLight)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total Stay Duration", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { if (checkOutDay > checkInDay + 1) checkOutDay-- },
                                    enabled = checkOutDay > checkInDay + 1
                                ) {
                                    Icon(Icons.Default.RemoveCircleOutline, contentDescription = "Decrease nights", tint = EmeraldPrimary)
                                }
                                Text("$nights Nights", fontWeight = FontWeight.Bold, color = TextPrimary)
                                IconButton(
                                    onClick = { checkOutDay++ }
                                ) {
                                    Icon(Icons.Default.AddCircleOutline, contentDescription = "Increase nights", tint = EmeraldPrimary)
                                }
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = BorderLight)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Number of Guests", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { if (guestCount > 1) guestCount-- },
                                    enabled = guestCount > 1
                                ) {
                                    Icon(Icons.Default.RemoveCircleOutline, contentDescription = "Decrease guests", tint = EmeraldPrimary)
                                }
                                Text("$guestCount Guests", fontWeight = FontWeight.Bold, color = TextPrimary)
                                IconButton(
                                    onClick = { if (guestCount < room.maxGuests) guestCount++ },
                                    enabled = guestCount < room.maxGuests
                                ) {
                                    Icon(Icons.Default.AddCircleOutline, contentDescription = "Increase guests", tint = EmeraldPrimary)
                                }
                            }
                        }
                    }
                }
            }

            // Price Breakdown
            item {
                Text(
                    text = "Price Summary",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = PureWhite,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("₹${String.format("%,.0f", room.pricePerNight)} x $nights Nights", color = TextSecondary, fontSize = 13.sp)
                            Text("₹${String.format("%,.0f", basePrice)}", fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Taxes & GST (12%)", color = TextSecondary, fontSize = 13.sp)
                            Text("₹${String.format("%,.0f", gstTax)}", fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = BorderLight)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Grand Total", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                            Text("₹${String.format("%,.0f", grandTotal)}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = EmeraldPrimary)
                        }
                    }
                }
            }
        }
    }
}
