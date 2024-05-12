package com.zapata.reactivestockmarket.domain.command;

import com.zapata.reactivestockmarket.cqrs.Command;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Command to request order cancellation
 */
public record CancelOrderCommand(@NonNull String aggregateId, @NonNull UUID commandId, @NonNull long orderId,
                                 @NonNull Boolean cancelAll, @NonNull BigDecimal newAmount)
        implements Command {

}
