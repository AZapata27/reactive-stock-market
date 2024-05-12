package com.zapata.reactivestockmarket.domain.engine.events;

import com.zapata.reactivestockmarket.cqrs.UpdateEvent;
import com.zapata.reactivestockmarket.domain.query.OrderType;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Update event that signals that order has been matched.
 *
 */
public record OrderMatchedEvent(
        long restingId,
        String aggregateId,
        Instant entryTimestamp,
        long incomingId,
        OrderType orderType,
        BigDecimal incomingPrice,
        BigDecimal restingPrice,
        BigDecimal incomingAmount,
        BigDecimal previousRestingAmount,
        BigDecimal restingRemainingAmount) implements UpdateEvent {

}
