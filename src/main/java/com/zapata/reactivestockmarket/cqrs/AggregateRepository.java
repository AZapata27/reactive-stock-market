package com.zapata.reactivestockmarket.cqrs;

import reactor.core.publisher.Mono;

/**
 * Represents repository that stores all aggregates
 *
 */
public interface AggregateRepository<T extends Aggregate> {

    Mono<T> load(String aggregateId);
}
