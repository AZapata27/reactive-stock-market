package com.zapata.reactivestockmarket.cqrs;

import java.util.UUID;

/**
 * Interface to define command
 *
 */
public interface Command {

    /**
     * Aggregate identifier that is used to uniquely represent asset
     *
     * @return unique aggregate identifier
     */
    String aggregateId();

    /**
     * Uniquely identifies command
     *
     * @return unique command identifier
     */
    UUID commandId();
}
