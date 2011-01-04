package com.github.ldriscoll.ektorplucene;

/**
 * Copyright 2010 Luke Driscoll
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
