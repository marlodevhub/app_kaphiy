package com.marlodev.app_android.data.network.model.order;


import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private Long id;
    private Integer userId;
    private String username;
    private String status;
    private String message;
    private BigDecimal totalAmount;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private List<CartItemResponse> items;
}