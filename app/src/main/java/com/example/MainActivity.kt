package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.HotelRepository
import com.example.model.Booking
import com.example.model.HotelRoom
import com.example.model.HotelRoomUnit
import com.example.model.OdishaHotel
import com.example.model.UserRole
import com.example.ui.components.LiveNotificationAlert
import com.example.ui.screens.*
import com.example.ui.theme.*

sealed class AppDestination {
    object Home : AppDestination()

    // Odisha Hotel Flow (User Requirements)
    data class OdishaHotelRooms(val hotel: OdishaHotel) : AppDestination()
    data class BookingRegistration(val hotel: OdishaHotel, val room: HotelRoomUnit) : AppDestination()
    data class CustomerDetailsAndPayment(
        val hotel: OdishaHotel,
        val room: HotelRoomUnit,
        val guestName: String,
        val registrationNumber: String,
        val guestEmail: String,
        val guestPhone: String,
        val checkInDate: String,
        val checkOutDate: String,
        val nights: Int,
        val guestsCount: Int
    ) : AppDestination()

    // Legacy Room & Card screens for backwards compatibility
    data class HotelDetail(val room: HotelRoom) : AppDestination()
    data class SelectDate(val room: HotelRoom) : AppDestination()
    data class NameOfReservation(
        val room: HotelRoom,
        val checkIn: String,
        val checkOut: String,
        val nights: Int,
        val guests: Int
    ) : AppDestination()
    data class PaymentMethodSelect(val booking: Booking, val room: HotelRoom) : AppDestination()
    object AddNewCard : AppDestination()
    data class PaymentConfirmation(
        val booking: Booking,
        val room: HotelRoom,
        val selectedMethod: String
    ) : AppDestination()

    // Passports & Profiles
    data class Ticket(val booking: Booking) : AppDestination()
    object MyBookings : AppDestination()
    object Profile : AppDestination()
    object EditProfile : AppDestination()
    object NotificationSettings : AppDestination()
    object SecuritySettings : AppDestination()

    // Admin Panel (Bank Settings, Real-time Revenue & Rooms)
    object AdminDashboard : AppDestination()
    object AdminBankSettings : AppDestination()
    object AdminBookings : AppDestination()
    object AdminRooms : AppDestination()
    object AdminUsers : AppDestination()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StarHotelTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    val isAuthenticated by HotelRepository.isAuthenticated.collectAsState()
    val currentUser by HotelRepository.currentUser.collectAsState()
    val notifications by HotelRepository.notifications.collectAsState()

    var currentDestination by remember { mutableStateOf<AppDestination>(AppDestination.Home) }
    var backStack by remember { mutableStateOf(listOf<AppDestination>(AppDestination.Home)) }

    fun navigateTo(dest: AppDestination) {
        backStack = backStack + dest
        currentDestination = dest
    }

    fun navigateBack() {
        if (backStack.size > 1) {
            val newStack = backStack.dropLast(1)
            backStack = newStack
            currentDestination = newStack.last()
        } else {
            currentDestination = AppDestination.Home
        }
    }

    // MANDATORY AUTHENTICATION GATE
    // "When the customer opens the app, they must first log in or register. Once logged in, they can proceed with their work."
    if (!isAuthenticated || currentUser == null) {
        AuthScreen(
            onAuthSuccess = {
                val startDest = if (HotelRepository.currentUser.value?.role == UserRole.ADMIN) {
                    AppDestination.AdminDashboard
                } else {
                    AppDestination.Home
                }
                backStack = listOf(startDest)
                currentDestination = startDest
            }
        )
        return
    }

    BackHandler(enabled = backStack.size > 1 || currentDestination != AppDestination.Home) {
        navigateBack()
    }

