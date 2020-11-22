package com.example.TransactionService.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.stereotype.Repository;
import transactionServiceModels.Asset;
import transactionServiceModels.Product;
import transactionServiceModels.Trade;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class TransactionRepository {

    @Autowired
    RestHighLevelClient client;

    @Autowired
    ObjectMapper mapper;

    public String saveAsset(String reqBodyJSON) {
        PlainActionFuture<IndexResponse> future = new PlainActionFuture<>() {
            @Override
            public void onFailure(Exception e) {
                System.out.println(e.getMessage());
            }
        };

        IndexRequest request = new IndexRequest("assets").source(reqBodyJSON, XContentType.JSON);
        client.indexAsync(request, RequestOptions.DEFAULT, future);
        IndexResponse res = future.actionGet();
        return res.status().toString() + "\nID: " + res.getId();

    }

    public Asset findAssetById(String id) {
        PlainActionFuture<GetResponse> future = new PlainActionFuture<>() {
            @Override
            public void onFailure(Exception e) {
                System.out.println(e.getMessage());
            }
        };
        GetRequest req = new GetRequest("assets", id);
        client.getAsync(req, RequestOptions.DEFAULT, future);
        GetResponse res = future.actionGet();

        Map<String, Object> resAssetWithId = res.getSource();
        resAssetWithId.put("id", res.getId());

        return mapper.convertValue(resAssetWithId, Asset.class);
    }

    public Asset updateAsset(String id, String reqBody) {
        PlainActionFuture<UpdateResponse> future = new PlainActionFuture<>() {
            @Override
            public void onFailure(Exception e) {
                System.out.println(e.getMessage());
            }
        };
        UpdateRequest req = new UpdateRequest("assets", id).doc(reqBody, XContentType.JSON).fetchSource(true);
        client.updateAsync(req, RequestOptions.DEFAULT, future);
        UpdateResponse res = future.actionGet();

        Map<String, Object> resModelWithId = res.getGetResult().getSource();
        resModelWithId.put("id", id);
        return mapper.convertValue(resModelWithId, Asset.class);
    }

    public Product findProductById(String id) {
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

    public Product updateProduct(String id, String reqBody) {
        PlainActionFuture<UpdateResponse> future = new PlainActionFuture<>() {
            @Override
            public void onFailure(Exception e) {
                System.out.println(e.getMessage());
            }
        };
        UpdateRequest req = new UpdateRequest("products", id).doc(reqBody, XContentType.JSON).fetchSource(true);
        client.updateAsync(req, RequestOptions.DEFAULT, future);
        UpdateResponse res = future.actionGet();

        return mapper.convertValue(res.getGetResult().getSource(), Product.class);
    }

    public List<Asset> findAssetsByPhrase(String phrase, int page, int size) {
        SearchRequest searchRequest = new SearchRequest();

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(size);
        searchSourceBuilder.from((page * size) - size);
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

    public List<Asset> findAllAssets(int page, int size) {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("assets");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(size);
        searchSourceBuilder.from((page * size) - size);
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

    public List<Product> findAllProducts(int page, int size) {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("products");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(size);
        searchSourceBuilder.from((page * size) - size);
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

        List<Product> productsList = new ArrayList<>();

        SearchHit[] hits = res.getHits().getHits();
        for(SearchHit hit : hits) {
            Map<String, Object> map = hit.getSourceAsMap();
            map.put("id", hit.getId());
            productsList.add(mapper.convertValue(map, Product.class));
        }

        return productsList;
    }

    public List<Trade> findAllTrades(int page, int size) {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("trades");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(size);
        searchSourceBuilder.from((page * size) - size);
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

        List<Trade> tradesList = new ArrayList<>();

        SearchHit[] hits = res.getHits().getHits();
        for(SearchHit hit : hits) {
            Map<String, Object> map = hit.getSourceAsMap();
            map.put("id", hit.getId());
            tradesList.add(mapper.convertValue(map, Trade.class));
        }

        return tradesList;
    }

}
