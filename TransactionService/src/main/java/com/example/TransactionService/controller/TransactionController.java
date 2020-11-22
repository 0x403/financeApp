package com.example.TransactionService.controller;

import com.example.TransactionService.repository.TransactionRepository;
import com.example.TransactionService.service.TransactionService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.PlainActionFuture;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import transactionServiceModels.AbstractContent;
import transactionServiceModels.Asset;
import transactionServiceModels.Product;
import transactionServiceModels.Trade;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@RestController
@RequestMapping
public class TransactionController {

    @Autowired
    RestHighLevelClient client;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    TransactionService transactionService;

    // add new asset
    @PostMapping("/assets")
    public String postAsset(@RequestBody String reqBody) throws IOException {
        return transactionService.createUser(reqBody);
    }

    // get asset by id
    @GetMapping("/assets/{id}")
    public Asset getAsset(@PathVariable("id") String id) throws IOException {
        return transactionService.getAssetById(id);
    }

    // update asset by id
    @PutMapping("/assets/{id}")
    public Asset putAsset(@PathVariable("id") String id, @RequestBody String reqBody) throws IOException {
        return transactionService.updateAsset(id, reqBody);
    }

    // get product by id
    @GetMapping("/products/{id}")
    public Product getProduct(@PathVariable String id) throws IOException {
        return transactionService.getProductById(id);
    }

    // update product by id
    @PutMapping("/products/{id}")
    public Product putProduct(@PathVariable String id, @RequestBody String reqBody) throws IOException {
        return transactionService.updateProduct(id, reqBody);
    }

//     search data by phrase
    @GetMapping("/search/{phrase}")
    public List<Asset> searchAsset(@PathVariable String phrase, @RequestParam(required = false) Integer page,
                                   @RequestParam(required = false) Integer size) {
        return transactionService.searchAssets(phrase, page, size);
    }

    // get assets || products || trades
    @GetMapping("/assets")
    public List<Asset> getAssets(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        return transactionService.getAllAssets(page, size);
    }

    @GetMapping("/products")
    public List<Product> getProducts(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        return transactionService.getAllProducts(page, size);
    }

    @GetMapping("/trades")
    public List<Trade> getTrades(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        return transactionService.getAllTrades(page, size);
    }

}
