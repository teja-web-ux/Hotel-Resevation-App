package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.EmeraldPillButton
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterHotelModal(
    selectedCountry: String,
    onCountryChange: (String) -> Unit,
    selectedSort: String,
    onSortChange: (String) -> Unit,
    minPrice: Float,
    maxPrice: Float,
    onPriceRangeChange: (Float, Float) -> Unit,
    selectedStars: Int,
    onStarsChange: (Int) -> Unit,
    onReset: () -> Unit,
    onApply: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = PureWhite,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 28.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filter Hotel",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = BorderLight)
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // Country Filter
                item {
                    SectionHeaderWithSeeAll("Country")
                    Spacer(modifier = Modifier.height(10.dp))
                    val countries = listOf("All", "France", "Italia", "Turkiye", "Germany")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(countries) { c ->
                            FilterSelectChip(
                                text = c,
                                isSelected = selectedCountry == c,
                                onClick = { onCountryChange(c) }
                            )
                        }
                    }
                }

                // Sort Results
                item {
                    Text(
                        text = "Sort Results",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    val sorts = listOf("Highest Popularity", "Highest Price", "Lowest Price")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(sorts) { s ->
                            FilterSelectChip(
                                text = s,
                                isSelected = selectedSort == s,
                                onClick = { onSortChange(s) }
                            )
                        }
                    }
                }

                // Price Range Per Night
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Price Range Per Night",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$${minPrice.toInt()} - $${maxPrice.toInt()}",
                            style = MaterialTheme.typography.titleSmall,
                            color = EmeraldPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    RangeSlider(
                        value = minPrice..maxPrice,
                        onValueChange = { range ->
                            onPriceRangeChange(range.start, range.endInclusive)
                        },
                        valueRange = 20f..500f,
                        colors = SliderDefaults.colors(
                            thumbColor = EmeraldPrimary,
                            activeTrackColor = EmeraldPrimary,
                            inactiveTrackColor = BorderMedium
                        )
                    )
                }

                // Star Rating
                item {
                    Text(
                        text = "Star Rating",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        (5 downTo 1).forEach { stars ->
                            val isSelected = selectedStars == stars
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(50))
                                    .border(
                                        1.dp,
                                        if (isSelected) EmeraldPrimary else BorderMedium,
                                        RoundedCornerShape(50)
                                    )
                                    .clickable { onStarsChange(if (isSelected) 0 else stars) },
                                color = if (isSelected) EmeraldPrimary else PureWhite,
                                shape = RoundedCornerShape(50)
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = if (isSelected) PureWhite else YellowStar,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "$stars",
                                        color = if (isSelected) PureWhite else TextPrimary,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // Facilities
                item {
                    SectionHeaderWithSeeAll("Facilities")
                    Spacer(modifier = Modifier.height(10.dp))
                    val facilities = listOf("WiFi", "Swimming Pool", "Parking", "Restaurant")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        facilities.take(2).forEach { f ->
                            FacilityChip(name = f, isChecked = true, modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        facilities.drop(2).forEach { f ->
                            FacilityChip(name = f, isChecked = true, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
            HorizontalDivider(color = BorderLight)
            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Button(
                    onClick = onReset,
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldContainer,
                        contentColor = EmeraldDark
                    ),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("Reset", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                }

                Button(
                    onClick = onApply,
                    modifier = Modifier
                        .weight(1.3f)
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldPrimary,
                        contentColor = PureWhite
                    ),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("Apply Filter", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Composable
private fun SectionHeaderWithSeeAll(title: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "See All",
            style = MaterialTheme.typography.labelLarge,
            color = EmeraldPrimary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun FilterSelectChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .border(
                1.dp,
                if (isSelected) EmeraldPrimary else BorderMedium,
                RoundedCornerShape(50)
            )
            .clickable(onClick = onClick),
        color = if (isSelected) EmeraldPrimary else PureWhite,
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = text,
            color = if (isSelected) PureWhite else TextSecondary,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 9.dp)
        )
    }
}

@Composable
private fun FacilityChip(name: String, isChecked: Boolean, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .border(1.dp, BorderMedium, RoundedCornerShape(50)),
        color = PureWhite,
        shape = RoundedCornerShape(50)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = EmeraldPrimary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.labelMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
