package com.example.cleanarch.application.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class PaginatedData<T> {
    private List<T> data;
    private int total;
    private Integer nextPage;
    private int currentPage;
    private boolean hasNext;
}