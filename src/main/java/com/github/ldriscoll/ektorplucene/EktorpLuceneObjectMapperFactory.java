package com.github.ldriscoll.ektorplucene;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.ser.CustomSerializerFactory;
import org.ektorp.impl.ObjectMapperFactory;
import org.joda.time.DateTime;

/**
 * Code based on StdObjectMapperFactory
 * The objectmapper must remain a single instance as it is used both in StdCouchDbConnector, and LuceneAwareCouchDbConnector
 * Created by IntelliJ IDEA.
 * User: ldriscoll
 * Date: 1/10/11
 * Time: 9:59 AM
 * To change this template use File | Settings | File Templates.
 */
public class EktorpLuceneObjectMapperFactory implements ObjectMapperFactory {
    private ObjectMapper instance;

    public synchronized ObjectMapper createObjectMapper() {
        if (instance == null) {
            createDefaultObjectMapper();
        }
        return instance;
    }


    private void createDefaultObjectMapper() {
        instance = new ObjectMapper();
        
        instance.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
        instance.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        instance.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        instance.getSerializationConfig().setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);

        CustomSerializerFactory sf = new CustomSerializerFactory();
        sf.addSpecificMapping(DateTime.class, new DateTimeSerializer());
        instance.setSerializerFactory(sf);
    }
}
