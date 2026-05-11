package com.example.Ap.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data @Builder
public class DashboardStats {
    private BigDecimal totalSales;
    private long      totalBooks;
    private long      newOrders;
}
