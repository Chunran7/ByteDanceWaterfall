package com.team.bytedancewaterfall.data.vurtualData;

import com.team.bytedancewaterfall.data.pojo.entity.FeedItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class FeedItemData {
    public static List<FeedItem> feedItemList;
    static{
        feedItemList = new ArrayList<>();
        feedItemList.add(new FeedItem(UUID.randomUUID().toString(), 0, "https://picsum.photos/id/10/300/400", "Stylish Watch", "A very stylish watch for modern people.", "$99.99", Arrays.asList("Fashion", "Accessory", "Men's Style"), (String)null));
        feedItemList.add(new FeedItem(UUID.randomUUID().toString(), 1, "https://picsum.photos/id/20/300/500", "Beautiful Landscape", "Captured this amazing view during my trip.", (String)null, Arrays.asList("Travel", "Nature", "Photography"), (String)null));
        feedItemList.add(new FeedItem(UUID.randomUUID().toString(), 0, "https://picsum.photos/id/30/300/450", "Comfortable Shoes", "Perfect for running and daily activities.", "$75.50", Arrays.asList("Sports", "Running"), (String)null));
        feedItemList.add(new FeedItem(UUID.randomUUID().toString(), 1, "https://picsum.photos/id/40/300/420", "City at Night", (String)null, (String)null, Arrays.asList("Cityscape", "Night", "Urban"), (String)null));
        feedItemList.add(new FeedItem(UUID.randomUUID().toString(), 1, "https://picsum.photos/id/50/300/480", "Delicious Food", "Best pasta I've ever had!", (String)null, Arrays.asList("Food", "Recipe", "Italian"), (String)null));
        feedItemList.add(new FeedItem(UUID.randomUUID().toString(), 0, "https://picsum.photos/id/60/300/430", "A Very Long Title That Is Meant to Wrap Around to Multiple Lines", (String)null, "$12.00", (List)null, (String)null));
        feedItemList.forEach(item-> System.out.println(item));
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
