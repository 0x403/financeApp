package com.example.TransactionService.service;

import transactionServiceModels.AbstractContent;
import transactionServiceModels.Asset;
import transactionServiceModels.Product;
import transactionServiceModels.Trade;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface TransactionService {

    CompletableFuture<String> createUser(Asset reqAsset) throws IOException;

    CompletableFuture<Asset> getAssetById(String id);

    CompletableFuture<Asset> updateAsset(String id, String reqBody) throws IOException;

    CompletableFuture<Product> getProductById(String id) throws IOException;

    CompletableFuture<Product> updateProduct(String id, String reqBody) throws IOException;

    CompletableFuture<List<AbstractContent>> searchResourcesByPhrase(String phrase, Integer page, Integer size);

    CompletableFuture<List<Asset>> getAllAssets(Integer page, Integer size);

    CompletableFuture<List<Product>> getAllProducts(Integer page, Integer size);

    CompletableFuture<List<Trade>> getAllTrades(Integer page, Integer size);

    CompletableFuture<List<String>> getSuggestedAssets(String keyword);
}
