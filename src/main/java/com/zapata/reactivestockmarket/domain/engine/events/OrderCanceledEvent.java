package com.zapata.reactivestockmarket.domain.engine.events;

import com.zapata.reactivestockmarket.cqrs.UpdateEvent;
import com.zapata.reactivestockmarket.domain.query.OrderType;

import java.math.BigDecimal;

/**
 * Update event that signals that order has been canceled.
 *
 */
public record OrderCanceledEvent(
        long orderId,
        String aggregateId,
        OrderType orderType,
        BigDecimal canceledAmount,
        BigDecimal remainingAmount) implements UpdateEvent {

}