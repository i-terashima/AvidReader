package com.gashfara.it.avidreader;

import java.io.Serializable;

public class Item_stock implements Serializable {
    String stock_tag;
    int stock_page;
    String stock_quote;
    String stock_memo;
    String isbn;
    String title;
    String author;
    String publisher;

    public Item_stock() {
        this.stock_tag = "";
        this.stock_page = 0;
        this.stock_quote = "";
        this.stock_memo = "";
        this.isbn = "";
        this.title = "";
        this.author = "";
        this.publisher = "";
    }

    public String getStock_tag() {
        return stock_tag;
    }

    public void setStock_tag(String stock_tag) {
        this.stock_tag = stock_tag;
    }

    public int getStock_page() {
        return stock_page;
    }

    public void setStock_page(int stock_page) {
        this.stock_page = stock_page;
    }

    public String getStock_quote() {
        return stock_quote;
    }

    public void setStock_quote(String stock_quote) {
        this.stock_quote = stock_quote;
    }

    public String getStock_memo() {
        return stock_memo;
    }

    public void setStock_memo(String stock_memo) {
        this.stock_memo = stock_memo;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
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
}

