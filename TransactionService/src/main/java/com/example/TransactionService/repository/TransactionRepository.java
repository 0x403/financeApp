package com.example.TransactionService.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.SuggestionBuilder;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class TransactionRepository {

    @Autowired
    RestHighLevelClient client;

    @Autowired
    ObjectMapper mapper;

    public CompletableFuture<String> saveAsset(Asset reqAsset) throws JsonProcessingException {

        CompletableFuture<String> indexResponseFuture = new CompletableFuture<>();

        String reqAssetJSON = mapper.writeValueAsString(reqAsset);
        IndexRequest indexRequest = new IndexRequest("assets").source(reqAssetJSON, XContentType.JSON);
        ActionListener<IndexResponse> actionListener = new ElasticActionListener<IndexResponse, String>(indexResponseFuture) {
            @Override
            public void onResponse(IndexResponse response) {
                String message = response.status() + "\nID: " + response.getId();
                this.getFuture().complete(message);
            }
        };

        reqAsset.setSuggestName(reqAsset.getName().split(" "));

        client.indexAsync(indexRequest, RequestOptions.DEFAULT, actionListener);

        return indexResponseFuture;
    }

    public CompletableFuture<Asset> findAssetById(String id) {

        CompletableFuture<Asset> assetFuture = new CompletableFuture<>();

        GetRequest getRequest = new GetRequest("assets", id);
        ActionListener<GetResponse> actionListener = new ElasticActionListener<GetResponse, Asset>(assetFuture) {
            @Override
            public void onResponse(GetResponse response) {
                Map<String, Object> responseAssetMap = response.getSource();
                responseAssetMap.put("id", response.getId());
                Asset wantedAsset = mapper.convertValue(responseAssetMap, Asset.class);
                this.getFuture().complete(wantedAsset);
            }
        };

        client.getAsync(getRequest, RequestOptions.DEFAULT, actionListener);

        return assetFuture;
    }

    public CompletableFuture<Asset> updateAsset(String id, Asset updatedFields) throws JsonProcessingException {

        CompletableFuture<Asset> assetFuture = new CompletableFuture<>();

        String updatedFieldsString = mapper.writeValueAsString(updatedFields);
        UpdateRequest updateRequest = new UpdateRequest("assets", id).doc(updatedFieldsString, XContentType.JSON).fetchSource(true);
        ActionListener<UpdateResponse> actionListener = new ElasticActionListener<UpdateResponse, Asset>(assetFuture) {
            @Override
            public void onResponse(UpdateResponse response) {
                Map<String, Object> responseAssetMap = response.getGetResult().getSource();
                responseAssetMap.put("id", id);
                this.getFuture().complete(mapper.convertValue(responseAssetMap, Asset.class));
            }
        };

        client.updateAsync(updateRequest, RequestOptions.DEFAULT, actionListener);

        return assetFuture;
    }

    public CompletableFuture<Product> findProductById(String id) {

        CompletableFuture<Product> productFuture = new CompletableFuture<>();

        GetRequest getRequest = new GetRequest("products", id);
        ActionListener<GetResponse> actionListener = new ElasticActionListener<GetResponse, Product>(productFuture) {
            @Override
            public void onResponse(GetResponse response) {
                Map<String, Object> wantedProductMap = response.getSource();
                wantedProductMap.put("id", response.getId());
                Product wantedProduct = mapper.convertValue(wantedProductMap, Product.class);
                this.getFuture().complete(wantedProduct);
            }
        };

        client.getAsync(getRequest, RequestOptions.DEFAULT, actionListener);

        return productFuture;
    }

    public CompletableFuture<Product> updateProduct(String id, Product updatedFields) throws IOException {

        CompletableFuture<Product> productFuture = new CompletableFuture<>();

        String updatedFieldsString = mapper.writeValueAsString(updatedFields);
        UpdateRequest updateRequest = new UpdateRequest("products", id).doc(updatedFieldsString, XContentType.JSON).fetchSource(true);
        ActionListener<UpdateResponse> actionListener = new ElasticActionListener<UpdateResponse, Product>(productFuture) {
            @Override
            public void onResponse(UpdateResponse response) {
                Map<String, Object> responseProductMap = response.getGetResult().getSource();
                responseProductMap.put("id", id);
                this.getFuture().complete(mapper.convertValue(responseProductMap, Product.class));
            }
        };

        client.updateAsync(updateRequest, RequestOptions.DEFAULT, actionListener);

        return productFuture;
    }

    public CompletableFuture<List<AbstractContent>> findResourcesByPhrase(String phrase, int page, int size) {

        CompletableFuture<List<AbstractContent>> resourcesListFuture = new CompletableFuture<>();

        SearchRequest searchRequest = searchRequestFindResourcesByPhrase(phrase, page, size);
        ActionListener<SearchResponse> actionListener = new ElasticActionListener<SearchResponse, List<AbstractContent>>(resourcesListFuture) {
            @Override
            public void onResponse(SearchResponse response) {
                List<AbstractContent> resourcesList = getResourcesListFromSearchResponse(response);
                this.getFuture().complete(resourcesList);
            }
        };

        client.searchAsync(searchRequest, RequestOptions.DEFAULT, actionListener);

        return resourcesListFuture;
    }

    public CompletableFuture<List<Asset>> findAllAssets(int page, int size) {

        CompletableFuture<List<Asset>> assetsListFuture = new CompletableFuture<>();

        SearchRequest searchRequest = findAllResourcesByIndex("assets", page, size);
        ActionListener<SearchResponse> actionListener = new ElasticActionListener<SearchResponse, List<Asset>>(assetsListFuture) {
            @Override
            public void onResponse(SearchResponse response) {
                List<Asset> assetsList = getAssetsListFromSearchResponse(response);
                this.getFuture().complete(assetsList);
            }
        };

        client.searchAsync(searchRequest, RequestOptions.DEFAULT, actionListener);

        return assetsListFuture;
    }

    public CompletableFuture<List<Product>> findAllProducts(int page, int size) {

        CompletableFuture<List<Product>> productsListFuture = new CompletableFuture<>();

        SearchRequest searchRequest = findAllResourcesByIndex("products", page, size);
        ActionListener<SearchResponse> actionListener = new ElasticActionListener<SearchResponse, List<Product>>(productsListFuture) {
            @Override
            public void onResponse(SearchResponse response) {
                List<Product> productsList = getProductsListFromSearchResponse(response);
                this.getFuture().complete(productsList);
            }
        };

        client.searchAsync(searchRequest, RequestOptions.DEFAULT, actionListener);

        return productsListFuture;
    }

    public CompletableFuture<List<Trade>> findAllTrades(int page, int size) {

        CompletableFuture<List<Trade>> tradesListFuture = new CompletableFuture<>();

        SearchRequest searchRequest = findAllResourcesByIndex("trades", page, size);
        ActionListener<SearchResponse> actionListener = new ElasticActionListener<SearchResponse, List<Trade>>(tradesListFuture) {
            @Override
            public void onResponse(SearchResponse response) {
                List<Trade> tradesList = getTradesListFromSearchResponse(response);
                this.getFuture().complete(tradesList);
            }
        };

        client.searchAsync(searchRequest, RequestOptions.DEFAULT, actionListener);

        return tradesListFuture;
    }

    public CompletableFuture<List<String>> findSuggestedAssetsAndProducts(String keyword) {

        CompletableFuture<List<String>> suggestedAssetsNamesFuture = new CompletableFuture<>();
        String suggestionNameInBuilder = "suggest-assets";

        SearchRequest searchRequest = searchRequestSuggestedAssetsByPrefixKeyword(keyword, suggestionNameInBuilder);
        ActionListener<SearchResponse> actionListener = new ElasticActionListener<SearchResponse, List<String>>(suggestedAssetsNamesFuture) {
            @Override
            public void onResponse(SearchResponse response) {
                this.getFuture().complete(getSuggestedAssetsNamesFromSearchResponse(response, suggestionNameInBuilder));
            }
        };

        client.searchAsync(searchRequest, RequestOptions.DEFAULT, actionListener);

        return suggestedAssetsNamesFuture.thenComposeAsync(suggestedAssetsNames -> {

            CompletableFuture<List<String>> suggestedAssetsAndProductsNamesFuture = new CompletableFuture<>();

            SearchRequest nextSearchRequest = searchRequestProductsNamesBySuggestedAssetsNames(suggestedAssetsNames);
            ActionListener<SearchResponse> nextActionListener = new ElasticActionListener<SearchResponse, List<String>>(suggestedAssetsAndProductsNamesFuture) {
                @Override
                public void onResponse(SearchResponse response) {
                    List<String> productsNamesList = getProductsNamesFromSearchResponse(response);
                    // CONCAT LIST OF ASSETS' NAMES AND PRODUCTS' NAMES
                    this.getFuture().complete(Stream.concat(suggestedAssetsNames.stream(), productsNamesList.stream()).collect(Collectors.toList()));
                }
            };

            client.searchAsync(nextSearchRequest, RequestOptions.DEFAULT, nextActionListener);

            return suggestedAssetsAndProductsNamesFuture;
        });
    }

    private SearchRequest searchRequestFindResourcesByPhrase(String phrase, int page, int size) {
        SearchRequest searchRequest = new SearchRequest();

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(size);
        searchSourceBuilder.from((page * size) - size);
        searchSourceBuilder.query(QueryBuilders.queryStringQuery(phrase));

        searchRequest.source(searchSourceBuilder);

        return searchRequest;
    }

    private SearchRequest findAllResourcesByIndex(String index, int page, int size) {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(index);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(size);
        searchSourceBuilder.from((page * size) - size);
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());

        searchRequest.source(searchSourceBuilder);

        return searchRequest;
    }

    private SearchRequest searchRequestSuggestedAssetsByPrefixKeyword(String keyword, String suggestionNameInBuilder) {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("assets");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        searchSourceBuilder.size(3); // returned size, default == 5
        SuggestionBuilder completionSuggestionBuilder = SuggestBuilders.completionSuggestion("suggestName").prefix(keyword);
        SuggestBuilder suggestBuilder = new SuggestBuilder();
        suggestBuilder.addSuggestion(suggestionNameInBuilder, completionSuggestionBuilder);
        searchSourceBuilder.suggest(suggestBuilder);

        searchRequest.source(searchSourceBuilder);

        return searchRequest;
    }

    private SearchRequest searchRequestProductsNamesBySuggestedAssetsNames(List<String> suggestedAssetsNames) {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("products");

        StringBuilder assetsNames = new StringBuilder();
        for(String assetName: suggestedAssetsNames) {
            assetsNames.append(assetName + " ");
        }

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(4);
        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("assetsList", assetsNames.toString());
        searchSourceBuilder.query(matchQueryBuilder);

        searchRequest.source(searchSourceBuilder);

        return searchRequest;
    }

    private List<AbstractContent> getResourcesListFromSearchResponse(SearchResponse searchResponse) {
        List<AbstractContent> resourcesList = new ArrayList<>();

        SearchHit[] hits = searchResponse.getHits().getHits();
        for(SearchHit hit : hits) {
            Map<String, Object> map = hit.getSourceAsMap();
            map.put("id", hit.getId());
            switch(hit.getIndex()) {
                case "assets":
                    resourcesList.add(mapper.convertValue(map, Asset.class));
                    break;
                case "products":
                    resourcesList.add(mapper.convertValue(map, Product.class));
                    break;
                case "trades":
                    resourcesList.add(mapper.convertValue(map, Trade.class));
                    break;
            }
        }

        return resourcesList;
    }

    private List<Asset> getAssetsListFromSearchResponse(SearchResponse searchResponse) {
        List<Asset> assetsList = new ArrayList<>();

        SearchHit[] hits = searchResponse.getHits().getHits();
        for(SearchHit hit : hits) {
            Map<String, Object> map = hit.getSourceAsMap();
            map.put("id", hit.getId());
            assetsList.add(mapper.convertValue(map, Asset.class));
        }

        return assetsList;
    }

    private List<Product> getProductsListFromSearchResponse(SearchResponse searchResponse) {
        List<Product> productsList = new ArrayList<>();

        SearchHit[] hits = searchResponse.getHits().getHits();
        for(SearchHit hit : hits) {
            Map<String, Object> map = hit.getSourceAsMap();
            map.put("id", hit.getId());
            productsList.add(mapper.convertValue(map, Product.class));
        }

        return productsList;
    }

    private List<Trade> getTradesListFromSearchResponse(SearchResponse searchResponse) {
        List<Trade> tradesList = new ArrayList<>();

        SearchHit[] hits = searchResponse.getHits().getHits();
        for(SearchHit hit : hits) {
            Map<String, Object> map = hit.getSourceAsMap();
            map.put("id", hit.getId());
            tradesList.add(mapper.convertValue(map, Trade.class));
        }

        return tradesList;
    }

    private List<String> getSuggestedAssetsNamesFromSearchResponse(SearchResponse searchResponse, String suggestionNameInBuilder) {
        ArrayList<String> suggestedAssetsNames = new ArrayList<>();

        Suggest suggest = searchResponse.getSuggest();
        CompletionSuggestion completionSuggestion = suggest.getSuggestion(suggestionNameInBuilder);
        for(CompletionSuggestion.Entry entry: completionSuggestion.getEntries()) {
            for(CompletionSuggestion.Entry.Option option: entry) {
                String suggestedAssetName = option.getText().string();
                suggestedAssetsNames.add(suggestedAssetName);
            }
        }

        List<String> suggestedAssetsNamesWithoutDuplicates =
                suggestedAssetsNames.stream().distinct().collect(Collectors.toList());
        if(suggestedAssetsNamesWithoutDuplicates.size() > 3) {
            suggestedAssetsNamesWithoutDuplicates.subList(0, 3);
        }

        return suggestedAssetsNamesWithoutDuplicates;
    }

    private List<String> getProductsNamesFromSearchResponse(SearchResponse searchResponse) {
        ArrayList<String> productsNamesList = new ArrayList<>();

        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            Map<String, Object> map = hit.getSourceAsMap();
            productsNamesList.add((String) map.get("name"));
        }

        return productsNamesList;
    }
}
