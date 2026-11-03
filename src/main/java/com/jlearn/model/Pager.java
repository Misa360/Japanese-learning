package com.jlearn.model;

import java.util.ArrayList;
import java.util.List;

/** Phan trang cho partials/pagination.jsp. page bat dau tu 1. */
public class Pager {
    private final int page;
    private final int size;
    private final long total;
    private final int totalPages;

    private Pager(int page, int size, long total) {
        this.size = size;
        this.total = total;
        this.totalPages = (int) Math.max(1, (total + size - 1) / size);
        this.page = Math.max(1, Math.min(page, totalPages));
    }

    public static Pager of(int requestedPage, int size, long total) { return new Pager(requestedPage, size, total); }

    public int getPage() { return page; }
    public int getSize() { return size; }
    public long getTotal() { return total; }
    public int getTotalPages() { return totalPages; }
    public int getOffset() { return (page - 1) * size; }
    public boolean isHasPrev() { return page > 1; }
    public boolean isHasNext() { return page < totalPages; }

    /** Cua so toi da 5 trang quanh trang hien tai. */
    public List<Integer> getWindow() {
        int from = Math.max(1, page - 2), to = Math.min(totalPages, page + 2);
        List<Integer> out = new ArrayList<>();
        for (int i = from; i <= to; i++) out.add(i);
        return out;
    }
}
