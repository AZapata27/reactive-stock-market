package com.zapata.reactivestockmarket.domain.query;

import java.math.BigDecimal;

public record OrderTradeEntry(
        long orderId,
        BigDecimal amount,
        BigDecimal price
) {

}
