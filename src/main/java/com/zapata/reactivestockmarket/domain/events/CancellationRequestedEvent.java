package com.zapata.reactivestockmarket.domain.events;

import com.zapata.reactivestockmarket.cqrs.SourcingEvent;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Event that user requested cancellation of order
 * Not used - POC
 */
public record CancellationRequestedEvent(@NonNull String aggregateId, @NonNull UUID eventId,
                                         @NonNull long orderId, @NonNull Boolean cancelAll, @NonNull BigDecimal newAmount)
        implements SourcingEvent {

}
