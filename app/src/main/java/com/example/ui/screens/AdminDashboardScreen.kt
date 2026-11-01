package com.example.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.HotelRepository
import com.example.model.BookingStatus
import com.example.model.PaymentTransaction
import com.example.ui.components.EmeraldPillButton
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onNavigateToBankSettings: () -> Unit,
    onNavigateToBookings: () -> Unit,
    onNavigateToRooms: () -> Unit,
    onNavigateToUsers: () -> Unit,
    onExitAdmin: () -> Unit
) {
    val bankDetails by HotelRepository.bankDetails.collectAsState()
    val bookings by HotelRepository.bookings.collectAsState()
    val rooms by HotelRepository.rooms.collectAsState()
    val users by HotelRepository.users.collectAsState()
    val transactions by HotelRepository.transactions.collectAsState()

    val totalRevenue = remember(bankDetails) { bankDetails.totalSettledRevenue }
    val activeBookingsCount = remember(bookings) { bookings.count { it.bookingStatus == BookingStatus.CONFIRMED } }
    val pendingBookingsCount = remember(bookings) { bookings.count { it.bookingStatus == BookingStatus.PENDING } }
    val occupancyRate = remember(rooms, activeBookingsCount) {
        if (rooms.isEmpty()) 0 else ((activeBookingsCount.toFloat() / rooms.size.toFloat()) * 100).toInt().coerceIn(0, 100)
    }

    Scaffold(
        containerColor = PureWhite,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(EmeraldContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = EmeraldDark, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Hotel Admin & Payouts", style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
                            Text("Direct Bank Account Settlement", style = MaterialTheme.typography.labelSmall, color = EmeraldDark)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onExitAdmin) {
                        Icon(Icons.Default.Close, contentDescription = "Exit Admin", tint = TextPrimary)
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
                .testTag("admin_dashboard_screen"),
            contentPadding = PaddingValues(top = 10.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Live Bank Account Primary Card
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.5.dp, EmeraldPrimary.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                        .clickable(onClick = onNavigateToBankSettings),
                    color = EmeraldContainer.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldPrimary)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "LIVE SETTLED BANK ACCOUNT",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = EmeraldDark,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Edit Payouts", style = MaterialTheme.typography.labelSmall, color = EmeraldPrimary, fontWeight = FontWeight.Bold)
                                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(18.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "$${String.format("%,.2f", bankDetails.currentBalance)}",
                            style = MaterialTheme.typography.displayMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Direct balance in ${bankDetails.bankName} (A/C: *${bankDetails.accountNumber.takeLast(4)})",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = BorderMedium)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Total Settled Revenue", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                Text("$${String.format("%,.2f", totalRevenue)}", style = MaterialTheme.typography.titleMedium, color = EmeraldDark, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Last Payment Credit", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                Text("+$${String.format("%,.2f", bankDetails.lastSettledAmount)}", style = MaterialTheme.typography.titleMedium, color = EmeraldPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Quick Navigation Hub
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AdminNavCard(
                        icon = Icons.Default.AccountBalance,
                        title = "Bank & UPI",
                        subtitle = "Payout Setup",
                        onClick = onNavigateToBankSettings,
                        modifier = Modifier.weight(1f)
                    )
                    AdminNavCard(
                        icon = Icons.Default.CalendarToday,
                        title = "Bookings",
                        subtitle = "${bookings.size} Total",
                        onClick = onNavigateToBookings,
                        modifier = Modifier.weight(1f)
                    )
                    AdminNavCard(
                        icon = Icons.Default.Hotel,
                        title = "Hotels",
                        subtitle = "${rooms.size} Units",
                        onClick = onNavigateToRooms,
                        modifier = Modifier.weight(1f)
                    )
                    AdminNavCard(
                        icon = Icons.Default.People,
                        title = "Users",
                        subtitle = "${users.size} Guests",
                        onClick = onNavigateToUsers,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Analytics Metrics Grid
            item {
                Text(
                    text = "Performance Analytics",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricBox(
                        title = "Occupancy Rate",
                        value = "$occupancyRate%",
                        subtitle = "$activeBookingsCount of ${rooms.size} booked",
                        icon = Icons.Default.Hotel,
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        title = "Active Bookings",
                        value = "$activeBookingsCount",
                        subtitle = "$pendingBookingsCount pending",
                        icon = Icons.Default.ConfirmationNumber,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Live Transactions Log
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Real-Time Incoming Payouts",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${transactions.size} Settled",
                        style = MaterialTheme.typography.labelSmall,
                        color = EmeraldPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            items(transactions) { txn ->
                val dateStr = remember(txn.timestamp) {
                    SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(txn.timestamp))
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, BorderLight, RoundedCornerShape(16.dp)),
                    color = PureWhite,
                    shadowElevation = 1.dp,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(txn.guestName, style = MaterialTheme.typography.titleSmall, color = TextPrimary, fontWeight = FontWeight.Bold)
                            Text("${txn.paymentMethod} • Ref: ${txn.transactionRef}", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            Text("Direct credit to ${txn.destinationBank} (*${txn.destinationAccount.takeLast(4)})", style = MaterialTheme.typography.labelSmall, color = EmeraldDark)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("+$${String.format("%.2f", txn.amount)}", style = MaterialTheme.typography.titleMedium, color = EmeraldPrimary, fontWeight = FontWeight.Bold)
                            Text(dateStr, style = MaterialTheme.typography.labelSmall, color = TextMuted, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminNavCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, BorderLight, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        color = SurfaceLight,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(title, style = MaterialTheme.typography.labelMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = TextMuted, fontSize = 10.sp)
        }
    }
}

@Composable
private fun MetricBox(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, BorderLight, RoundedCornerShape(16.dp)),
        color = SurfaceLight,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, style = MaterialTheme.typography.labelSmall, color = TextMuted)
                Icon(icon, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.headlineMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = EmeraldDark)
        }
    }
}
