package com.example.TransactionService.repository;

import lombok.SneakyThrows;
import org.elasticsearch.action.ActionListener;

@FunctionalInterface
public interface ElasticActionListener<T> extends ActionListener<T> {

    @Override
    void onResponse(T response);

    @SneakyThrows
    @Override
    default void onFailure(Exception e) {
        throw e;
    }
}