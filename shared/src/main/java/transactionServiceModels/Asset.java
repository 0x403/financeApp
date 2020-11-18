package transactionServiceModels;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Document(indexName = "assets")
public class Asset extends AbstractContent {

    private String name;

    private float price;

    private LocalDateTime lastPriceTime;

    private String currency;

}
