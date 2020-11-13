package transactionServiceModels;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ProductTerms {

    private LocalDateTime firstTradingDate;
    private LocalDateTime lastTradingDate;
    private int minimumTradingSize;
    private String denominated;
    private LocalDateTime expirationDate;

}
