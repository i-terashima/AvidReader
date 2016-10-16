package com.gashfara.it.avidreader;

/**
 * Created by it on 2016/10/16.
 */

public class Item_stockInLibrary {

    String stock_title;
    int stock_page;
    String stock_quote;
    String stock_memo;

    public Item_stockInLibrary() {
        this.stock_title = "";
        this.stock_page = 0;
        this.stock_quote = "";
        this.stock_memo = "";
    }

    public String getStock_title() {
        return stock_title;
    }

    public void setStock_title(String stock_title) {
        this.stock_title = stock_title;
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
}

