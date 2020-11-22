package com.example.TransactionService.service;

import transactionServiceModels.AbstractContent;
import transactionServiceModels.Asset;
import transactionServiceModels.Product;
import transactionServiceModels.Trade;

import java.io.IOException;
import java.util.List;

public interface TransactionService {

    String createUser(String reqBodyJSON) throws IOException;

    Asset getAssetById(String id);

    Asset updateAsset(String id, String reqBody) throws IOException;

    Product getProductById(String id) throws IOException;

    Product updateProduct(String id, String reqBody) throws IOException;

    List<Asset> searchAssets(String phrase, Integer page, Integer size);

    List<Asset> getAllAssets(Integer page, Integer size);

    List<Product> getAllProducts(Integer page, Integer size);

    List<Trade> getAllTrades(Integer page, Integer size);
}
