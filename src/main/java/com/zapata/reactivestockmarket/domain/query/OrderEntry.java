package com.zapata.reactivestockmarket.domain.query;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class OrderEntry {

    private final long orderId;
    private final Instant entryTimestamp;
    private final String asset;
    private final BigDecimal price;
    private final BigDecimal amount;
    private final OrderType direction;
    private final List<com.zapata.reactivestockmarket.domain.query.OrderTradeEntry> trades;
    private BigDecimal pendingAmount;


    public OrderEntry(
            long orderId,
            Instant entryTimestamp,
            String asset,
            BigDecimal price,
            BigDecimal amount,
            OrderType direction,
            List<com.zapata.reactivestockmarket.domain.query.OrderTradeEntry> trades,
            BigDecimal pendingAmount) {
        this.orderId = orderId;
        this.entryTimestamp = entryTimestamp;
        this.asset = asset;
        this.price = price;
        this.amount = amount;
        this.direction = direction;
        this.trades = trades;
        this.pendingAmount = pendingAmount;
    }

    public void setPendingAmount(BigDecimal pendingAmount) {
        this.pendingAmount = pendingAmount;
    }

    public long orderId() {
        return orderId;
    }

    public Instant entryTimestamp() {
        return entryTimestamp;
    }

    public String asset() {
        return asset;
    }

    public BigDecimal price() {
        return price;
    }

    public BigDecimal amount() {
        return amount;
    }

    public OrderType direction() {
        return direction;
    }

    public List<com.zapata.reactivestockmarket.domain.query.OrderTradeEntry> trades() {
        return trades;
    }

    public BigDecimal pendingAmount() {
        return pendingAmount;
    }

}

