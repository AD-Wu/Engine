package com.x.isearch.web.serial;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.x.doraemon.DateTimes;
import com.x.doraemon.DateTimes.Formatter;
import com.x.doraemon.Strings;
import java.io.IOException;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

/**
 * @author AD
 * @date 2022/1/20 21:43
 */
@Component
public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {

    @Override
    public void serialize(LocalDateTime time, JsonGenerator gen, SerializerProvider sp) throws IOException {
        try {
            String format = DateTimes.format(time, Formatter.DEFAULT);
            if (Strings.isNotBlank(format) && format.endsWith(".000")) {
                gen.writeString(format.replace(".000", ""));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
