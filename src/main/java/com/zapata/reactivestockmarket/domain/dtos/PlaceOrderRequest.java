package com.zapata.reactivestockmarket.domain.dtos;

import com.zapata.reactivestockmarket.domain.query.OrderType;

public record PlaceOrderRequest(String asset, double price, double amount, OrderType direction) {}
