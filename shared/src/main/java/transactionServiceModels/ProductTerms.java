package transactionServiceModels;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Document(indexName = "productTerms")
public class ProductTerms {

    private LocalDateTime firstTradingDate;
    private LocalDateTime lastTradingDate;
    private int minimumTradingSize;
    private String denominated;
    private LocalDateTime expirationDate;

}
