package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import com.example.data.HotelRepository
import com.example.ui.components.EmeraldPillButton
import com.example.ui.theme.*

data class IncomingSmsNotification(
    val sender: String = "VM-STARHTL",
    val recipientPhone: String,
    val otpCode: String,
    val fullText: String,
    val timestamp: String = "Just now"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var isRegisterMode by remember { mutableStateOf(false) }

    // Login Form State
    var loginEmailOrPhone by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    // Register Form State
    var regName by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPhone by remember { mutableStateOf("") }
    var regOtp by remember { mutableStateOf("") }
    var isOtpSent by remember { mutableStateOf(false) }
    var isOtpVerified by remember { mutableStateOf(false) }
    var regPassword by remember { mutableStateOf("") }
    var selectedDistrict by remember { mutableStateOf("Khordha") }
    var selectedCity by remember { mutableStateOf("Bhubaneswar") }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var statusSuccessMessage by remember { mutableStateOf<String?>(null) }
    var isDistrictDropdownExpanded by remember { mutableStateOf(false) }

    // Forgot Password Dialog State
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var forgotIdentifier by remember { mutableStateOf("") }
    var forgotOtp by remember { mutableStateOf("") }
    var forgotNewPassword by remember { mutableStateOf("") }
    var isForgotOtpSent by remember { mutableStateOf(false) }
    var forgotDialogError by remember { mutableStateOf<String?>(null) }

    // Live Phone SMS Arrival Simulation State
    var incomingSms by remember { mutableStateOf<IncomingSmsNotification?>(null) }
    var resendCooldown by remember { mutableIntStateOf(0) }

    // 30s Cooldown Ticker
    LaunchedEffect(resendCooldown) {
        if (resendCooldown > 0) {
            delay(1000L)
            resendCooldown -= 1
        }
    }

    // Auto-dismiss SMS Notification Banner after 15 seconds
    LaunchedEffect(incomingSms) {
        if (incomingSms != null) {
            delay(15000L)
            incomingSms = null
        }
    }

    // Dispatch OTP to mobile phone number and trigger real-time heads-up SMS notification
    fun triggerOtpDispatch(phone: String, isForgot: Boolean = false) {
        val cleanDigits = phone.filter { it.isDigit() }
        if (cleanDigits.length < 10) {
            val msg = "Please enter a valid 10-digit mobile number."
            if (isForgot) forgotDialogError = msg else errorMessage = msg
            return
        }
        val rawNumber = cleanDigits.takeLast(10)
        val formattedPhone = "+91 $rawNumber"
        val code = HotelRepository.sendOtp(rawNumber)
        resendCooldown = 30

        if (isForgot) {
            isForgotOtpSent = true
            forgotDialogError = null
            Toast.makeText(context, "SMS OTP dispatched to $formattedPhone", Toast.LENGTH_SHORT).show()
        } else {
            isOtpSent = true
            errorMessage = null
            statusSuccessMessage = "SMS OTP delivered to $formattedPhone. Check notification banner above."
            Toast.makeText(context, "SMS OTP delivered to $formattedPhone", Toast.LENGTH_SHORT).show()
        }

        // Realistic heads-up SMS notification banner appears on the device
        incomingSms = IncomingSmsNotification(
            sender = "VM-STARHTL",
            recipientPhone = formattedPhone,
            otpCode = code,
            fullText = "Star Hotel Odisha: Your phone verification OTP is $code for mobile $formattedPhone. Valid for 10 minutes. Do not share with anyone."
        )
    }

    val districts = HotelRepository.odishaDistricts.filter { it != "All Districts" }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PureWhite)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("auth_screen")
    ) {
        // ================= HEADS-UP SMS NOTIFICATION BANNER =================
        AnimatedVisibility(
            visible = incomingSms != null,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp)
                .zIndex(999f)
        ) {
            incomingSms?.let { sms ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(16.dp, RoundedCornerShape(18.dp)),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        // Banner Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Sms,
                                    contentDescription = "SMS",
                                    tint = PureWhite,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "MESSAGES • ${sms.sender}",
                                        color = PureWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = EmeraldDark.copy(alpha = 0.6f)
                                    ) {
                                        Text(
                                            text = "SIM 1 • SMS",
                                            color = Color(0xFF86EFAC),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "To: ${sms.recipientPhone} • ${sms.timestamp}",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp
                                )
                            }

                            IconButton(
                                onClick = { incomingSms = null },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Dismiss SMS",
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = sms.fullText,
                            color = Color(0xFFE2E8F0),
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // OTP Code Chip and Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF1E293B),
                                border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.6f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "OTP: ",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = sms.otpCode,
                                        color = Color(0xFF4ADE80),
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 18.sp,
                                        letterSpacing = 2.sp
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                OutlinedButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(sms.otpCode))
                                        Toast.makeText(context, "OTP ${sms.otpCode} copied!", Toast.LENGTH_SHORT).show()
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF475569)),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PureWhite),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Copy", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Button(
                                    onClick = {
                                        if (showForgotPasswordDialog) {
                                            forgotOtp = sms.otpCode
                                            incomingSms = null
                                            Toast.makeText(context, "OTP Auto-filled into reset form!", Toast.LENGTH_SHORT).show()
                                        } else {
                                            regOtp = sms.otpCode
                                            val verified = HotelRepository.verifyOtp(regPhone, regOtp)
                                            if (verified) {
                                                isOtpVerified = true
                                                errorMessage = null
                                                statusSuccessMessage = "Phone verified successfully! ✓"
                                            }
                                            incomingSms = null
                                            Toast.makeText(context, "OTP Auto-filled & Phone Verified!", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Icon(Icons.Default.FlashOn, contentDescription = null, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Auto-fill OTP", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // App Brand Logo & Title
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(EmeraldLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Hotel,
                    contentDescription = null,
                    tint = EmeraldPrimary,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Star Hotel Odisha",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Text(
                text = "Official Odisha Hotels & Room Booking System",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Tab Switcher (Login vs Register)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(24.dp)),
                color = SurfaceLight
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(4.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (!isRegisterMode) EmeraldPrimary else Color.Transparent)
                            .clickable {
                                isRegisterMode = false
                                errorMessage = null
                                statusSuccessMessage = null
                            }
                            .testTag("login_tab_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Login",
                            fontWeight = FontWeight.Bold,
                            color = if (!isRegisterMode) PureWhite else TextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(4.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isRegisterMode) EmeraldPrimary else Color.Transparent)
                            .clickable {
                                isRegisterMode = true
                                errorMessage = null
                                statusSuccessMessage = null
                            }
                            .testTag("register_tab_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Customer Register",
                            fontWeight = FontWeight.Bold,
                            color = if (isRegisterMode) PureWhite else TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Database Security Badge
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = EmeraldLight,
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Encrypted Database Security: SHA-256 cryptographically hashed credentials.",
                        style = MaterialTheme.typography.bodySmall,
                        color = EmeraldDark,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Status message
            if (statusSuccessMessage != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = EmeraldLight,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = statusSuccessMessage ?: "",
                        color = EmeraldDark,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Error Message
            if (errorMessage != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = RedCancelBg,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = errorMessage ?: "",
                        color = RedCancel,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (!isRegisterMode) {
                // ================= UNIFIED LOGIN FORM =================
                OutlinedTextField(
                    value = loginEmailOrPhone,
                    onValueChange = { loginEmailOrPhone = it },
                    label = { Text("Email, Phone or Registration Number") },
                    placeholder = { Text("Enter your registered ID / mobile / email") },
                    leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null, tint = EmeraldPrimary) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_email_input"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = BorderMedium,
                        focusedLabelColor = EmeraldPrimary
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = loginPassword,
                    onValueChange = { loginPassword = it },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null, tint = EmeraldPrimary) },
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle password visibility"
                            )
                        }
                    },
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_password_input"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = BorderMedium,
                        focusedLabelColor = EmeraldPrimary
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Forgot Password Clickable
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = {
                            forgotIdentifier = loginEmailOrPhone
                            showForgotPasswordDialog = true
                        },
                        modifier = Modifier.testTag("forgot_password_btn")
                    ) {
                        Text(
                            text = "Forgot Password?",
                            color = EmeraldDark,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                EmeraldPillButton(
                    text = "Sign In & Proceed",
                    onClick = {
                        if (loginEmailOrPhone.isBlank() || loginPassword.isBlank()) {
                            errorMessage = "Please enter your Email/Phone and Password to login."
                        } else {
                            val success = HotelRepository.login(loginEmailOrPhone, loginPassword)
                            if (success) {
                                onAuthSuccess()
                            } else {
                                errorMessage = "Invalid login credentials. Please verify your password."
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "login_submit_btn"
                )

            } else {
                // ================= REGISTER CUSTOMER FORM WITH OTP =================
                OutlinedTextField(
                    value = regName,
                    onValueChange = { regName = it },
                    label = { Text("Customer Full Name (as per ID)") },
                    placeholder = { Text("e.g. Ramesh Mohapatra") },
                    leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null, tint = EmeraldPrimary) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("register_name_input"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = BorderMedium,
                        focusedLabelColor = EmeraldPrimary
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Phone Number with Send OTP Action
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = regPhone,
                        onValueChange = {
                            regPhone = it
                            isOtpVerified = false
                        },
                        label = { Text("Mobile Number") },
                        placeholder = { Text("10-digit number") },
                        prefix = { Text("+91 ", color = TextPrimary, fontWeight = FontWeight.SemiBold) },
                        leadingIcon = { Icon(Icons.Outlined.Phone, contentDescription = null, tint = EmeraldPrimary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("register_phone_input"),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = BorderMedium,
                            focusedLabelColor = EmeraldPrimary
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            triggerOtpDispatch(regPhone, isForgot = false)
                        },
                        enabled = resendCooldown == 0 && !isOtpVerified,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isOtpVerified) EmeraldDark else EmeraldPrimary,
                            disabledContainerColor = SurfaceLight
                        ),
                        modifier = Modifier
                            .height(56.dp)
                            .testTag("send_otp_btn")
                    ) {
                        Text(
                            text = when {
                                isOtpVerified -> "Verified ✓"
                                resendCooldown > 0 -> "${resendCooldown}s"
                                isOtpSent -> "Resend"
                                else -> "Get OTP"
                            },
                            color = if (resendCooldown > 0 && !isOtpVerified) TextMuted else PureWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // OTP Verification & SMS Status Card
                if (isOtpSent && !isOtpVerified) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = SurfaceLight,
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.35f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Sms,
                                    contentDescription = null,
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "SMS OTP delivered to +91 ${regPhone.filter { it.isDigit() }.takeLast(10)}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = EmeraldDark
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = regOtp,
                                    onValueChange = { 
                                        if (it.length <= 6) regOtp = it 
                                    },
                                    label = { Text("Enter 6-digit OTP") },
                                    placeholder = { Text("e.g. 123456") },
                                    leadingIcon = { Icon(Icons.Outlined.Pin, contentDescription = null, tint = EmeraldPrimary) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("register_otp_input"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = EmeraldPrimary,
                                        unfocusedBorderColor = BorderMedium,
                                        focusedLabelColor = EmeraldPrimary
                                    )
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Button(
                                    onClick = {
                                        if (HotelRepository.verifyOtp(regPhone, regOtp)) {
                                            isOtpVerified = true
                                            errorMessage = null
                                            statusSuccessMessage = "Mobile number verified successfully! ✓"
                                            Toast.makeText(context, "Mobile Verified Successfully!", Toast.LENGTH_SHORT).show()
                                        } else {
                                            errorMessage = "Invalid OTP. Please check your SMS or click Auto-fill."
                                        }
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                    modifier = Modifier
                                        .height(56.dp)
                                        .testTag("verify_otp_btn")
                                ) {
                                    Text("Verify", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else if (isOtpVerified) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = EmeraldLight,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Mobile Number Verified: +91 ${regPhone.filter { it.isDigit() }.takeLast(10)} ✓",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldDark
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = regEmail,
                    onValueChange = { regEmail = it },
                    label = { Text("Email Address") },
                    placeholder = { Text("your.email@example.com") },
                    leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null, tint = EmeraldPrimary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("register_email_input"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = BorderMedium,
                        focusedLabelColor = EmeraldPrimary
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // District Dropdown
                ExposedDropdownMenuBox(
                    expanded = isDistrictDropdownExpanded,
                    onExpandedChange = { isDistrictDropdownExpanded = !isDistrictDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedDistrict,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Odisha District") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDistrictDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = BorderMedium,
                            focusedLabelColor = EmeraldPrimary
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = isDistrictDropdownExpanded,
                        onDismissRequest = { isDistrictDropdownExpanded = false }
                    ) {
                        districts.forEach { dist ->
                            DropdownMenuItem(
                                text = { Text(dist) },
                                onClick = {
                                    selectedDistrict = dist
                                    selectedCity = HotelRepository.odishaCitiesByDistrict[dist]?.firstOrNull { it != "All Cities" } ?: dist
                                    isDistrictDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = regPassword,
                    onValueChange = { regPassword = it },
                    label = { Text("Set Secure Password") },
                    leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null, tint = EmeraldPrimary) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("register_password_input"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = BorderMedium,
                        focusedLabelColor = EmeraldPrimary
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                EmeraldPillButton(
                    text = "Complete Registration & Proceed",
                    onClick = {
                        if (regName.isBlank() || regEmail.isBlank() || regPassword.isBlank() || regPhone.isBlank()) {
                            errorMessage = "Please enter your name, phone number, email, and password to register."
                        } else if (!isOtpVerified) {
                            errorMessage = "Please enter the SMS OTP sent to your mobile phone and tap Verify."
                        } else {
                            val success = HotelRepository.register(
                                name = regName,
                                email = regEmail,
                                password = regPassword,
                                phone = regPhone,
                                district = selectedDistrict,
                                city = selectedCity
                            )
                            if (success) {
                                onAuthSuccess()
                            } else {
                                errorMessage = "Registration failed. Please check inputs."
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "register_submit_btn"
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }

        // ================= FORGOT PASSWORD DIALOG =================
        if (showForgotPasswordDialog) {
            AlertDialog(
                onDismissRequest = { showForgotPasswordDialog = false },
                containerColor = PureWhite,
                shape = RoundedCornerShape(20.dp),
                title = {
                    Text(
                        text = "Reset Password",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Enter your registered email or phone to receive a reset OTP.",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )

                        if (forgotDialogError != null) {
                            Text(
                                text = forgotDialogError ?: "",
                                color = RedCancel,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        OutlinedTextField(
                            value = forgotIdentifier,
                            onValueChange = { forgotIdentifier = it },
                            label = { Text("Registered Email or Mobile") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        if (!isForgotOtpSent) {
                            Button(
                                onClick = {
                                    if (forgotIdentifier.isBlank()) {
                                        forgotDialogError = "Please enter your phone number or email."
                                    } else {
                                        triggerOtpDispatch(forgotIdentifier, isForgot = true)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Send SMS Reset OTP")
                            }
                        } else {
                            OutlinedTextField(
                                value = forgotOtp,
                                onValueChange = { forgotOtp = it },
                                label = { Text("Enter OTP Code") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            OutlinedTextField(
                                value = forgotNewPassword,
                                onValueChange = { forgotNewPassword = it },
                                label = { Text("New Password") },
                                singleLine = true,
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Button(
                                onClick = {
                                    val ok = HotelRepository.resetPassword(forgotIdentifier, forgotNewPassword, forgotOtp)
                                    if (ok) {
                                        showForgotPasswordDialog = false
                                        statusSuccessMessage = "Password reset successfully! Please log in."
                                        Toast.makeText(context, "Password updated successfully!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        forgotDialogError = "Invalid OTP or password length too short (min 4 chars)."
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Update Password")
                            }
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showForgotPasswordDialog = false }) {
                        Text("Cancel", color = TextSecondary)
                    }
                }
            )
        }
    }
}
