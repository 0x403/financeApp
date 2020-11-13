package transactionServiceModels;

import lombok.Data;

import java.util.List;

@Data
public class Product extends AbstractContent {

    private ProductTerms productTerms;

    private List<Asset> assetsList;

    private List<String> synonyms;

    private Price price;

}
