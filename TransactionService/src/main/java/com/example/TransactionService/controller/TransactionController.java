package com.example.TransactionService.controller;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class TransactionController {

    // get assets || products || trades
    @GetMapping("/assets")
    public List<Asset> getAssets() {
        return new List<Asset>();
    }

    @GetMapping("/products")
    public List<Product> getProducts() {
        return new List<Product>();
    }

    @GetMapping("/trades")
    public List<Trade> getTrades() {
        return new List<Trade>();
    }

    // add new asset
    @PostMapping("/assets")
    public String postAsset() {
        return "";
    }

    // get asset by id
    @GetMapping("/assets/{id}")
    public Asset getAsset(@PathVariable String id) {
        // TODO
    }

    // update existing asset
    @PutMapping("/assets/{id}")
    public Asset putAsset(@PathVariable String id) {
        // TODO
    }

    // get product by id
    @GetMapping("/products/{id}")
    public Product getProduct(@PathVariable String id) {
        // TODO
    }

    // update product by id
    @PutMapping("/products/{id}")
    public Product putProduct(@PathVariable String id) {
        // TODO
    }

    // search data by phrase
    @GetMapping("/search/{phrase}")
    public List<AbstractAsset> searchAsset(@PathVariable String phrase) {
        // TODO
    }

}
