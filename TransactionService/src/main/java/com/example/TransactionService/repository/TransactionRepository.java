package com.example.TransactionService.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import transactionServiceModels.AbstractContent;
import transactionServiceModels.Asset;
import transactionServiceModels.Product;
import transactionServiceModels.Trade;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Repository
public class TransactionRepository {

    @Autowired
    RestHighLevelClient client;

    @Autowired
    ObjectMapper mapper;

    @Async
    public CompletableFuture<String> saveAsset(Asset reqAsset) throws JsonProcessingException {

        CompletableFuture<String> indexResponseFuture = new CompletableFuture<>();

        ActionListener<IndexResponse> actionListener = new ActionListener<>() {
            @Override
            public void onResponse(IndexResponse result) {
                String message = result.status() + "\nID: " + result.getId();
                indexResponseFuture.complete(message);
            }

            @Override
            public void onFailure(Exception e) {
                indexResponseFuture.completeExceptionally(e);
            }
        };

        String reqAssetJSON = mapper.writeValueAsString(reqAsset);

        IndexRequest request = new IndexRequest("assets").source(reqAssetJSON, XContentType.JSON);
        client.indexAsync(request, RequestOptions.DEFAULT, actionListener);

        return indexResponseFuture;
    }

    @Async
    public CompletableFuture<Asset> findAssetById(String id) {

        CompletableFuture<Asset> assetFuture = new CompletableFuture<>();

        ActionListener<GetResponse> actionListener = new ActionListener<>() {
            @SneakyThrows
            @Override
            public void onResponse(GetResponse response) {
                Map<String, Object> responseAssetMap = response.getSource();
                responseAssetMap.put("id", response.getId());
                Asset wantedAsset = mapper.convertValue(responseAssetMap, Asset.class);
                assetFuture.complete(wantedAsset);
            }

            @Override
            public void onFailure(Exception e) {
                assetFuture.completeExceptionally(e);
            }
        };

        GetRequest req = new GetRequest("assets", id);
        client.getAsync(req, RequestOptions.DEFAULT, actionListener);

        return assetFuture;
    }

    @Async
    public CompletableFuture<Asset> updateAsset(String id, Asset updatedFields) throws JsonProcessingException {

        CompletableFuture<Asset> assetFuture = new CompletableFuture<>();

        ActionListener<UpdateResponse> actionListener = new ActionListener<>() {
            @Override
            public void onResponse(UpdateResponse response) {
                Map<String, Object> responseAssetMap = response.getGetResult().getSource();
                responseAssetMap.put("id", id);
                assetFuture.complete(mapper.convertValue(responseAssetMap, Asset.class));
            }

            @Override
            public void onFailure(Exception e) {
                assetFuture.completeExceptionally(e);
            }
        };

        String updatedFieldsString = mapper.writeValueAsString(updatedFields);
        UpdateRequest req = new UpdateRequest("assets", id).doc(updatedFieldsString, XContentType.JSON).fetchSource(true);
        client.updateAsync(req, RequestOptions.DEFAULT, actionListener);

        return assetFuture;
    }

    @Async
    public CompletableFuture<Product> findProductById(String id) {

        CompletableFuture<Product> productFuture = new CompletableFuture<>();

        ActionListener<GetResponse> actionListener = new ActionListener<>() {
            @Override
            public void onResponse(GetResponse result) {
                Map<String, Object> wantedProductMap = result.getSource();
                wantedProductMap.put("id", result.getId());
                Product wantedProduct = mapper.convertValue(wantedProductMap, Product.class);
                productFuture.complete(wantedProduct);
            }

            @Override
            public void onFailure(Exception e) {
                productFuture.completeExceptionally(e);
            }
        };

        GetRequest req = new GetRequest("products", id);
        client.getAsync(req, RequestOptions.DEFAULT, actionListener);

        return productFuture;
    }

    @Async
    public CompletableFuture<Product> updateProduct(String id, Product updatedFields) throws IOException {

        CompletableFuture<Product> productFuture = new CompletableFuture<>();

        ActionListener<UpdateResponse> actionListener = new ActionListener<>() {
            @Override
            public void onResponse(UpdateResponse response) {
                Map<String, Object> responseProductMap = response.getGetResult().getSource();
                responseProductMap.put("id", id);
                productFuture.complete(mapper.convertValue(responseProductMap, Product.class));
            }

            @Override
            public void onFailure(Exception e) {
                productFuture.completeExceptionally(e);
            }
        };

        String updatedFieldsString = mapper.writeValueAsString(updatedFields);
        UpdateRequest req = new UpdateRequest("products", id).doc(updatedFieldsString, XContentType.JSON).fetchSource(true);
        client.updateAsync(req, RequestOptions.DEFAULT, actionListener);

        return productFuture;
    }

