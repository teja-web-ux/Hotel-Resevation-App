package com.example.model

data class HotelReview(
    val id: String,
    val author: String,
    val avatarUrl: String,
    val rating: Float,
    val date: String,
    val comment: String
)
