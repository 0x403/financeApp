package transactionServiceModels;

import com.fasterxml.jackson.annotation.JsonFormat;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@Document(indexName = "assets")
public class Asset extends AbstractContent {

    private String name;

    private float price;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime lastPriceTime;

    private String currency;

}

class LocalDateTimeDeserializer extends StdDeserializer<LocalDateTime> {

    private static final long serialVersionUID = 1L;

    protected LocalDateTimeDeserializer() {
        super(LocalDateTime.class);
    }


    @Override
    public LocalDateTime deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        return LocalDateTime.parse(jp.readValueAs(String.class));
    }

}

class LocalDateTimeSerializer extends StdSerializer<LocalDateTime> {

    private static final long serialVersionUID = 1L;

    public LocalDateTimeSerializer(){
        super(LocalDateTime.class);
    }

    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider sp) throws IOException {
        gen.writeString(value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
}