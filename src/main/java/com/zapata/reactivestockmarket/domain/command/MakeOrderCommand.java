package com.zapata.reactivestockmarket.domain.command;

import com.zapata.reactivestockmarket.cqrs.Command;
import com.zapata.reactivestockmarket.domain.query.OrderType;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Command to place new order
 */
public record MakeOrderCommand(@NonNull String aggregateId, @NonNull UUID commandId,
                               @NonNull OrderType type, @NonNull BigDecimal amount, @NonNull BigDecimal price)
        implements Command {

}
