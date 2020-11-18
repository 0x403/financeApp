package transactionServiceModels;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;

@Getter
@Setter
@Document(indexName = "prices")
public class Price {

    private float bid;
    private float ask;
    private int bidSize;
    private int askSize;

}
