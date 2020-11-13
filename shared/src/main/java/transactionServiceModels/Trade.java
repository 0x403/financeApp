package transactionServiceModels;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Trade extends AbstractContent {

    private String user;

    Product product;

    private String direction;

    private LocalDateTime createdTime;

    private LocalDateTime lastUpdateTime;

    private String status;

    private List<String> activities;

}
