package com.example.TransactionService.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
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
import transactionServiceModels.Asset;
import transactionServiceModels.Product;
import transactionServiceModels.Trade;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@RestController
@RequestMapping
public class TransactionController {

    @Autowired
    RestHighLevelClient client;

    @Autowired
    ObjectMapper mapper;

    // add new asset
    @PostMapping("/assets")
    public String postAsset(@RequestBody String reqBody) throws IOException {
        Asset reqBodyAsset = mapper.readValue(reqBody, Asset.class); // validation
        JsonNode reqBodyJSON = mapper.readTree(reqBody);

        if(!(reqBodyJSON.hasNonNull("name")
                && reqBodyJSON.hasNonNull("price")
                && reqBodyJSON.hasNonNull("currency"))) {
            throw new IOException("Bad JSON input");
        }

        if(reqBodyJSON.has("id")) {
            ((ObjectNode) reqBodyJSON).remove("id");
        }
        ((ObjectNode) reqBodyJSON).put("lastPriceTime", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        IndexRequest request = new IndexRequest("assets").source(reqBodyJSON.toString(), XContentType.JSON);

        PlainActionFuture<IndexResponse> future = new PlainActionFuture<>() {
            @Override
            public void onFailure(Exception e) {
                System.out.println(e.getMessage());
            }
        };
        client.indexAsync(request, RequestOptions.DEFAULT, future);
        IndexResponse res = future.actionGet();
        return res.status().toString() + "\nID: " + res.getId();
    }

    // get asset by id
    @GetMapping("/assets/{id}")
    public Asset getAsset(@PathVariable("id") String id) throws IOException {
        PlainActionFuture<GetResponse> future = new PlainActionFuture<>() {
            @Override
            public void onFailure(Exception e) {
                System.out.println(e.getMessage());
            }
        };
        GetRequest req = new GetRequest("assets", id);
        client.getAsync(req, RequestOptions.DEFAULT, future);
        GetResponse res = future.actionGet();

        Map<String, Object> resModelWithId = res.getSource();
        resModelWithId.put("id", res.getId());

        return mapper.convertValue(resModelWithId, Asset.class);
    }

    // update asset by id
    @PutMapping("/assets/{id}")
    public Asset putAsset(@PathVariable("id") String id, @RequestBody String reqBody) throws IOException {
        Asset reqBodyAsset = mapper.readValue(reqBody, Asset.class); // validate
        JsonNode reqBodyJSON = mapper.readTree(reqBody);
        Iterator<Map.Entry<String, JsonNode>> fields = reqBodyJSON.fields();

        // delete empty and null values
        while(fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            Object val = field.getValue();
            String value = field.getValue().asText();
            if(value.equals("null") || value.length() == 0) {
                ((ObjectNode) reqBodyJSON).remove(field.getKey());
            }
        }

        if(reqBodyJSON.has("id")) {
            ((ObjectNode) reqBodyJSON).remove("id");
        }
        if(reqBodyJSON.has("price")) {
            ((ObjectNode) reqBodyJSON).put("lastPriceTime", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }

        PlainActionFuture<UpdateResponse> future = new PlainActionFuture<>() {
            @Override
            public void onFailure(Exception e) {
                System.out.println(e.getMessage());
            }
        };
        UpdateRequest req = new UpdateRequest("assets", id).doc(reqBodyJSON.toString(), XContentType.JSON).fetchSource(true);
        client.updateAsync(req, RequestOptions.DEFAULT, future);
        UpdateResponse res = future.actionGet();

        Map<String, Object> resModelWithId = res.getGetResult().getSource();
        resModelWithId.put("id", id);
        return mapper.convertValue(resModelWithId, Asset.class);
    }

    // get product by id
    @GetMapping("/products/{id}")
    public Product getProduct(@PathVariable String id) throws IOException {
        PlainActionFuture<GetResponse> future = new PlainActionFuture<>() {
            @Override
            public void onFailure(Exception e) {
                System.out.println(e.getMessage());
            }
        };
        GetRequest req = new GetRequest("products", id);
        client.getAsync(req, RequestOptions.DEFAULT, future);
        GetResponse res = future.actionGet();

        return mapper.convertValue(res.getSource(), Product.class);
    }

    // update product by id
    @PutMapping("/products/{id}")
    public Product putProduct(@PathVariable String id, @RequestBody String reqBody) throws IOException {
        Product reqBodyProduct = mapper.readValue(reqBody, Product.class); // validation
        JsonNode reqBodyJSON = mapper.readTree(reqBody);

        PlainActionFuture<UpdateResponse> future = new PlainActionFuture<>() {
            @Override
            public void onFailure(Exception e) {
                System.out.println(e.getMessage());
            }
        };
        UpdateRequest req = new UpdateRequest("products", id).doc(reqBodyJSON.toString(), XContentType.JSON).fetchSource(true);
        client.updateAsync(req, RequestOptions.DEFAULT, future);
        UpdateResponse res = future.actionGet();

        return mapper.convertValue(res.getGetResult().getSource(), Product.class);
    }

//     search data by phrase
    @GetMapping("/search/{phrase}")
    public List<Asset> searchAsset(@PathVariable String phrase, @RequestParam(required = false) Integer page) {
        SearchRequest searchRequest = new SearchRequest();

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(10);
        if(page != null && page > 0) {
            searchSourceBuilder.from((page * 10) - 10);
        }
        searchSourceBuilder.query(QueryBuilders.queryStringQuery(phrase));

        searchRequest.source(searchSourceBuilder);

        PlainActionFuture<SearchResponse> future = new PlainActionFuture<>() {
            @Override
            public void onFailure(Exception e) {
                System.out.println(e.getMessage());
            }
        };
        client.searchAsync(searchRequest, RequestOptions.DEFAULT, future);
        SearchResponse res = future.actionGet();

        List<Asset> assetList = new ArrayList<>();

        SearchHit[] hits = res.getHits().getHits();
        for(SearchHit hit : hits) {
            Map<String, Object> map = hit.getSourceAsMap();
            map.put("id", hit.getId());
            assetList.add(mapper.convertValue(map, Asset.class));
        }

        return assetList;
    }

    // get assets || products || trades
    @GetMapping("/assets")
    public List<Asset> getAssets(@RequestParam(required = false) Integer page) {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("assets");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(10);
        if(page != null && page > 0) {
            searchSourceBuilder.from((page * 10) - 10);
        }
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());

        searchRequest.source(searchSourceBuilder);

        PlainActionFuture<SearchResponse> future = new PlainActionFuture<>() {
            @Override
            public void onFailure(Exception e) {
                System.out.println(e.getMessage());
            }
        };
        client.searchAsync(searchRequest, RequestOptions.DEFAULT, future);
        SearchResponse res = future.actionGet();

        List<Asset> assetList = new ArrayList<>();

        SearchHit[] hits = res.getHits().getHits();
        for(SearchHit hit : hits) {
            Map<String, Object> map = hit.getSourceAsMap();
            map.put("id", hit.getId());
            assetList.add(mapper.convertValue(map, Asset.class));
        }

        return assetList;
    }

    @GetMapping("/products")
    public List<Product> getProducts(@RequestParam(required = false) Integer page) {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("products");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(10);
        if(page != null && page > 0) {
            searchSourceBuilder.from((page * 10) - 10);
        }
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());

        searchRequest.source(searchSourceBuilder);

        PlainActionFuture<SearchResponse> future = new PlainActionFuture<>() {
            @Override
            public void onFailure(Exception e) {
                System.out.println(e.getMessage());
            }
        };
        client.searchAsync(searchRequest, RequestOptions.DEFAULT, future);
        SearchResponse res = future.actionGet();

        List<Product> productList = new ArrayList<>();

        SearchHit[] hits = res.getHits().getHits();
        for(SearchHit hit : hits) {
            Map<String, Object> map = hit.getSourceAsMap();
            map.put("id", hit.getId());
            productList.add(mapper.convertValue(map, Product.class));
        }

        return productList;
    }

    @GetMapping("/trades")
    public List<Trade> getTrades(@RequestParam(required = false) Integer page) {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("trades");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(10);
        if(page != null && page > 0) {
            searchSourceBuilder.from((page * 10) - 10);
        }
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());

        searchRequest.source(searchSourceBuilder);

        PlainActionFuture<SearchResponse> future = new PlainActionFuture<>() {
            @Override
            public void onFailure(Exception e) {
                System.out.println(e.getMessage());
            }
        };
        client.searchAsync(searchRequest, RequestOptions.DEFAULT, future);
        SearchResponse res = future.actionGet();

        List<Trade> tradeList = new ArrayList<>();

        SearchHit[] hits = res.getHits().getHits();
        for(SearchHit hit : hits) {
            Map<String, Object> map = hit.getSourceAsMap();
            map.put("id", hit.getId());
            tradeList.add(mapper.convertValue(map, Trade.class));
        }

        return tradeList;
    }

}
