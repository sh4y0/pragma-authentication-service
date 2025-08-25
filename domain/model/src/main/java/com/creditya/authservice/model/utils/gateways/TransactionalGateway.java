package com.creditya.authservice.model.utils.gateways;

import reactor.core.publisher.Mono;

public interface TransactionalGateway {
    <T> Mono<T> executeInTransaction(Mono<T> action);
}
