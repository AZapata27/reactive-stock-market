package com.zapata.reactivestockmarket.web;

import com.zapata.reactivestockmarket.cqrs.Event;
import com.zapata.reactivestockmarket.cqrs.SourcingEvent;
import com.zapata.reactivestockmarket.domain.bus.CommandBus;
import com.zapata.reactivestockmarket.domain.command.CancelOrderCommand;
import com.zapata.reactivestockmarket.domain.command.MakeOrderCommand;
import com.zapata.reactivestockmarket.domain.dtos.OrderStatusResponse;
import com.zapata.reactivestockmarket.domain.dtos.PlaceOrderRequest;
import com.zapata.reactivestockmarket.domain.dtos.Trade;
import com.zapata.reactivestockmarket.domain.events.OrderAcceptedEvent;
import com.zapata.reactivestockmarket.domain.query.BookQueryRepository;
import com.zapata.reactivestockmarket.domain.query.OrderEntry;
import com.zapata.reactivestockmarket.domain.query.OrderType;
import com.zapata.reactivestockmarket.domain.Book;
import com.zapata.reactivestockmarket.domain.BookAggregateRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implements REST Endpoints to place, get or cancel order.
 *
 */
@RestController
public class MarketController {

    private final CommandBus commandBus;
    private final BookAggregateRepository bookAggregateRepository;
    private final BookQueryRepository bookQueryRepository;

    public MarketController(CommandBus commandBus,
                            BookAggregateRepository bookAggregateRepository,
                            BookQueryRepository bookQueryRepository) {
        this.commandBus = commandBus;
        this.bookAggregateRepository = bookAggregateRepository;
        this.bookQueryRepository = bookQueryRepository;
    }

    /**
     * Places order into trading system
     *
     * @param request user request to place order
     * @return order status
     */
    @PostMapping(value = "/orders", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<OrderStatusResponse> placeOrder(@RequestBody PlaceOrderRequest request) {
        return commandBus.sendCommand(toMakeOrderCommand(request))
                         .cast(OrderAcceptedEvent.class)
                         .flatMap(this::getOrderProjection)
                         .map(this::toOrderStatus);
    }

    /**
     * Not used - POC
     * Intended to UI or client applications to maintain their own projection
     *
     * @param asset
     * @return streams all events from aggregate
     */
    @GetMapping(value = "/book/{asset}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Event> bookEvents(@PathVariable String asset) {
        return bookAggregateRepository.load(asset)
                                      .flatMapMany(Book::aggregateEvents);
    }

    /**
     * Retrieves order from projection
     *
     * @param orderId - order identifier
     * @return order status
     */
    @GetMapping("/orders/{orderId}")
    public Mono<OrderStatusResponse> getOrder(@PathVariable Long orderId) {
        return bookQueryRepository.getProjection(orderId)
                                  .map(this::toOrderStatus);
    }

    /**
     * POC
     * Cancels pending order from book.
     * @param orderId - order identifier
     * @return response OK or error with error message
     */
    @PostMapping("/orders/{orderId}/cancel")
    public Mono<ResponseEntity<String>> cancelOrder(@PathVariable Long orderId) {
        return bookQueryRepository.getProjection(orderId)
                                  .flatMap(MarketController::validateOrderAmount)
                                  .flatMap(this::sendCancelCommand)
                                  .switchIfEmpty(Mono.error(new IllegalStateException(
                                          "You can't cancel non-existing order.")))
                                  .map(order -> ResponseEntity.accepted().body("OK"))
                                  .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().body(e.getMessage())));
    }

    private Mono<SourcingEvent> sendCancelCommand(OrderEntry order) {
        return commandBus.sendCommand(new CancelOrderCommand(order.asset(),
                                                             UUID.randomUUID(),
                                                             order.orderId(),
                                                             true,
                                                             BigDecimal.ZERO));
    }

    private Mono<? extends OrderEntry> getOrderProjection(OrderAcceptedEvent ev) {
        return bookQueryRepository.getProjection(ev.orderId())
                                  .repeatWhenEmpty(10, o -> o.delayElements(
                                          Duration.ofMillis(50)));
    }

    private MakeOrderCommand toMakeOrderCommand(PlaceOrderRequest request) {
        return new MakeOrderCommand(request.asset(),
                                    UUID.randomUUID(),
                                    OrderType.valueOf(request.direction().name()),
                                    BigDecimal.valueOf(request.amount()),
                                    BigDecimal.valueOf(request.price()));
    }

    private OrderStatusResponse toOrderStatus(OrderEntry order) {
        return OrderStatusResponse.builder()
                                  .id(order.orderId())
                                  .timestamp(order.entryTimestamp().toString())
                                  .asset(order.asset())
                                  .amount(order.amount().doubleValue())
                                  .price(order.price().doubleValue())
                                  .direction(OrderType.valueOf(
                                          order.direction().name()))
                                  .trades(order.trades().stream()
                                                     .map(t -> Trade.builder()
                                                                    .orderId(t.orderId())
                                                                    .price(t.price()
                                                                               .doubleValue())
                                                                    .amount(t.amount()
                                                                                .doubleValue())
                                                                    .build())
                                                     .toList())
                                  .pendingAmount(order.pendingAmount().doubleValue())
                                  .build();
    }

    private static Mono<? extends OrderEntry> validateOrderAmount(OrderEntry order) {
        if (order.price().compareTo(BigDecimal.ZERO) > 0) {
            return Mono.just(order);
        } else {
            return Mono.error(new IllegalStateException("Order already executed."));
        }
    }

}
