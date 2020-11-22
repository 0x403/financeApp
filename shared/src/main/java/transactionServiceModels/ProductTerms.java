package transactionServiceModels;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.ZonedDateTime;

@Getter
@Setter
@Document(indexName = "productTerms")
public class ProductTerms {

    private ZonedDateTime firstTradingDate;
    private ZonedDateTime lastTradingDate;
    private int minimumTradingSize;
    private String denominated;
    private ZonedDateTime expirationDate;

}
