package com.zapata.reactivestockmarket.domain.dtos;

import com.zapata.reactivestockmarket.domain.query.OrderType;
import lombok.Builder;

import java.util.List;

@Builder
public record OrderStatusResponse(
    long id,
    String timestamp,
    String asset,
    double price,
    double amount,
    OrderType direction,
    List<Trade> trades,
    double pendingAmount
) {}
