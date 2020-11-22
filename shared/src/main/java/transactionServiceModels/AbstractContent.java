package transactionServiceModels;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
public abstract class AbstractContent {

    @Id
    protected String id;

}