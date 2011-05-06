package com.github.ldriscoll.ektorplucene;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.ser.CustomSerializerFactory;
import org.ektorp.CouchDbConnector;
import org.ektorp.impl.StdObjectMapperFactory;
import org.joda.time.DateTime;

/**
 * Code based on StdObjectMapperFactory
 * The objectmapper must remain a single instance as it is used both in StdCouchDbConnector, and LuceneAwareCouchDbConnector
 */
public class EktorpLuceneObjectMapperFactory extends StdObjectMapperFactory {
    private ObjectMapper instance;

    @Override
    public synchronized ObjectMapper createObjectMapper() {
        if (instance == null) {
            instance = new ObjectMapper();
            applyDefaultConfiguration(instance);
        }
        return instance;
    }

    @Override
    public ObjectMapper createObjectMapper(CouchDbConnector connector) {
        ObjectMapper om = super.createObjectMapper(connector);
        applyDefaultConfiguration(om);
        return om;
    }

    private void applyDefaultConfiguration(ObjectMapper instance) {

        instance.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
        instance.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        instance.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        instance.getSerializationConfig().setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);

        CustomSerializerFactory sf = new CustomSerializerFactory();
        sf.addSpecificMapping(DateTime.class, new DateTimeSerializer());
        instance.setSerializerFactory(sf);
    }
}
