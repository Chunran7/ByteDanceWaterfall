package com.team.bytedancewaterfall.data.pojo.entity;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 瀑布流数据项

 */

/**
 *  id 唯一标识，主键id
 *  type 卡片类型：0=双列商品, 1=大图活动卡, 2=视频商品
 *  imageUrl 内容图片
 *  title 标题
 *  description 描述 (可选)
 *  price 价格 (可选, 例如商品)
 *  tags 标签列表 (可选)
 *  videoUrl 视频地址 (可选, 例如视频商品)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedItem implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private int type;
    private String imageUrl;
    private String title;
    private String description;
    private String price;
    private List<String> tags;
    private String videoUrl;
    public FeedItem() {
    }
    public FeedItem(String id, int type, String imageUrl, String title, String description, String price, List<String> tags, String videoUrl) {
        this.id = id;
        this.type = type;
        this.imageUrl = imageUrl;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    @Override
    public String toString() {
        return "FeedItem{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", imageUrl='" + imageUrl + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", price='" + price + '\'' +
                ", tags=" + tags +
                ", videoUrl='" + videoUrl + '\'' +
                '}';
    }
}
