package com.zapata.reactivestockmarket.domain.events;

import com.zapata.reactivestockmarket.cqrs.SourcingEvent;
import com.zapata.reactivestockmarket.domain.query.OrderType;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Event that marks that order has passed validation phase, and order id is generated to be used for tracker.
 *
 */
public record OrderAcceptedEvent(@NonNull String aggregateId, @NonNull UUID eventId, @NonNull long orderId,
                                 @NonNull OrderType type, @NonNull BigDecimal amount, @NonNull BigDecimal price, @NonNull Instant entryTimestamp)
        implements SourcingEvent {

}
