package transactionServiceModels;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Asset {

    private String name;

    private float price;

    private LocalDateTime lastPriceTime;

    private String currency;

}
