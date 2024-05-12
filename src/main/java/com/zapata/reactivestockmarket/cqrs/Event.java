package com.zapata.reactivestockmarket.cqrs;

/**
 * Interface to define event
 */
public interface Event {

    /**
     * Aggregate identifier that is used to uniquely represent asset
     *
     * @return unique aggregate identifier
     */
    String aggregateId();

}
