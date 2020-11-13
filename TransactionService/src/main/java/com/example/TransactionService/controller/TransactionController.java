package com.example.TransactionService.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.GetSourceRequest;
import org.elasticsearch.client.core.GetSourceResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import transactionServiceModels.AbstractContent;
import transactionServiceModels.Asset;
import transactionServiceModels.Product;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping
public class TransactionController {

    @Autowired
    RestHighLevelClient client;

    // get assets || products || trades
//    @GetMapping("/assets")
//    public Page<Asset> getAssets(@RequestParam Optional<Integer> page) {
//        // TODO
//    }
//
//    @GetMapping("/products")
//    public Page<Product> getProducts(@RequestParam Optional<Integer> page) {
//        // TODO
//    }
//
//    @GetMapping("/trades")
//    public Page<Trade> getTrades(@RequestParam Optional<Integer> page) {
//        // TODO
//    }

    // add new asset
    @PostMapping("/assets")
    public String postAsset(@RequestBody String reqBody) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode reqBodyJSON = mapper.readTree(reqBody);
        ((ObjectNode) reqBodyJSON).put("lastPriceTime", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        IndexRequest request = new IndexRequest("assets").source(reqBodyJSON.toString(), XContentType.JSON);

        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        return response.status().toString() + "\nID: " + response.getId();
    }

    // get asset by id
    @GetMapping("/assets/{id}")
    public Asset getAsset(@PathVariable("id") String id) throws IOException {
        GetSourceRequest req = new GetSourceRequest("assets", id);
        GetSourceResponse res = client.getSource(req, RequestOptions.DEFAULT);

        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        return mapper.convertValue(res.getSource(), Asset.class);
    }

    // update asset by id
    @PutMapping("/assets/{id}")
    public Asset putAsset(@PathVariable("id") String id, @RequestBody String reqBody) throws IOException {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        JsonNode reqBodyJSON = mapper.readTree(reqBody);
        if(reqBodyJSON.has("price")) {
            ((ObjectNode) reqBodyJSON).put("lastPriceTime", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }

        UpdateRequest req = new UpdateRequest("assets", id).doc(reqBodyJSON.toString(), XContentType.JSON).fetchSource(true);
        UpdateResponse res = client.update(req, RequestOptions.DEFAULT);

        return mapper.convertValue(res.getGetResult().getSource(), Asset.class);
    }

    // get product by id
    @GetMapping("/products/{id}")
    public Product getProduct(@PathVariable String id) throws IOException {
        GetSourceRequest req = new GetSourceRequest("products", id);
        GetSourceResponse res = client.getSource(req, RequestOptions.DEFAULT);

        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        return mapper.convertValue(res.getSource(), Product.class);
    }

    // update product by id
    @PutMapping("/products/{id}")
    public Product putProduct(@PathVariable String id, @RequestBody String reqBody) throws IOException {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        JsonNode reqBodyJSON = mapper.readTree(reqBody);

        UpdateRequest req = new UpdateRequest("products", id).doc(reqBodyJSON.toString(), XContentType.JSON).fetchSource(true);
        UpdateResponse res = client.update(req, RequestOptions.DEFAULT);

        return mapper.convertValue(res.getGetResult().getSource(), Product.class);
    }

    // search data by phrase
//    @GetMapping("/search/{phrase}")
//    public List<AbstractContent> searchAsset(@PathVariable String phrase, @RequestParam Optional<Integer> page) {
//        SearchRequest searchRequest = new SearchRequest();
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        searchSourceBuilder.query(QueryBuilders.matchPhraseQuery());
//        searchRequest.source(searchSourceBuilder);
//    }

}
