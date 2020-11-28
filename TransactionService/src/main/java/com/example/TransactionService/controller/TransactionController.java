package com.example.TransactionService.controller;

import com.example.TransactionService.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import transactionServiceModels.AbstractContent;
import transactionServiceModels.Asset;
import transactionServiceModels.Product;
import transactionServiceModels.Trade;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;


@RestController
@RequestMapping
public class TransactionController {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    TransactionService transactionService;

    // add new asset
    @PostMapping("/assets")
    public CompletableFuture<String> postAsset(@RequestBody String reqBody) throws IOException {
        Asset reqBodyAsset = mapper.readValue(reqBody, Asset.class);
        return transactionService.createUser(reqBodyAsset);
    }

    // get asset by id
    @GetMapping("/assets/{id}")
    public CompletableFuture<Asset> getAsset(@PathVariable("id") String id) {
        return transactionService.getAssetById(id);
    }

    // update asset by id
    @PutMapping("/assets/{id}")
    public CompletableFuture<Asset> putAsset(@PathVariable("id") String id, @RequestBody String reqBody) throws IOException {
        return transactionService.updateAsset(id, reqBody);
    }

    // get product by id
    @GetMapping("/products/{id}")
    public CompletableFuture<Product> getProduct(@PathVariable String id) throws IOException {
        return transactionService.getProductById(id);
    }

    // update product by id
    @PutMapping("/products/{id}")
    public CompletableFuture<Product> putProduct(@PathVariable String id, @RequestBody String reqBody) throws IOException {
        return transactionService.updateProduct(id, reqBody);
    }

//     search data by phrase
    @GetMapping("/search")
    public CompletableFuture<List<AbstractContent>> searchAsset(@RequestParam String phrase, @RequestParam(required = false) Integer page,
                                   @RequestParam(required = false) Integer size) {
        return transactionService.searchResourcesByPhrase(phrase, page, size);
    }

    // get assets || products || trades
    @GetMapping("/assets")
    public CompletableFuture<List<Asset>> getAssets(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        return transactionService.getAllAssets(page, size);
    }

    @GetMapping("/products")
    public CompletableFuture<List<Product>> getProducts(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        return transactionService.getAllProducts(page, size);
    }

    @GetMapping("/trades")
    public CompletableFuture<List<Trade>> getTrades(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        return transactionService.getAllTrades(page, size);
    }

    @GetMapping("/assets/suggest")
    public CompletableFuture<List<String>> getSuggestedAssets(@RequestParam String keyword) {
        return transactionService.getSuggestedAssets(keyword);
    }

}
