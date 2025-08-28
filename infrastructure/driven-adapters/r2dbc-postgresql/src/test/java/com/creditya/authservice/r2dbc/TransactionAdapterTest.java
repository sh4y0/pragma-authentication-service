package com.creditya.authservice.r2dbc;

import com.creditya.authservice.r2dbc.transaction_adapter.TransactionAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class TransactionAdapterTest {

    @Mock
    private TransactionalOperator transactionalOperator;

    private TransactionAdapter transactionAdapter;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        transactionAdapter = new TransactionAdapter(transactionalOperator);
    }

    @Test
    @DisplayName("Should execute Mono successfully within transaction")
    void executeInTransaction_success() {
        String resultValue = "success";
        Mono<String> action = Mono.just(resultValue);

        when(transactionalOperator.transactional(action)).thenReturn(Mono.just(resultValue));

        StepVerifier.create(transactionAdapter.executeInTransaction(action))
                .expectNext(resultValue)
                .verifyComplete();

        verify(transactionalOperator, times(1)).transactional(action);
    }

    @Test
    @DisplayName("Should propagate error within transaction")
    void executeInTransaction_error() {
        RuntimeException exception = new RuntimeException("Transaction failed");
        Mono<String> action = Mono.error(exception);

        when(transactionalOperator.transactional(action)).thenReturn(Mono.error(exception));

        StepVerifier.create(transactionAdapter.executeInTransaction(action))
                .expectErrorMatches(e -> e instanceof RuntimeException
                        && e.getMessage().equals("Transaction failed"))
                .verify();

        verify(transactionalOperator, times(1)).transactional(action);
    }
}