    val isTopLevelDestination = currentDestination is AppDestination.Home ||
            currentDestination is AppDestination.MyBookings ||
            currentDestination is AppDestination.Profile

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = PureWhite,
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                // Real-time Notification Banner for Payments & Account Credits
                notifications.firstOrNull()?.let { notif ->
                    LiveNotificationAlert(
                        notification = notif,
                        onDismiss = { HotelRepository.dismissNotification(notif.id) }
                    )
                }
            }
        },
        bottomBar = {
            if (isTopLevelDestination) {
                NavigationBar(
                    containerColor = PureWhite,
                    contentColor = TextPrimary,
                    modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = currentDestination is AppDestination.Home,
                        onClick = {
                            backStack = listOf(AppDestination.Home)
                            currentDestination = AppDestination.Home
                        },
                        icon = {
                            Icon(
                                imageVector = if (currentDestination is AppDestination.Home) Icons.Default.Home else Icons.Outlined.Home,
                                contentDescription = "Home"
                            )
                        },
                        label = { Text("Home", fontWeight = FontWeight.SemiBold, fontSize = 11.sp) },
                        colors = bottomNavColors()
                    )

                    NavigationBarItem(
                        selected = false,
                        onClick = {
                            backStack = listOf(AppDestination.Home)
                            currentDestination = AppDestination.Home
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = "Search"
                            )
                        },
                        label = { Text("Search", fontWeight = FontWeight.SemiBold, fontSize = 11.sp) },
                        colors = bottomNavColors()
                    )

                    NavigationBarItem(
                        selected = currentDestination is AppDestination.MyBookings,
                        onClick = {
                            backStack = listOf(AppDestination.Home)
                            navigateTo(AppDestination.MyBookings)
                        },
                        icon = {
                            Icon(
                                imageVector = if (currentDestination is AppDestination.MyBookings) Icons.Default.ConfirmationNumber else Icons.Outlined.ConfirmationNumber,
                                contentDescription = "Booking"
                            )
                        },
                        label = { Text("Booking", fontWeight = FontWeight.SemiBold, fontSize = 11.sp) },
                        colors = bottomNavColors()
                    )

                    NavigationBarItem(
                        selected = currentDestination is AppDestination.Profile,
                        onClick = {
                            backStack = listOf(AppDestination.Home)
                            navigateTo(AppDestination.Profile)
                        },
                        icon = {
                            Icon(
                                imageVector = if (currentDestination is AppDestination.Profile) Icons.Default.Person else Icons.Outlined.Person,
                                contentDescription = "Profile"
                            )
                        },
                        label = { Text("Profile", fontWeight = FontWeight.SemiBold, fontSize = 11.sp) },
                        colors = bottomNavColors()
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val dest = currentDestination) {
                is AppDestination.Home -> OdishaHomeScreen(
                    onSelectHotel = { hotel -> navigateTo(AppDestination.OdishaHotelRooms(hotel)) },
                    onSelectSpecificRoom = { hotel, roomUnit ->
                        navigateTo(AppDestination.BookingRegistration(hotel, roomUnit))
                    },
                    onOpenAdmin = { navigateTo(AppDestination.AdminDashboard) }
                )

                is AppDestination.OdishaHotelRooms -> HotelRoomsScreen(
                    hotel = dest.hotel,
                    onBack = { navigateBack() },
                    onSelectRoomToBook = { hotel, roomUnit ->
                        navigateTo(AppDestination.BookingRegistration(hotel, roomUnit))
                    }
                )

                is AppDestination.BookingRegistration -> BookingRegistrationScreen(
                    hotel = dest.hotel,
                    room = dest.room,
                    onBack = { navigateBack() },
                    onProceedToCustomerDetails = { name, regNo, email, phone, checkIn, checkOut, nights, guests ->
                        navigateTo(
                            AppDestination.CustomerDetailsAndPayment(
                                hotel = dest.hotel,
                                room = dest.room,
                                guestName = name,
                                registrationNumber = regNo,
                                guestEmail = email,
                                guestPhone = phone,
                                checkInDate = checkIn,
                                checkOutDate = checkOut,
                                nights = nights,
                                guestsCount = guests
                            )
                        )
                    }
                )

                is AppDestination.CustomerDetailsAndPayment -> CustomerDetailsAndPaymentScreen(
                    hotel = dest.hotel,
                    room = dest.room,
                    guestName = dest.guestName,
                    registrationNumber = dest.registrationNumber,
                    guestEmail = dest.guestEmail,
                    guestPhone = dest.guestPhone,
                    checkInDate = dest.checkInDate,
                    checkOutDate = dest.checkOutDate,
                    nights = dest.nights,
                    guestsCount = dest.guestsCount,
                    onBack = { navigateBack() },
                    onPaymentSuccess = { confirmedBooking ->
                        navigateTo(AppDestination.Ticket(confirmedBooking))
                    }
                )

                is AppDestination.Ticket -> TicketScreen(
                    booking = dest.booking,
                    onBack = {
                        backStack = listOf(AppDestination.Home)
                        navigateTo(AppDestination.MyBookings)
                    }
                )

                is AppDestination.MyBookings -> MyBookingsScreen(
                    onViewTicket = { booking -> navigateTo(AppDestination.Ticket(booking)) }
                )

                is AppDestination.Profile -> ProfileScreen(
                    onNavigateToEditProfile = { navigateTo(AppDestination.EditProfile) },
                    onNavigateToPaymentMethods = {
                        val sampleHotel = HotelRepository.hotels.value.first()
                        val sampleRoom = sampleHotel.rooms.first()
                        navigateTo(AppDestination.BookingRegistration(sampleHotel, sampleRoom))
                    },
                    onNavigateToNotifications = { navigateTo(AppDestination.NotificationSettings) },
                    onNavigateToSecurity = { navigateTo(AppDestination.SecuritySettings) },
                    onOpenAdminPanel = { navigateTo(AppDestination.AdminDashboard) },
                    onLogout = {
                        HotelRepository.logout()
                        backStack = listOf(AppDestination.Home)
                        currentDestination = AppDestination.Home
                    }
                )

                is AppDestination.EditProfile -> EditProfileScreen(
                    onBack = { navigateBack() }
                )

                is AppDestination.NotificationSettings -> NotificationSettingsScreen(
                    onBack = { navigateBack() }
                )

                is AppDestination.SecuritySettings -> SecuritySettingsScreen(
                    onBack = { navigateBack() }
                )

                // Admin Panel Screens
                is AppDestination.AdminDashboard -> AdminDashboardScreen(
                    onNavigateToBankSettings = { navigateTo(AppDestination.AdminBankSettings) },
                    onNavigateToBookings = { navigateTo(AppDestination.AdminBookings) },
                    onNavigateToRooms = { navigateTo(AppDestination.AdminRooms) },
                    onNavigateToUsers = { navigateTo(AppDestination.AdminUsers) },
                    onExitAdmin = {
                        backStack = listOf(AppDestination.Home)
                        currentDestination = AppDestination.Home
                    }
                )

                is AppDestination.AdminBankSettings -> AdminBankSettingsScreen(
                    onBack = { navigateBack() }
                )

                is AppDestination.AdminBookings -> AdminBookingsScreen(
                    onBack = { navigateBack() }
                )

                is AppDestination.AdminRooms -> AdminRoomsScreen(
                    onBack = { navigateBack() }
                )

                is AppDestination.AdminUsers -> AdminUsersScreen(
                    onBack = { navigateBack() }
                )

                // Backward-compat destinations
                is AppDestination.HotelDetail -> RoomDetailScreen(
                    room = dest.room,
                    onBack = { navigateBack() },
                    onBookNow = { room -> navigateTo(AppDestination.SelectDate(room)) }
                )

                is AppDestination.SelectDate -> SelectDateScreen(
                    room = dest.room,
                    onBack = { navigateBack() },
                    onContinue = { checkIn, checkOut, nights, guests ->
                        navigateTo(AppDestination.NameOfReservation(dest.room, checkIn, checkOut, nights, guests))
                    }
                )

                is AppDestination.NameOfReservation -> NameOfReservationScreen(
                    room = dest.room,
                    checkInDate = dest.checkIn,
                    checkOutDate = dest.checkOut,
                    nights = dest.nights,
                    guestCount = dest.guests,
                    onBack = { navigateBack() },
                    onContinueToPayment = { booking ->
                        navigateTo(AppDestination.PaymentMethodSelect(booking, dest.room))
                    }
                )

                is AppDestination.PaymentMethodSelect -> PaymentMethodSelectScreen(
                    booking = dest.booking,
                    room = dest.room,
                    onBack = { navigateBack() },
                    onAddNewCard = { navigateTo(AppDestination.AddNewCard) },
                    onSelectAndProceed = { method ->
                        navigateTo(AppDestination.PaymentConfirmation(dest.booking, dest.room, method))
                    }
                )

                is AppDestination.AddNewCard -> AddNewCardScreen(
                    onBack = { navigateBack() },
                    onCardAdded = { navigateBack() }
                )

                is AppDestination.PaymentConfirmation -> PaymentConfirmationScreen(
                    booking = dest.booking,
                    room = dest.room,
                    selectedMethod = dest.selectedMethod,
                    onBack = { navigateBack() },
                    onChangePaymentMethod = { navigateBack() },
                    onPaymentSuccess = { confirmedBooking ->
                        navigateTo(AppDestination.Ticket(confirmedBooking))
                    }
                )
            }
        }
    }
}

@Composable
fun bottomNavColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = EmeraldPrimary,
    selectedTextColor = EmeraldPrimary,
    indicatorColor = EmeraldLight,
    unselectedIconColor = TextMuted,
    unselectedTextColor = TextMuted
)
