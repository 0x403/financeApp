package transactionServiceModels;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@Document(indexName = "assets")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Asset extends AbstractContent {

    private String name;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String[] suggestName;

    private Float price;

    @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    private ZonedDateTime lastPriceTime;

    private String currency;

}

class ZonedDateTimeDeserializer extends StdDeserializer<ZonedDateTime> {

    private static final long serialVersionUID = 1L;

    protected ZonedDateTimeDeserializer() {
        super(ZonedDateTime.class);
    }

    @Override
    public ZonedDateTime deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        return ZonedDateTime.parse(jp.readValueAs(String.class));
    }

}

class ZonedDateTimeSerializer extends StdSerializer<ZonedDateTime> {

    private static final long serialVersionUID = 1L;

    public ZonedDateTimeSerializer(){
        super(ZonedDateTime.class);
    }

    @Override
    public void serialize(ZonedDateTime value, JsonGenerator gen, SerializerProvider sp) throws IOException {
        gen.writeString(value.format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
    }
}