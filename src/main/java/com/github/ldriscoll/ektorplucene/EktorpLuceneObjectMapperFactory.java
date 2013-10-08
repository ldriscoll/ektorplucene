package com.github.ldriscoll.ektorplucene;

import org.ektorp.CouchDbConnector;
import org.ektorp.impl.StdObjectMapperFactory;
import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

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
        instance.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        instance.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        instance.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        instance.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        
        SimpleModule module = new SimpleModule("Serialization", new Version(1, 0, 0, null, null, null));
        module.addSerializer(DateTime.class, new DateTimeSerializer());
    }
}
