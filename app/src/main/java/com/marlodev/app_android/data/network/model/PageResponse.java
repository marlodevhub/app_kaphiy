package com.marlodev.app_android.data.network.model;

import java.util.List;

public class PageResponse<T> {
    public List<T> content;
    public int totalPages;
    public int number; // número de página actual (0-indexed)
    public int size;
    public long totalElements;
}