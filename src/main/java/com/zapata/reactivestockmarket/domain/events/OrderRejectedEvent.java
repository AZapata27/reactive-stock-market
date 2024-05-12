package com.zapata.reactivestockmarket.domain.events;

import com.zapata.reactivestockmarket.cqrs.SourcingEvent;
import com.zapata.reactivestockmarket.domain.query.OrderType;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Event that marks that order didn't pass validation.
 * Not used - POC
 *
 */
public record OrderRejectedEvent(@NonNull String aggregateId, @NonNull UUID eventId,
                                 @NonNull OrderType type, @NonNull BigDecimal amount,
                                 @NonNull BigDecimal price, @NonNull String cause)
        implements SourcingEvent {

}
