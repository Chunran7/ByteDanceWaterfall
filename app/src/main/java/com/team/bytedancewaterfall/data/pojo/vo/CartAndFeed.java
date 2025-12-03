package com.team.bytedancewaterfall.data.pojo.vo;

import com.team.bytedancewaterfall.data.pojo.entity.FeedItem;

public class CartAndFeed extends FeedItem {
    // 购物车中商品数量
    private String id;
    private Integer count;
    private String totalPrice;
    private String productId;
    private String productName;
    private String productDesc;
    private Double price;
    private Double originalPrice;
    private String shopName;
    private boolean isSelected = false;
    private boolean hasDiscount = false;
    private String discountInfo;
    private Integer stock;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public CartAndFeed() {
    }

    // 适配不同的属性名（兼容Title和Description）
    @Override
    public String getTitle() {
        return productName != null ? productName : super.getTitle();
    }

    @Override
    public void setTitle(String title) {
        this.productName = title;
        super.setTitle(title);
    }

    @Override
    public String getDescription() {
        return productDesc != null ? productDesc : super.getDescription();
    }

    @Override
    public void setDescription(String description) {
        this.productDesc = description;
        super.setDescription(description);
    }

    // 适配字符串类型的价格
    @Override
    public String getPrice() {
        if (price != null) {
            return String.format("%.2f", price);
        }
        return super.getPrice() != null ? super.getPrice() : "0.00";
    }

    @Override
    public void setPrice(String priceStr) {
        try {
            this.price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            this.price = 0.0;
        }
        super.setPrice(priceStr);
    }

    public String getOriginalPrice() {
        return originalPrice != null ? String.format("%.2f", originalPrice) : "0.00";
    }

    public void setOriginalPrice(String originalPriceStr) {
        try {
            this.originalPrice = Double.parseDouble(originalPriceStr);
        } catch (NumberFormatException e) {
            this.originalPrice = 0.0;
        }
    }

    // 原始的价格getter和setter（保持兼容性）
    public Double getPriceDouble() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getOriginalPriceDouble() {
        return originalPrice;
    }

    public void setOriginalPrice(Double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean getHasDiscount() {
        return hasDiscount;
    }

    public void setHasDiscount(boolean hasDiscount) {
        this.hasDiscount = hasDiscount;
    }

    public String getDiscountInfo() {
        return discountInfo;
    }

    public void setDiscountInfo(String discountInfo) {
        this.discountInfo = discountInfo;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}
