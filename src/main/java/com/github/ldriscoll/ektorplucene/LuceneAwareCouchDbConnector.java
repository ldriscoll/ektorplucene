package com.github.ldriscoll.ektorplucene;

/**
 * Copyright 2011 Luke Driscoll
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

import java.io.IOException;

import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpResponse;
import org.ektorp.http.ResponseCallback;
import org.ektorp.http.RestTemplate;
import org.ektorp.http.StdResponseHandler;
import org.ektorp.http.URI;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.util.Assert;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Simple override of the base StdCouchDbConnector that allows us to run queries against couchdb
 */
public class LuceneAwareCouchDbConnector extends StdCouchDbConnector {

    public static String DEFAULT_LUCENE_PREFIX = "_fti"; // this is the default location of the couchdb-lucene indexer
    public static String DEFAULT_LUCENE_INDEX = "local"; // this is the default alias of the couchdb for 1.1 URI change


    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private final String lucenePrefix;
    private final String luceneIndex;
    private final String dbName;

    public LuceneAwareCouchDbConnector(String databaseName, CouchDbInstance dbInstance) throws IOException {
        super(databaseName, dbInstance, new EktorpLuceneObjectMapperFactory());
        this.dbName = databaseName;
        this.restTemplate = new RestTemplate(dbInstance.getConnection());
        this.objectMapper = new EktorpLuceneObjectMapperFactory().createObjectMapper();

        HttpResponse response = dbInstance.getConnection().get("/");
        JsonNode node = objectMapper.readValue(response.getContent(), JsonNode.class);
        String version = node.get("version").asText();
        if (version.startsWith("0.") || version.startsWith("1.0")) {
            this.lucenePrefix = DEFAULT_LUCENE_PREFIX;
            this.luceneIndex = null;
        } else {
            this.lucenePrefix = DEFAULT_LUCENE_PREFIX;
            this.luceneIndex = DEFAULT_LUCENE_INDEX;
        }
    }

    public LuceneAwareCouchDbConnector(String databaseName, CouchDbInstance dbInstance,
                                       String lucenePrefix, String luceneIndex) {
        super(databaseName, dbInstance, new EktorpLuceneObjectMapperFactory());
        this.dbName = databaseName;
        this.restTemplate = new RestTemplate(dbInstance.getConnection());
        this.objectMapper = new EktorpLuceneObjectMapperFactory().createObjectMapper();
        this.lucenePrefix = lucenePrefix;
        this.luceneIndex = luceneIndex;
    }


    private URI buildIndexRoot() {
        final URI uri;
        if (luceneIndex != null) {
            uri = URI.of("/" + lucenePrefix);
            uri.append(luceneIndex);
            uri.append(dbName);
        } else {
            uri = URI.of(path());
            uri.append(lucenePrefix);
        }
        return uri;
    }

    public LuceneResult queryLucene(LuceneQuery query) {
        Assert.notNull(query, "query cannot be null");
        ResponseCallback<LuceneResult> rh = new StdResponseHandler<LuceneResult>() {

            public LuceneResult success(HttpResponse hr) throws Exception {
                return objectMapper.readValue(hr.getContent(), LuceneResult.class);
            }

        };
        return restTemplate.get(query.buildQuery(buildIndexRoot()), rh);
    }

    public CustomLuceneResult queryLucene(LuceneQuery query, final TypeReference type) {
        Assert.notNull(query, "query cannot be null");
        ResponseCallback<CustomLuceneResult> rh = new StdResponseHandler<CustomLuceneResult>() {

            public CustomLuceneResult success(HttpResponse hr) throws Exception {
                return (CustomLuceneResult) objectMapper.readValue(hr.getContent(), type);
            }

        };
        return restTemplate.get(query.buildQuery(buildIndexRoot()), rh);
    }
}
