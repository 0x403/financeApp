package com.example.TransactionService.repository;

import com.example.TransactionService.model.Article;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ArticleRepository extends ElasticsearchRepository<Article, String> {

    Article findByTitle(String firstname);

}
