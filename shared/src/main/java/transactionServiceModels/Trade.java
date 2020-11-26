package transactionServiceModels;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@Document(indexName = "trades")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Trade extends AbstractContent {

    private String user;

    Product product;

    private String direction;

    private ZonedDateTime createdTime;

    private ZonedDateTime lastUpdateTime;

    private String status;

    private List<String> activities;

}
