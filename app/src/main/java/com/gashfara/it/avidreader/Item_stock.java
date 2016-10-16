package com.gashfara.it.avidreader;

import java.io.Serializable;

public class Item_stock implements Serializable {
    String title;
    String page;
    String quote;

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getPage() {
        return page;
    }
    public void setPage(String page) {
        this.page = page;
    }

    public String getQuote() {
        return quote;
    }
    public void setQuote(String quote) {
        this.quote = quote;
    }
}

