package com.team.bytedancewaterfall.data

import java.util.UUID

object Repository {

    fun getMockData(): List<FeedItem> {
        return listOf(
            // Item with price and description (e-commerce style)
            FeedItem(
                id = UUID.randomUUID().toString(),
                type = 0,
                imageUrl = "https://picsum.photos/id/10/300/400",
                title = "Stylish Watch",
                description = "A very stylish watch for modern people.",
                price = "$99.99",
                tags = listOf("Fashion", "Accessory", "Men's Style")
            ),
            // Item without price (social media style)
            FeedItem(
                id = UUID.randomUUID().toString(),
                type = 1,
                imageUrl = "https://picsum.photos/id/20/300/500",
                title = "Beautiful Landscape",
                description = "Captured this amazing view during my trip.",
                tags = listOf("Travel", "Nature", "Photography")
            ),
            // Another e-commerce item
            FeedItem(
                id = UUID.randomUUID().toString(),
                type = 0,
                imageUrl = "https://picsum.photos/id/30/300/450",
                title = "Comfortable Shoes",
                description = "Perfect for running and daily activities.",
                price = "$75.50",
                tags = listOf("Sports", "Running")
            ),
            // Another social media item
            FeedItem(
                id = UUID.randomUUID().toString(),
                type = 1,
                imageUrl = "https://picsum.photos/id/40/300/420",
                title = "City at Night",
                tags = listOf("Cityscape", "Night", "Urban")
            ),
            FeedItem(
                id = UUID.randomUUID().toString(),
                type = 1,
                imageUrl = "https://picsum.photos/id/50/300/480",
                title = "Delicious Food",
                description = "Best pasta I've ever had!",
                tags = listOf("Food", "Recipe", "Italian")
            ),
            // Item with a long title to test text wrapping
            FeedItem(
                id = UUID.randomUUID().toString(),
                type = 0,
                imageUrl = "https://picsum.photos/id/60/300/430",
                title = "A Very Long Title That Is Meant to Wrap Around to Multiple Lines",
                price = "$12.00"
            ),
        )
    }
}
