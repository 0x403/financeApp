package com.example.TransactionService.service;

import com.example.TransactionService.repository.TransactionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import transactionServiceModels.AbstractContent;
import transactionServiceModels.Asset;
import transactionServiceModels.Product;
import transactionServiceModels.Trade;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    TransactionRepository repo;

    @Override
    public String createUser(String reqBody) throws IOException {
        mapper.readValue(reqBody, Asset.class); // validation
        JsonNode reqBodyJSON = mapper.readTree(reqBody);

        if(!(reqBodyJSON.hasNonNull("name")
                && reqBodyJSON.hasNonNull("price")
                && reqBodyJSON.hasNonNull("currency"))) {
            throw new IOException("Bad JSON input");
        }

        if(reqBodyJSON.has("id")) {
            ((ObjectNode) reqBodyJSON).remove("id");
        }
        ((ObjectNode) reqBodyJSON).put("lastPriceTime", ZonedDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME));

        return repo.saveAsset(reqBodyJSON.toString());
    }

    @Override
    public Asset getAssetById(String id) {
        return repo.findAssetById(id);
    }

    @Override
    public Asset updateAsset(String id, String reqBody) throws IOException {
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
            ((ObjectNode) reqBodyJSON).put("lastPriceTime", ZonedDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
        }

        return repo.updateAsset(id, reqBodyJSON.toString());
    }

    @Override
    public Product getProductById(String id) throws IOException {
        return repo.findProductById(id);
    }

    @Override
    public Product updateProduct(String id, String reqBody) throws IOException {
        Product reqBodyProduct = mapper.readValue(reqBody, Product.class); // validation
        JsonNode reqBodyJSON = mapper.readTree(reqBody);

        return repo.updateProduct(id, reqBodyJSON.toString());
    }

    @Override
    public List<Asset> searchAssets(String phrase, Integer page, Integer size) {
        size = sizeValidator(size);
        page = pageValidator(page);

        return repo.findAssetsByPhrase(phrase, page, size);
    }

    @Override
    public List<Asset> getAllAssets(Integer page, Integer size) {
        size = sizeValidator(size);
        page = pageValidator(page);

        return repo.findAllAssets(page, size);
    }

    @Override
    public List<Product> getAllProducts(Integer page, Integer size) {
        size = sizeValidator(size);
        page = pageValidator(page);

        return repo.findAllProducts(page, size);
    }

    @Override
    public List<Trade> getAllTrades(Integer page, Integer size) {
        size = sizeValidator(size);
        page = pageValidator(page);

        return repo.findAllTrades(page, size);
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
