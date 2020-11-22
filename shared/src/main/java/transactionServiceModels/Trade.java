package transactionServiceModels;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@Document(indexName = "trades")
public class Trade extends AbstractContent {

    private String user;

    Product product;

    private String direction;

    private ZonedDateTime createdTime;

    private ZonedDateTime lastUpdateTime;

    private String status;

    private List<String> activities;

}
