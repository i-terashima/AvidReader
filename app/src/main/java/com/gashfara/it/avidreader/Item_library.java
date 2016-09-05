package com.gashfara.it.avidreader;

public class Item_library {
    String imageUrl;
    String title;
    String author;
    String publisher;
    String purchaseUrl;
    String status;

    public Item_library() {
        this.imageUrl = "";
        this.title = "";
        this.author = "";
        this.publisher = "";
        this.purchaseUrl ="";
        this.status = "";
    }

    public Item_library(String imageUrl, String title, String author, String publisher, String purchaseUrl) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.purchaseUrl = purchaseUrl;
        this.status = "";
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

    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPurchaseUrl() {
        return purchaseUrl;
    }
    public void setPurchaseUrl(String purchaseUrl) {
        this.purchaseUrl = purchaseUrl;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}