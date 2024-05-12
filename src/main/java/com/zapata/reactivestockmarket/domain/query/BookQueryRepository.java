package com.zapata.reactivestockmarket.domain.query;

import com.zapata.reactivestockmarket.domain.engine.events.OrderCanceledEvent;
import com.zapata.reactivestockmarket.domain.engine.events.OrderMatchedEvent;
import com.zapata.reactivestockmarket.domain.engine.events.OrderPlacedEvent;
import com.zapata.reactivestockmarket.cqrs.Event;
import com.zapata.reactivestockmarket.cqrs.QueryRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.zapata.reactivestockmarket.Config.DEFAULT_CONCURRENCY_LEVEL;

/**
 * Thread-safe implementation of {@link QueryRepository} used to store order & book projections.
 *¿
 */
@Component
public class BookQueryRepository implements QueryRepository<com.zapata.reactivestockmarket.domain.query.OrderEntry> {

    private final ConcurrentHashMap<Long, com.zapata.reactivestockmarket.domain.query.OrderEntry> projection = new ConcurrentHashMap<>(32,0.75f,DEFAULT_CONCURRENCY_LEVEL);

    /**
     * Returns current order projection
     *
     * @param orderId - order identifier
     * @return - materialized projection
     */
    @Override
    public Mono<com.zapata.reactivestockmarket.domain.query.OrderEntry> getProjection(long orderId) {
        return Mono.fromCallable(() -> projection.get(orderId));
    }

    /**
     * Updates projection with event. Repository uses this event to create/maintain projections.
     *
     * @param event - materialized event
     */
    @Override
    public Mono<Void> updateProjection(Event event) {
        return (switch (event) {
            case OrderPlacedEvent evt -> handleOrderPlacedEvent(evt);
            case OrderMatchedEvent evt -> handleOrderMatchedEvent(evt);
            case OrderCanceledEvent evt -> handleOrderCanceledEvent(evt);
            default -> Mono.empty();
        }).subscribeOn(Schedulers.parallel())
          .then();
    }

    private Mono<com.zapata.reactivestockmarket.domain.query.OrderEntry> handleOrderPlacedEvent(OrderPlacedEvent evt) {
        return Mono.fromCallable(() -> projection.computeIfAbsent(evt.orderId(), orderId ->
                new com.zapata.reactivestockmarket.domain.query.OrderEntry(orderId,
                               evt.timestamp(),
                               evt.aggregateId(),
                               evt.price(),
                               evt.amount(),
                               evt.orderType(),
                               new CopyOnWriteArrayList<>(),
                               evt.amount())));
    }

    private Mono<com.zapata.reactivestockmarket.domain.query.OrderEntry> handleOrderMatchedEvent(OrderMatchedEvent evt) {
        return Mono.fromCallable(() -> {
            //update previous
            projection.computeIfPresent(evt.restingId(), (key, order) -> {
                order.setPendingAmount(evt.restingRemainingAmount());
                order.trades().add(
                        new com.zapata.reactivestockmarket.domain.query.OrderTradeEntry(evt.incomingId(),
                                            evt.incomingAmount(),
                                            evt.restingPrice()));
                return order;
            });
            //enter new
            return projection.computeIfAbsent(evt.incomingId(), incomingId ->
                    new com.zapata.reactivestockmarket.domain.query.OrderEntry(incomingId,
                                   evt.entryTimestamp(),
                                   evt.aggregateId(),
                                   evt.incomingPrice(),
                                   evt.incomingAmount(),
                                   evt.orderType(),
                                   new CopyOnWriteArrayList<>(List.of(new com.zapata.reactivestockmarket.domain.query.OrderTradeEntry(
                                           evt.restingId(),
                                           evt.previousRestingAmount().subtract(evt.restingRemainingAmount()),
                                           evt.restingPrice()
                                   ))),
                                   evt.incomingAmount()
                                      .subtract(evt.previousRestingAmount().subtract(evt.restingRemainingAmount()))));
        });
    }

    private Mono<com.zapata.reactivestockmarket.domain.query.OrderEntry> handleOrderCanceledEvent(OrderCanceledEvent evt) {
        return Mono.fromCallable(() -> projection.computeIfPresent(evt.orderId(), (key, order) -> {
            order.setPendingAmount(evt.remainingAmount());
            return order;
        }));
    }
}
