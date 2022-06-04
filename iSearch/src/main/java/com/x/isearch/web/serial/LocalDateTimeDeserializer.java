package com.x.isearch.web.serial;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.x.doraemon.DateTimes;
import java.io.IOException;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

/**
 * @author AD
 * @date 2022/1/20 21:43
 */
@Component
public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext context)
        throws IOException, JsonProcessingException {
        String text = jsonParser.getText();
        try {
            return DateTimes.toLocalDateTime(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
