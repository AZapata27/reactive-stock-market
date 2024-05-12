package com.zapata.reactivestockmarket.domain.dtos;

import lombok.Builder;

@Builder
public record Trade(long orderId, double amount, double price) {}
