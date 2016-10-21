package com.gashfara.it.avidreader;

/**
 * Created by it on 2016/10/16.
 */

public class Item_stockInLibrary {

    String stock_tag;
    int stock_page;
    String stock_quote;
    String stock_memo;

    public Item_stockInLibrary() {
        this.stock_tag = "";
        this.stock_page = 0;
        this.stock_quote = "";
        this.stock_memo = "";
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
}

