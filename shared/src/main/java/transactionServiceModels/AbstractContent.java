package transactionServiceModels;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Getter
@Setter
public abstract class AbstractContent {

    @Id
    protected String id;

}