package com.zapata.reactivestockmarket.domain.engine.events;

import com.zapata.reactivestockmarket.cqrs.UpdateEvent;
import com.zapata.reactivestockmarket.domain.query.OrderType;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Update event that signals that order has been placed but not yet matched.
 *
 */
public record OrderPlacedEvent(
        long orderId,
        String aggregateId,
        Instant timestamp,
        OrderType orderType,
        BigDecimal price,
        BigDecimal amount) implements UpdateEvent {

}
