package com.gashfara.it.avidreader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Item_library implements Serializable {
    String imageUrl;
    String isbn;
    String title;
    String author;
    String publisher;
    String purchaseUrl;
    String page;
    String status;
    List<Item_stockInLibrary> stocks;

    public Item_library() {
        this.imageUrl = "";
        this.isbn = "";
        this.title = "";
        this.author = "";
        this.publisher = "";
        this.purchaseUrl = "";
        this.page = "";
        this.status = "";
        this.stocks = new ArrayList<Item_stockInLibrary>();
    }

    public Item_library(String imageUrl, String title, String author, String publisher, String purchaseUrl) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.purchaseUrl = purchaseUrl;
        this.status = "";
    }

    public Item_library(String title, String author, String publisher, String status) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.status = status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getPurchaseUrl() {
        return purchaseUrl;
    }

    public String getStatus() {
        return status;
    }

    public List<Item_stockInLibrary> getStocks() {
        return stocks;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setStocks(List<Item_stockInLibrary> stocks) {
        this.stocks = stocks;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}