package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.HotelRepository
import com.example.model.UserProfile
import com.example.model.UserRole
import com.example.ui.components.EmeraldPillButton
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToEditProfile: () -> Unit,
    onNavigateToPaymentMethods: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToSecurity: () -> Unit,
    onOpenAdminPanel: () -> Unit,
    onLogout: () -> Unit
) {
    val currentUser by HotelRepository.currentUser.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showAvatarPickerSheet by remember { mutableStateOf(false) }

    // Android Zero-Permission Photo Picker for Customer Profile Picture Upload
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            HotelRepository.updateProfileAvatar(uri.toString())
        }
    }

    Scaffold(
        containerColor = PureWhite,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profile",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { showAvatarPickerSheet = true }) {
                        Icon(Icons.Outlined.AddPhotoAlternate, contentDescription = "Change Photo", tint = TextPrimary)
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
                .padding(horizontal = 24.dp)
                .testTag("profile_screen"),
            contentPadding = PaddingValues(top = 10.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // User Avatar & Name with Photo Upload affordance
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(104.dp)
                            .clip(CircleShape)
                            .border(2.dp, EmeraldPrimary, CircleShape)
                    ) {
                        AsyncImage(
                            model = currentUser?.avatarUrl ?: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400&q=80",
                            contentDescription = "Customer Profile Picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(EmeraldPrimary)
                                .align(Alignment.BottomEnd)
                                .clickable { showAvatarPickerSheet = true }
                                .testTag("upload_profile_photo_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = "Upload Photo", tint = PureWhite, modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = currentUser?.name ?: "Customer",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentUser?.email ?: "customer@hotel.com",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Registration No: ${currentUser?.registrationNumber ?: "REG-OD-2026-9842"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = EmeraldDark,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${currentUser?.city ?: "Bhubaneswar"}, ${currentUser?.district ?: "Khordha"} (Odisha)",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }

            // Menu Items List
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    ProfileMenuItem(Icons.Outlined.Person, "Edit Profile & Photo", onClick = onNavigateToEditProfile)
                    ProfileMenuItem(Icons.Outlined.Payment, "Payment Methods", onClick = onNavigateToPaymentMethods)
                    ProfileMenuItem(Icons.Outlined.Notifications, "Notifications", onClick = onNavigateToNotifications)
                    ProfileMenuItem(Icons.Outlined.Security, "Security & Privacy", onClick = onNavigateToSecurity)

                    // STRICT ROLE RESTRICTION: Admin Panel is ONLY rendered if user has ADMIN role.
                    // "कस्टमर जब लॉगिन करे, उसमें भी एडमिन नहीं दिखाना चाहिए।"
                    if (currentUser?.role == UserRole.ADMIN) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(1.dp, EmeraldPrimary.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                                .clickable(onClick = onOpenAdminPanel),
                            color = EmeraldContainer.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldPrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = PureWhite, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Admin Panel & Bank Details", style = MaterialTheme.typography.titleSmall, color = EmeraldDark, fontWeight = FontWeight.Bold)
                                    Text("Manage bank account, payout settlement & rooms", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                }
                                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = EmeraldDark)
                            }
                        }
                    }

                    // Logout Action
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showLogoutDialog = true }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = null, tint = RedCancel, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Logout", style = MaterialTheme.typography.titleMedium, color = RedCancel, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Avatar Picker / Upload Dialog
        if (showAvatarPickerSheet) {
            AlertDialog(
                onDismissRequest = { showAvatarPickerSheet = false },
                containerColor = PureWhite,
                shape = RoundedCornerShape(20.dp),
                title = {
                    Text(
                        text = "Change Profile Picture",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(
                            text = "Upload a photo from your gallery or choose an avatar:",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )

                        // Button to launch Android Photo Picker
                        Button(
                            onClick = {
                                showAvatarPickerSheet = false
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Upload from Device Gallery", fontWeight = FontWeight.Bold)
                        }

                        Text(
                            text = "Or Choose an Avatar:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = TextPrimary
                        )

                        val sampleAvatars = listOf(
                            "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400&q=80",
                            "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&q=80",
                            "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&q=80",
                            "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=400&q=80",
                            "https://images.unsplash.com/photo-1580489944761-15a19d654956?w=400&q=80"
                        )

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(sampleAvatars) { url ->
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .border(2.dp, if (currentUser?.avatarUrl == url) EmeraldPrimary else BorderMedium, CircleShape)
                                        .clickable {
                                            HotelRepository.updateProfileAvatar(url)
                                            showAvatarPickerSheet = false
                                        }
                                ) {
                                    AsyncImage(
                                        model = url,
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showAvatarPickerSheet = false }) {
                        Text("Cancel", color = TextSecondary)
                    }
                }
            )
        }

        // Logout Confirmation Dialog
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                containerColor = PureWhite,
                shape = RoundedCornerShape(24.dp),
                title = {
                    Text(
                        text = "Logout",
                        color = RedCancel,
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
                            text = "Are you sure you want to log out of your Star Hotel account?",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { showLogoutDialog = false },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldContainer, contentColor = EmeraldDark),
                                shape = RoundedCornerShape(50)
                            ) {
                                Text("Cancel", fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    showLogoutDialog = false
                                    onLogout()
                                },
                                modifier = Modifier
                                    .weight(1.3f)
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = PureWhite),
                                shape = RoundedCornerShape(50)
                            ) {
                                Text("Yes, Logout", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                },
                confirmButton = { }
            )
        }
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.SemiBold)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBack: () -> Unit
) {
    val currentUser by HotelRepository.currentUser.collectAsState()

    var name by remember { mutableStateOf(currentUser?.name ?: "Customer") }
    var nickname by remember { mutableStateOf(currentUser?.nickname ?: "Guest") }
    var dob by remember { mutableStateOf(currentUser?.dateOfBirth ?: "12/27/1995") }
    var email by remember { mutableStateOf(currentUser?.email ?: "customer@hotel.com") }
    var phone by remember { mutableStateOf(currentUser?.phone ?: "+91 94370 12345") }
    var country by remember { mutableStateOf(currentUser?.country ?: "India") }
    var gender by remember { mutableStateOf(currentUser?.gender ?: "Male") }

    // Photo picker for edit profile
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            HotelRepository.updateProfileAvatar(uri.toString())
        }
    }

    Scaffold(
        containerColor = PureWhite,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Edit Profile & Photo",
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
                        text = "Save & Update Profile",
                        onClick = {
                            currentUser?.let {
                                HotelRepository.updateProfile(
                                    it.copy(
                                        name = name,
                                        nickname = nickname,
                                        dateOfBirth = dob,
                                        email = email,
                                        phone = phone,
                                        country = country,
                                        gender = gender
                                    )
                                )
                            }
                            onBack()
                        },
                        modifier = Modifier.fillMaxWidth()
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
            // Profile Picture with Direct Upload Trigger
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .border(2.dp, EmeraldPrimary, CircleShape)
                    ) {
                        AsyncImage(
                            model = currentUser?.avatarUrl ?: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400&q=80",
                            contentDescription = "Profile Picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = EmeraldDark, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Upload New Photo", color = EmeraldDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            item { FormInputItem("Full Name", name, onValueChange = { name = it }) }
            item { FormInputItem("Nickname", nickname, onValueChange = { nickname = it }) }
            item { FormInputItem("Date of Birth", dob, onValueChange = { dob = it }, trailingIcon = Icons.Default.CalendarToday) }
            item { FormInputItem("Email", email, onValueChange = { email = it }, trailingIcon = Icons.Default.Email) }
            item { FormInputItem("Country", country, onValueChange = { country = it }, trailingIcon = Icons.Default.ArrowDropDown) }
            item { FormInputItem("Phone Number", phone, onValueChange = { phone = it }, leadingFlag = true) }
            item { FormInputItem("Gender", gender, onValueChange = { gender = it }, trailingIcon = Icons.Default.ArrowDropDown) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    onBack: () -> Unit
) {
    var general by remember { mutableStateOf(true) }
    var sound by remember { mutableStateOf(false) }
    var vibrate by remember { mutableStateOf(false) }
    var appUpdates by remember { mutableStateOf(true) }
    var newServices by remember { mutableStateOf(false) }
    var newTips by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = PureWhite,
        topBar = {
            TopAppBar(
                title = { Text("Notification", color = TextPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
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
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SettingToggleItem("General Notification", general) { general = it }
            SettingToggleItem("Sound", sound) { sound = it }
            SettingToggleItem("Vibrate", vibrate) { vibrate = it }
            SettingToggleItem("App Updates", appUpdates) { appUpdates = it }
            SettingToggleItem("New Service Available", newServices) { newServices = it }
            SettingToggleItem("New tips available", newTips) { newTips = it }
        }
    }
}

@Composable
private fun SettingToggleItem(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = TextPrimary, fontWeight = FontWeight.Medium)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = EmeraldPrimary, checkedTrackColor = EmeraldContainer)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecuritySettingsScreen(
    onBack: () -> Unit
) {
    var faceId by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }
    var touchId by remember { mutableStateOf(true) }

    Scaffold(
        containerColor = PureWhite,
        topBar = {
            TopAppBar(
                title = { Text("Security", color = TextPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
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
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SettingToggleItem("Face ID", faceId) { faceId = it }
            SettingToggleItem("Remember me", rememberMe) { rememberMe = it }
            SettingToggleItem("Touch ID", touchId) { touchId = it }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Google Authenticator", style = MaterialTheme.typography.bodyMedium, color = TextPrimary, fontWeight = FontWeight.Medium)
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldContainer, contentColor = EmeraldDark),
                shape = RoundedCornerShape(50)
            ) {
                Text("Change Password", fontWeight = FontWeight.Bold)
            }
        }
    }
}
