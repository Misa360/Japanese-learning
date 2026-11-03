package com.jlearn.model;

import java.util.ArrayList;
import java.util.List;

public class HistoryPage {
    private java.util.List<HistoryItem> items = new java.util.ArrayList<>();
    private Pager pager;

    public java.util.List<HistoryItem> getItems() { return items; }
    public void setItems(java.util.List<HistoryItem> items) { this.items = items; }
    public Pager getPager() { return pager; }
    public void setPager(Pager pager) { this.pager = pager; }
}
