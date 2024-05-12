package com.zapata.reactivestockmarket.domain;

import com.zapata.reactivestockmarket.domain.query.BookQueryRepository;
import org.junit.jupiter.api.*;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class BookAggregateRepositoryTest {

    private final BookAggregateRepository testSubject = new BookAggregateRepository(mock(BookQueryRepository.class));

    @Test
    public void loadOrCreate() {
        StepVerifier.create(testSubject.load("instrumentId"))
                .expectNextCount(1)
                .verifyComplete();
    }

}