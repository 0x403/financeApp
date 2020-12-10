package com.example.TransactionService.repository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.elasticsearch.action.ActionListener;

import java.util.concurrent.CompletableFuture;

@Getter
@AllArgsConstructor
public abstract class ElasticActionListener<T, R> implements ActionListener<T> {

    CompletableFuture<R> future;

    @Override
    public abstract void onResponse(T response);

    @Override
    public void onFailure(Exception e) {
        future.completeExceptionally(e);
    }
}