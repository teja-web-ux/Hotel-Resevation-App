package com.example.ui.screens

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.HotelRepository
import com.example.model.HotelRoom
import com.example.ui.components.AppFilterChip
import com.example.ui.components.StarRatingBadge
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    onSelectHotel: (HotelRoom) -> Unit,
    onOpenAdmin: () -> Unit
) {
    val rooms by HotelRepository.rooms.collectAsState()
    val bankDetails by HotelRepository.bankDetails.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All Hotel") }
    var showFilterSheet by remember { mutableStateOf(false) }

    // Filter states
    var filterCountry by remember { mutableStateOf("All") }
    var filterSort by remember { mutableStateOf("Highest Popularity") }
    var filterMinPrice by remember { mutableFloatStateOf(20f) }
    var filterMaxPrice by remember { mutableFloatStateOf(100f) }
    var filterStars by remember { mutableIntStateOf(0) }

    val categories = listOf("All Hotel", "Recommended", "Popular", "Trending")

    val filteredHotels = remember(rooms, searchQuery, selectedCategory, filterCountry, filterStars, filterMaxPrice) {
        rooms.filter { hotel ->
            val matchesSearch = searchQuery.isBlank() ||
                    hotel.name.contains(searchQuery, ignoreCase = true) ||
                    hotel.location.contains(searchQuery, ignoreCase = true) ||
                    hotel.country.contains(searchQuery, ignoreCase = true)
            val matchesCategory = selectedCategory == "All Hotel" || hotel.category.equals(selectedCategory, ignoreCase = true)
            val matchesCountry = filterCountry == "All" || hotel.country.equals(filterCountry, ignoreCase = true)
            val matchesStars = filterStars == 0 || hotel.rating >= filterStars.toFloat()
            val matchesPrice = hotel.pricePerNight <= filterMaxPrice
            matchesSearch && matchesCategory && matchesCountry && matchesStars && matchesPrice
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PureWhite)
            .testTag("home_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Search Bar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Search Box
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        color = SurfaceLight,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = TextMuted,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            TextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Hotel", color = TextMuted, fontSize = 15.sp) },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                ),
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("home_hotel_search_input")
                            )
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Filter Button (Green Icon)
                    Surface(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { showFilterSheet = true }
                            .testTag("open_filter_button"),
                        color = EmeraldContainer,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Filter",
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            // Filter Pills Row (All Hotel, Recommended, Popular, Trending)
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(categories) { category ->
                        AppFilterChip(
                            text = category,
                            isSelected = selectedCategory == category,
                            onClick = { selectedCategory = category }
                        )
                    }
                }
            }

            // Recent Searches (if user is focused on searching)
            if (searchQuery.isEmpty() && selectedCategory == "All Hotel" && filterCountry == "All" && filterStars == 0) {
                item {
                    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Recent",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "CLEAR ALL",
                                style = MaterialTheme.typography.labelSmall,
                                color = EmeraldPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        val recents = HotelRepository.recentSearches
                        recents.forEach { query ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { searchQuery = query }
                                    .padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = query,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove",
                                    tint = TextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            HorizontalDivider(color = BorderLight)
                        }
                    }
                }
            }

            // Filtered count and View Mode toggle
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Filtered (${filteredHotels.size * 396})",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(
                            imageVector = Icons.Default.GridView,
                            contentDescription = "Grid View",
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.ViewAgenda,
                            contentDescription = "List View",
                            tint = TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Hotel List Items (Matching Screen 3 in Mockup)
            items(filteredHotels) { hotel ->
                HotelListItem(
                    hotel = hotel,
                    onClick = { onSelectHotel(hotel) },
                    onToggleBookmark = { HotelRepository.toggleBookmark(hotel.id) }
                )
            }
        }

        // Filter Sheet Dialog
        if (showFilterSheet) {
            FilterHotelModal(
                selectedCountry = filterCountry,
                onCountryChange = { filterCountry = it },
                selectedSort = filterSort,
                onSortChange = { filterSort = it },
                minPrice = filterMinPrice,
                maxPrice = filterMaxPrice,
                onPriceRangeChange = { min, max ->
                    filterMinPrice = min
                    filterMaxPrice = max
                },
                selectedStars = filterStars,
                onStarsChange = { filterStars = it },
                onReset = {
                    filterCountry = "All"
                    filterSort = "Highest Popularity"
                    filterMinPrice = 20f
                    filterMaxPrice = 100f
                    filterStars = 0
                },
                onApply = { showFilterSheet = false },
                onDismiss = { showFilterSheet = false }
            )
        }
    }
}

@Composable
private fun HotelListItem(
    hotel: HotelRoom,
    onClick: () -> Unit,
    onToggleBookmark: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, BorderLight, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        color = PureWhite,
        shadowElevation = 2.dp,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Hotel Photo (Left)
            AsyncImage(
                model = hotel.imageUrl,
                contentDescription = hotel.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(16.dp))
            )

            Spacer(modifier = Modifier.width(14.dp))

            // Hotel Info (Right)
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = hotel.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = hotel.location,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            maxLines = 1
                        )
                    }

                    // Price
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "$${hotel.pricePerNight.toInt()}",
                            style = MaterialTheme.typography.titleLarge,
                            color = EmeraldPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "/ night",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StarRatingBadge(rating = hotel.rating, reviewsCount = hotel.reviewCount)

                    IconButton(
                        onClick = onToggleBookmark,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (hotel.isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (hotel.isBookmarked) EmeraldPrimary else TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
