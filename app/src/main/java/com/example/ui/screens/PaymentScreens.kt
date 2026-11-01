package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.HotelRepository
import com.example.model.Booking
import com.example.model.HotelRoom
import com.example.ui.components.EmeraldPillButton
import com.example.ui.components.HotelCardWhite
import com.example.ui.components.StarRatingBadge
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodSelectScreen(
    booking: Booking,
    room: HotelRoom,
    onBack: () -> Unit,
    onAddNewCard: () -> Unit,
    onSelectAndProceed: (method: String) -> Unit
) {
    var selectedMethod by remember { mutableStateOf("Mastercard **** 4679") }

    Scaffold(
        containerColor = PureWhite,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Payment",
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
                actions = {
                    TextButton(onClick = onAddNewCard) {
                        Text("Add New Card", color = EmeraldPrimary, fontWeight = FontWeight.Bold)
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
                        onClick = { onSelectAndProceed(selectedMethod) },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "payment_method_continue_button"
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
            item {
                Text(
                    text = "Payment Methods",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            // Payment Options (Matching Screen 11 & 13)
            val options = listOf(
                "Paypal" to Icons.Default.Payment,
                "Google Pay" to Icons.Default.AccountBalanceWallet,
                "Apple Pay" to Icons.Default.PhoneIphone,
                "Mastercard **** 4679" to Icons.Default.CreditCard,
                "Direct Bank Transfer (IMPS)" to Icons.Default.AccountBalance,
                "Instant UPI & QR Code" to Icons.Default.QrCode
            )

            items(options.size) { index ->
                val (methodName, icon) = options[index]
                val isSelected = selectedMethod == methodName

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, BorderLight, RoundedCornerShape(16.dp))
                        .clickable { selectedMethod = methodName },
                    color = PureWhite,
                    shadowElevation = 1.dp,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isSelected) EmeraldPrimary else TextSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Text(
                            text = methodName,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        RadioButton(
                            selected = isSelected,
                            onClick = { selectedMethod = methodName },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = EmeraldPrimary,
                                unselectedColor = BorderMedium
                            )
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewCardScreen(
    onBack: () -> Unit,
    onCardAdded: () -> Unit
) {
    var cardName by remember { mutableStateOf("Daniel Austin") }
    var cardNumber by remember { mutableStateOf("6373 2728 4797 4679") }
    var expiry by remember { mutableStateOf("02/30") }
    var cvv by remember { mutableStateOf("190") }

    Scaffold(
        containerColor = PureWhite,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "New Card",
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
                        text = "Add New Card",
                        onClick = onCardAdded,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "add_new_card_submit_button"
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
            // Virtual Credit Card Graphic (Matching Screen 12 in Mockup)
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF1AB65C), Color(0xFF0F7D3E), Color(0xFF0B582C))
                                )
                            )
                            .padding(22.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Mocard", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("amazon", color = PureWhite.copy(alpha = 0.8f), fontWeight = FontWeight.Medium, fontSize = 14.sp)
                            }

                            // Card Number
                            Text(
                                text = cardNumber,
                                color = PureWhite,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("CARD HOLDER", color = PureWhite.copy(alpha = 0.6f), fontSize = 9.sp)
                                    Text(cardName, color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("EXPIRES", color = PureWhite.copy(alpha = 0.6f), fontSize = 9.sp)
                                    Text(expiry, color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Input Fields
            item {
                FormInputItem(
                    label = "Card Name",
                    value = cardName,
                    onValueChange = { cardName = it }
                )
            }

            item {
                FormInputItem(
                    label = "Card Number",
                    value = cardNumber,
                    onValueChange = { cardNumber = it }
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        FormInputItem(
                            label = "Expiry Date",
                            value = expiry,
                            onValueChange = { expiry = it }
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        FormInputItem(
                            label = "CVV",
                            value = cvv,
                            onValueChange = { cvv = it }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentConfirmationScreen(
    booking: Booking,
    room: HotelRoom,
    selectedMethod: String,
    onBack: () -> Unit,
    onChangePaymentMethod: () -> Unit,
    onPaymentSuccess: (Booking) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var isProcessing by remember { mutableStateOf(false) }
    var showSuccessModal by remember { mutableStateOf(false) }
    var confirmedBooking by remember { mutableStateOf<Booking?>(null) }

    Scaffold(
        containerColor = PureWhite,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Payment",
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
                        text = if (isProcessing) "Processing Payment..." else "Confirm Payment",
                        enabled = !isProcessing,
                        onClick = {
                            isProcessing = true
                            coroutineScope.launch {
                                delay(1200) // realistic settlement transition
                                val updated = HotelRepository.processPayment(
                                    bookingId = booking.id,
                                    paymentMethod = selectedMethod
                                )
                                isProcessing = false
                                if (updated != null) {
                                    confirmedBooking = updated
                                    showSuccessModal = true
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "confirm_payment_pill_button"
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
            // Hotel Card Thumbnail (Screen 13 in Mockup)
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, BorderLight, RoundedCornerShape(16.dp)),
                    color = PureWhite,
                    shadowElevation = 2.dp,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = room.imageUrl,
                            contentDescription = room.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(14.dp))
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(room.name, style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
                            Text(room.location, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            Spacer(modifier = Modifier.height(6.dp))
                            StarRatingBadge(rating = room.rating, reviewsCount = room.reviewCount)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("$${room.pricePerNight.toInt()}", style = MaterialTheme.typography.titleLarge, color = EmeraldPrimary, fontWeight = FontWeight.Bold)
                            Text("/ night", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        }
                    }
                }
            }

            // Dates & Guests
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, BorderLight, RoundedCornerShape(16.dp)),
                    color = PureWhite,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SummaryRow("Check in", booking.checkInDate)
                        SummaryRow("Check out", booking.checkOutDate)
                        SummaryRow("Guest", "${booking.guestsCount}")
                    }
                }
            }

            // Price Breakdown (Screen 13)
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, BorderLight, RoundedCornerShape(16.dp)),
                    color = PureWhite,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SummaryRow("${booking.nights} Nights", "$${String.format("%.2f", booking.basePrice)}")
                        SummaryRow("Taxes & Fees (10%)", "$${String.format("%.2f", booking.taxAmount)}")
                        HorizontalDivider(color = BorderLight)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total", style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
                            Text("$${String.format("%.2f", booking.totalAmount)}", style = MaterialTheme.typography.titleLarge, color = EmeraldPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Selected Payment Method with Change action
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, BorderLight, RoundedCornerShape(16.dp)),
                    color = PureWhite,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CreditCard, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(selectedMethod, style = MaterialTheme.typography.bodyMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
                        }

                        Text(
                            text = "Change",
                            color = EmeraldPrimary,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable(onClick = onChangePaymentMethod)
                        )
                    }
                }
            }
        }

        // Payment Success Modal (Screen 14 in Mockup)
        if (showSuccessModal && confirmedBooking != null) {
            AlertDialog(
                onDismissRequest = { },
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
                        // Green Circle Checkmark
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .clip(CircleShape)
                                .background(EmeraldPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Success",
                                tint = PureWhite,
                                modifier = Modifier.size(46.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Text(
                            text = "Payment Successful!",
                            style = MaterialTheme.typography.headlineSmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Successfully made payment and hotel booking. Funds credited directly to hotel bank account.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        EmeraldPillButton(
                            text = "View Ticket",
                            onClick = {
                                showSuccessModal = false
                                onPaymentSuccess(confirmedBooking!!)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "view_ticket_success_button"
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        TextButton(
                            onClick = {
                                showSuccessModal = false
                                onPaymentSuccess(confirmedBooking!!)
                            }
                        ) {
                            Text("Cancel", color = TextMuted, style = MaterialTheme.typography.labelLarge)
                        }
                    }
                },
                confirmButton = { }
            )
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        Text(value, style = MaterialTheme.typography.bodyMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketScreen(
    booking: Booking,
    onBack: () -> Unit
) {
    Scaffold(
        containerColor = PureWhite,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Ticket",
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
                        text = "Download Ticket",
                        onClick = onBack,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "download_ticket_button"
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
            // E-Ticket Card (Screen 15 in Mockup)
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .border(1.dp, BorderLight, RoundedCornerShape(24.dp)),
                    color = PureWhite,
                    shadowElevation = 4.dp,
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = booking.roomName,
                            style = MaterialTheme.typography.headlineSmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // QR Code Graphic
                        Box(
                            modifier = Modifier
                                .size(180.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(1.dp, BorderLight, RoundedCornerShape(16.dp))
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCode2,
                                contentDescription = "Ticket QR",
                                tint = TextPrimary,
                                modifier = Modifier.size(160.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        HorizontalDivider(color = BorderLight)
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Name", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                Text(booking.guestName, style = MaterialTheme.typography.titleSmall, color = TextPrimary, fontWeight = FontWeight.Bold)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Phone Number", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                Text(booking.guestPhone, style = MaterialTheme.typography.titleSmall, color = TextPrimary, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Check in", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                Text(booking.checkInDate, style = MaterialTheme.typography.titleSmall, color = TextPrimary, fontWeight = FontWeight.Bold)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Check out", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                Text(booking.checkOutDate, style = MaterialTheme.typography.titleSmall, color = TextPrimary, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Hotel & Room", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                Text("${booking.hotelName} (${booking.roomNumber})", style = MaterialTheme.typography.titleSmall, color = TextPrimary, fontWeight = FontWeight.Bold)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Registration No.", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                Text(booking.registrationNumber, style = MaterialTheme.typography.titleSmall, color = EmeraldDark, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Amount & Mode", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                Text("₹${String.format("%,.0f", booking.totalAmount)} via ${booking.paymentMethod}", style = MaterialTheme.typography.titleSmall, color = EmeraldPrimary, fontWeight = FontWeight.Bold)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Settlement Status", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                Text("Direct Bank Credit (Settled)", style = MaterialTheme.typography.titleSmall, color = EmeraldDark, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
