package transactionServiceModels;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Price {

    private float bid;
    private float ask;
    private int bidSize;
    private int askSize;

}
