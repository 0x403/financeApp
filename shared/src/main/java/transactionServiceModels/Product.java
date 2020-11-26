package transactionServiceModels;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;

@Getter
@Setter
@Document(indexName = "products")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Product extends AbstractContent {

    private ProductTerms productTerms;

    private List<Asset> assetsList;

    private List<String> synonyms;

    private Price price;

}
