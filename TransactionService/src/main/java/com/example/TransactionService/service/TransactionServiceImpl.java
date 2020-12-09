package com.example.TransactionService.service;

import com.example.TransactionService.repository.TransactionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import transactionServiceModels.AbstractContent;
import transactionServiceModels.Asset;
import transactionServiceModels.Product;
import transactionServiceModels.Trade;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    TransactionRepository repo;

    @Override
    public CompletableFuture<String> createAsset(Asset reqAsset) throws IOException {

        if(reqAsset.getName() == null
                || reqAsset.getPrice() == null
                || reqAsset.getCurrency() == null) {
            throw new IOException("Bad JSON input");
        }

        reqAsset.setId(null);
        reqAsset.setLastPriceTime(ZonedDateTime.now());

        return repo.saveAsset(reqAsset);
    }


    @Override
    public CompletableFuture<Asset> getAssetById(String id) {
        return repo.findAssetById(id);
    }

    @Override
    public CompletableFuture<Asset> updateAsset(String id, String reqBody) throws IOException {
        Asset reqBodyAsset = mapper.readValue(reqBody, Asset.class);

        if(reqBodyAsset.getId() != null) {
            reqBodyAsset.setId(null);
        }
        else if(reqBodyAsset.getName().equals("")) {
            reqBodyAsset.setName(null);
        }
        else if(reqBodyAsset.getPrice() == 0.0) {
            reqBodyAsset.setPrice(null);
        }
        else if(reqBodyAsset.getPrice() != 0.0) {
            reqBodyAsset.setLastPriceTime(ZonedDateTime.now());
        }
        else if(reqBodyAsset.getPrice() == null || reqBodyAsset.getLastPriceTime() != null) {
            reqBodyAsset.setLastPriceTime(null);
        }
        else if(reqBodyAsset.getCurrency().equals("")) {
            reqBodyAsset.setCurrency(null);
        }

        return repo.updateAsset(id, reqBodyAsset);
    }

    @Override
    public CompletableFuture<Product> getProductById(String id) {
        return repo.findProductById(id);
    }

    @Override
    public CompletableFuture<Product> updateProduct(String id, String reqBody) throws IOException {
        Product reqBodyProduct = mapper.readValue(reqBody, Product.class);

        return repo.updateProduct(id, reqBodyProduct);
    }

    @Override
    public CompletableFuture<List<AbstractContent>> searchResourcesByPhrase(String phrase, Integer page, Integer size) {
        size = sizeValidator(size);
        page = pageValidator(page);

        return repo.findResourcesByPhrase(phrase, page, size);
    }

    @Override
    public CompletableFuture<List<Asset>> getAllAssets(Integer page, Integer size) {
        size = sizeValidator(size);
        page = pageValidator(page);

        return repo.findAllAssets(page, size);
    }

    @Override
    public CompletableFuture<List<Product>> getAllProducts(Integer page, Integer size) {
        size = sizeValidator(size);
        page = pageValidator(page);

        return repo.findAllProducts(page, size);
    }

    @Override
    public CompletableFuture<List<Trade>> getAllTrades(Integer page, Integer size) {
        size = sizeValidator(size);
        page = pageValidator(page);

        return repo.findAllTrades(page, size);
    }

    @Override
    public CompletableFuture<List<String>> getSuggestedAssetsAndProducts(String keyword) {
        return repo.findSuggestedAssetsAndProducts(keyword);
    }

    private Integer sizeValidator(Integer size) {
        if(size == null || size <= 0) {
            size = 10;
        }

        return size;
    }

    private Integer pageValidator(Integer page) {
        if(page == null || page <= 0) {
            page = 1;
        }

        return page;
    }
}