    @Async
    public CompletableFuture<List<AbstractContent>> findAssetsByPhrase(String phrase, int page, int size) {

        CompletableFuture<List<AbstractContent>> assetsListFuture = new CompletableFuture<>();

        SearchRequest searchRequest = new SearchRequest();

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(size);
        searchSourceBuilder.from((page * size) - size);
        searchSourceBuilder.query(QueryBuilders.queryStringQuery(phrase));

        searchRequest.source(searchSourceBuilder);

        ActionListener<SearchResponse> future = new ActionListener<>() {
            @Override
            public void onResponse(SearchResponse response) {
                List<AbstractContent> assetsList = new ArrayList<>();

                SearchHit[] hits = response.getHits().getHits();
                for(SearchHit hit : hits) {
                    Map<String, Object> map = hit.getSourceAsMap();
                    map.put("id", hit.getId());
                    assetsList.add(mapper.convertValue(map, Asset.class));
                }

                assetsListFuture.complete(assetsList);
            }

            @Override
            public void onFailure(Exception e) {
                assetsListFuture.completeExceptionally(e);
            }
        };

        client.searchAsync(searchRequest, RequestOptions.DEFAULT, future);

        return assetsListFuture;
    }

    @Async
    public CompletableFuture<List<Asset>> findAllAssets(int page, int size) {

        CompletableFuture<List<Asset>> assetsListFuture = new CompletableFuture<>();

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("assets");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(size);
        searchSourceBuilder.from((page * size) - size);
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());

        searchRequest.source(searchSourceBuilder);

        ActionListener<SearchResponse> actionListener = new ActionListener<>() {
            @Override
            public void onResponse(SearchResponse response) {
                List<Asset> assetsList = new ArrayList<>();

                SearchHit[] hits = response.getHits().getHits();
                for(SearchHit hit : hits) {
                    Map<String, Object> map = hit.getSourceAsMap();
                    map.put("id", hit.getId());
                    assetsList.add(mapper.convertValue(map, Asset.class));
                }

                assetsListFuture.complete(assetsList);
            }

            @Override
            public void onFailure(Exception e) {
                assetsListFuture.completeExceptionally(e);
            }
        };
        client.searchAsync(searchRequest, RequestOptions.DEFAULT, actionListener);

        return assetsListFuture;
    }

    @Async
    public CompletableFuture<List<Product>> findAllProducts(int page, int size) {

        CompletableFuture<List<Product>> productsListFuture = new CompletableFuture<>();

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("products");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(size);
        searchSourceBuilder.from((page * size) - size);
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());

        searchRequest.source(searchSourceBuilder);

        ActionListener<SearchResponse> actionListener = new ActionListener<>() {
            @Override
            public void onResponse(SearchResponse response) {
                List<Product> productsList = new ArrayList<>();

                SearchHit[] hits = response.getHits().getHits();
                for(SearchHit hit : hits) {
                    Map<String, Object> map = hit.getSourceAsMap();
                    map.put("id", hit.getId());
                    productsList.add(mapper.convertValue(map, Product.class));
                }

                productsListFuture.complete(productsList);
            }

            @Override
            public void onFailure(Exception e) {
                productsListFuture.completeExceptionally(e);
            }
        };
        client.searchAsync(searchRequest, RequestOptions.DEFAULT, actionListener);

        return productsListFuture;
    }

    public CompletableFuture<List<Trade>> findAllTrades(int page, int size) {

        CompletableFuture<List<Trade>> tradesListFuture = new CompletableFuture<>();

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("trades");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(size);
        searchSourceBuilder.from((page * size) - size);
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());

        searchRequest.source(searchSourceBuilder);

        ActionListener<SearchResponse> actionListener = new ActionListener<>() {
            @Override
            public void onResponse(SearchResponse response) {
                List<Trade> tradesList = new ArrayList<>();

                SearchHit[] hits = response.getHits().getHits();
                for(SearchHit hit : hits) {
                    Map<String, Object> map = hit.getSourceAsMap();
                    map.put("id", hit.getId());
                    tradesList.add(mapper.convertValue(map, Trade.class));
                }

                tradesListFuture.complete(tradesList);
            }

            @Override
            public void onFailure(Exception e) {
                tradesListFuture.completeExceptionally(e);
            }
        };
        client.searchAsync(searchRequest, RequestOptions.DEFAULT, actionListener);


        return tradesListFuture;
    }

}
