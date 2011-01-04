package com.github.ldriscoll.ektorplucene;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;

/**
 * The standard joda datetime serializer uses the ZZ time zone.  This puts something like -07:00, however when
 * couchdb-lucene receives this 'date' it uses SimpleDateFormat to deserialize it, and SimpleDateFormat
 * Created by IntelliJ IDEA.
 * User: ldriscoll
 * Date: 12/22/10
 * Time: 9:59 AM
 * To change this template use File | Settings | File Templates.
 */
public class DateTimeSerializer extends JsonSerializer<DateTime> {

    private final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    @Override
    public void serialize(DateTime value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        jgen.writeString(DATE_TIME_FORMAT.print(value));
    }
}
