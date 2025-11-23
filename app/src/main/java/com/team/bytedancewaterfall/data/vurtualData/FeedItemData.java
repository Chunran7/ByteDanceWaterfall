package com.team.bytedancewaterfall.data.vurtualData;

import com.team.bytedancewaterfall.data.entity.FeedItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class FeedItemData {
    public static List<FeedItem> feedItemList;
    public FeedItemData(){
        feedItemList = new ArrayList<>();
        // Item with price and description (e-commerce style)
        feedItemList.add(new FeedItem(
                UUID.randomUUID().toString(),
                0,
                "https://picsum.photos/id/10/300/400",
                "Stylish Watch",
                "A very stylish watch for modern people.",
                "$99.99",
                Arrays.asList("Fashion", "Accessory", "Men's Style"),
                null
        ));

        // Item without price (social media style)
        feedItemList.add(new FeedItem(
                UUID.randomUUID().toString(),
                1,
                "https://picsum.photos/id/20/300/500",
                "Beautiful Landscape",
                "Captured this amazing view during my trip.",
                null,
                Arrays.asList("Travel", "Nature", "Photography"),
                null
        ));

        // Another e-commerce item
        feedItemList.add(new FeedItem(
                UUID.randomUUID().toString(),
                0,
                "https://picsum.photos/id/30/300/450",
                "Comfortable Shoes",
                "Perfect for running and daily activities.",
                "$75.50",
                Arrays.asList("Sports", "Running"),
                null
        ));

        // Another social media item
        feedItemList.add(new FeedItem(
                UUID.randomUUID().toString(),
                1,
                "https://picsum.photos/id/40/300/420",
                "City at Night",
                null,
                null,
                Arrays.asList("Cityscape", "Night", "Urban"),
                null
        ));

        // Social media item with food
        feedItemList.add(new FeedItem(
                UUID.randomUUID().toString(),
                1,
                "https://picsum.photos/id/50/300/480",
                "Delicious Food",
                "Best pasta I've ever had!",
                null,
                Arrays.asList("Food", "Recipe", "Italian"),
                null
        ));

        // Item with a long title to test text wrapping
        feedItemList.add(new FeedItem(
                UUID.randomUUID().toString(),
                0,
                "https://picsum.photos/id/60/300/430",
                "A Very Long Title That Is Meant to Wrap Around to Multiple Lines",
                null,
                "$12.00",
                null,
                null
        ));

    }
    public static List<FeedItem> getFeedItemList() {
        return feedItemList;
    }
    public static boolean addFeedItem(FeedItem feedItem) {
        if (feedItem == null) {
            return false;
        }
        // 添加到列表中
        return feedItemList.add(feedItem);
    }
    public static boolean removeFeedItem(String id) {
        if (id == null) {
            return false;
        }
        // 从列表中移除
        return feedItemList.removeIf(item -> item.getId().equals(id));
    }
    public static boolean updateFeedItem(FeedItem feedItem) {
        if (feedItem == null) {
            return false;
        }
        // 在列表中找到匹配的项并更新
        for (int i = 0; i < feedItemList.size(); i++) {
            FeedItem item = feedItemList.get(i);
            if (item.getId().equals(feedItem.getId())) {
                feedItemList.set(i, feedItem);
                return true;
            }
        }
        return false;
    }
}
