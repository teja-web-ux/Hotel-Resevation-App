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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.data.HotelRepository
import com.example.model.HotelRoom
import com.example.ui.components.EmeraldPillButton
import com.example.ui.theme.*
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminRoomsScreen(
    onBack: () -> Unit
) {
    val rooms by HotelRepository.rooms.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = PureWhite,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Manage Hotel Rooms",
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
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Room", tint = EmeraldPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PureWhite)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = EmeraldPrimary,
                contentColor = PureWhite,
                modifier = Modifier.testTag("admin_add_room_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Room")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(rooms) { room ->
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
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AsyncImage(
                            model = room.imageUrl,
                            contentDescription = room.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(84.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(room.category, style = MaterialTheme.typography.labelSmall, color = EmeraldDark, fontWeight = FontWeight.Bold)
                                Switch(
                                    checked = room.isAvailable,
                                    onCheckedChange = {
                                        HotelRepository.updateRoom(room.copy(isAvailable = !room.isAvailable))
                                    },
                                    colors = SwitchDefaults.colors(checkedThumbColor = EmeraldPrimary, checkedTrackColor = EmeraldContainer),
                                    modifier = Modifier.height(24.dp)
                                )
                            }

                            Text(room.name, style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
                            Text("$${room.pricePerNight.toInt()}/night • ${room.location}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (room.isAvailable) "Active & Bookable" else "Under Maintenance",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (room.isAvailable) EmeraldDark else RedCancel
                                )

                                IconButton(
                                    onClick = { HotelRepository.deleteRoom(room.id) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = RedCancel, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            AddRoomDialog(
                onDismiss = { showAddDialog = false },
                onAddRoom = { newRoom ->
                    HotelRepository.addRoom(newRoom)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
private fun AddRoomDialog(
    onDismiss: () -> Unit,
    onAddRoom: (HotelRoom) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("Paris, France") }
    var priceStr by remember { mutableStateOf("35") }
    var category by remember { mutableStateOf("Recommended") }
    var imageUrl by remember { mutableStateOf("https://images.unsplash.com/photo-1590490360182-c33d57733427?w=800&q=80") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PureWhite,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text("Add New Hotel Suite", color = TextPrimary, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                FormInputItem("Hotel Name", name, onValueChange = { name = it })
                FormInputItem("Location", location, onValueChange = { location = it })
                FormInputItem("Price ($/night)", priceStr, onValueChange = { priceStr = it })
                FormInputItem("Category", category, onValueChange = { category = it })
                FormInputItem("Photo URL", imageUrl, onValueChange = { imageUrl = it })
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val newRoom = HotelRoom(
                            id = "H-${UUID.randomUUID().toString().take(4)}",
                            name = name,
                            category = category,
                            location = location,
                            pricePerNight = priceStr.toDoubleOrNull() ?: 35.0,
                            imageUrl = imageUrl
                        )
                        onAddRoom(newRoom)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text("Add Hotel", color = PureWhite)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextMuted)
            }
        }
    )
}
