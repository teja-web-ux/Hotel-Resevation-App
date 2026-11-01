package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.HotelRepository
import com.example.model.Booking
import com.example.model.HotelRoom
import com.example.ui.components.EmeraldPillButton
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectDateScreen(
    room: HotelRoom,
    onBack: () -> Unit,
    onContinue: (checkIn: String, checkOut: String, nights: Int, guests: Int) -> Unit
) {
    var checkInDay by remember { mutableIntStateOf(16) }
    var checkOutDay by remember { mutableIntStateOf(20) }
    var guestCount by remember { mutableIntStateOf(3) }

    val nights = (checkOutDay - checkInDay).coerceAtLeast(1)
    val totalAmount = room.pricePerNight * nights * 1.10 // matches $479.50 with taxes

    Scaffold(
        containerColor = PureWhite,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Select Date",
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
                    Text(
                        text = "Total: $${String.format("%.0f", room.pricePerNight * nights)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    EmeraldPillButton(
                        text = "Continue",
                        onClick = {
                            onContinue("December $checkInDay, 2024", "December $checkOutDay, 2024", nights, guestCount)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "select_date_continue_button"
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Month Navigation
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "December 2024",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Previous", tint = TextPrimary, modifier = Modifier.size(22.dp))
                        Icon(Icons.Default.ChevronRight, contentDescription = "Next", tint = TextPrimary, modifier = Modifier.size(22.dp))
                    }
                }
            }

            // Calendar Grid Days of Week
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su").forEach { day ->
                        Text(
                            text = day,
                            color = TextMuted,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.width(36.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Days 1..31
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    val days = (1..31).toList()
                    days.chunked(7).forEach { week ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            week.forEach { d ->
                                val isInRange = d in checkInDay..checkOutDay
                                val isStart = d == checkInDay
                                val isEnd = d == checkOutDay

                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                isStart || isEnd -> EmeraldPrimary
                                                isInRange -> EmeraldLight
                                                else -> Color.Transparent
                                            }
                                        )
                                        .clickable {
                                            if (d < checkInDay) {
                                                checkInDay = d
                                            } else {
                                                checkOutDay = d
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$d",
                                        color = when {
                                            isStart || isEnd -> PureWhite
                                            isInRange -> EmeraldDark
                                            else -> TextPrimary
                                        },
                                        fontWeight = if (isInRange) FontWeight.Bold else FontWeight.Normal,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                            // Filler if week is less than 7 days
                            repeat(7 - week.size) {
                                Spacer(modifier = Modifier.size(36.dp))
                            }
                        }
                    }
                }
            }

            // Check in / Check out Summary
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Check in", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Dec $checkInDay", style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
                    }

                    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(20.dp))

                    Column(horizontalAlignment = Alignment.End) {
                        Text("Check out", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Dec $checkOutDay", style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = BorderLight)
            }

            // Guest Count Stepper (- 3 +)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Guest",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceLight)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        IconButton(
                            onClick = { if (guestCount > 1) guestCount-- },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = TextPrimary, modifier = Modifier.size(16.dp))
                        }
                        Text(
                            text = "$guestCount",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        IconButton(
                            onClick = { if (guestCount < room.maxGuests) guestCount++ },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Increase", tint = TextPrimary, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameOfReservationScreen(
    room: HotelRoom,
    checkInDate: String,
    checkOutDate: String,
    nights: Int,
    guestCount: Int,
    onBack: () -> Unit,
    onContinueToPayment: (Booking) -> Unit
) {
    val currentUser by HotelRepository.currentUser.collectAsState()

    var salutation by remember { mutableStateOf("Mr.") }
    var name by remember { mutableStateOf(currentUser?.name ?: "Daniel Austin") }
    var nickname by remember { mutableStateOf(currentUser?.nickname ?: "Daniel") }
    var dob by remember { mutableStateOf(currentUser?.dateOfBirth ?: "12/27/1995") }
    var email by remember { mutableStateOf(currentUser?.email ?: "daniel_austin@yourdomain.com") }
    var phone by remember { mutableStateOf(currentUser?.phone ?: "+1 111 467 378 399") }

    Scaffold(
        containerColor = PureWhite,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Name of Reservation",
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
                    EmeraldPillButton(
                        text = "Continue",
                        onClick = {
                            val booking = HotelRepository.createBooking(
                                guestName = name,
                                guestEmail = email,
                                guestPhone = phone,
                                room = room,
                                checkInDate = checkInDate,
                                checkOutDate = checkOutDate,
                                nights = nights,
                                guestsCount = guestCount
                            )
                            onContinueToPayment(booking)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "reservation_name_continue_button"
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Salutation chips: Mr. Mrs. Ms.
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf("Mr.", "Mrs.", "Ms.").forEach { s ->
                        val isSelected = salutation == s
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(50))
                                .border(
                                    1.dp,
                                    if (isSelected) EmeraldPrimary else BorderMedium,
                                    RoundedCornerShape(50)
                                )
                                .clickable { salutation = s },
                            color = if (isSelected) EmeraldPrimary else PureWhite,
                            shape = RoundedCornerShape(50)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = s,
                                    color = if (isSelected) PureWhite else TextSecondary,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Input Fields matching Screen 10
            item {
                FormInputItem(
                    label = "Full Name",
                    value = name,
                    onValueChange = { name = it }
                )
            }

            item {
                FormInputItem(
                    label = "Nickname",
                    value = nickname,
                    onValueChange = { nickname = it }
                )
            }

            item {
                FormInputItem(
                    label = "Date of Birth",
                    value = dob,
                    onValueChange = { dob = it },
                    trailingIcon = Icons.Default.CalendarToday
                )
            }

            item {
                FormInputItem(
                    label = "Email",
                    value = email,
                    onValueChange = { email = it },
                    trailingIcon = Icons.Default.Email
                )
            }

            item {
                FormInputItem(
                    label = "Phone Number",
                    value = phone,
                    onValueChange = { phone = it },
                    leadingFlag = true
                )
            }
        }
    }
}

@Composable
fun FormInputItem(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    trailingIcon: ImageVector? = null,
    leadingFlag: Boolean = false
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        color = SurfaceLight,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingFlag) {
                Text("🇺🇸", fontSize = 18.sp)
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = TextMuted)
                Spacer(modifier = Modifier.width(6.dp))
            }

            TextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(label, color = TextMuted, fontSize = 12.sp) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                singleLine = true,
                modifier = Modifier.weight(1f)
            )

            if (trailingIcon != null) {
                Icon(trailingIcon, contentDescription = null, tint = TextMuted, modifier = Modifier.size(20.dp))
            }
        }
    }
}
