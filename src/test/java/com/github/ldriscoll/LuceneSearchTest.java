package com.github.ldriscoll;

import com.github.ldriscoll.ektorplucene.LuceneAwareCouchDbConnector;
import com.github.ldriscoll.ektorplucene.LuceneQuery;
import com.github.ldriscoll.ektorplucene.LuceneResult;
import com.github.ldriscoll.ektorplucene.util.IndexUploader;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbInstance;
import org.ektorp.support.OpenCouchDbDocument;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: ldriscoll
 * Date: 5/6/11
 * Time: 10:11 AM
 * To change this template use File | Settings | File Templates.
 */
public class LuceneSearchTest {
    private static String indexEverythingJS;

    private LuceneAwareCouchDbConnector connector;

    @Test
    public void testSearch() {
        createDocuments();
        
        // create a simple query against the view/search function that we've created
        LuceneQuery query = new LuceneQuery(Const.VIEW_NAME, Const.SEARCH_FUNCTION);
        query.setQuery("field1:test AND field2:here");
        // stale must not be ok, as we've only just loaded the docs
        query.setStaleOk(false);

        try {
            LuceneResult result = connector.queryLucene(query);
            assertNotNull("Expecting a non null result", result);
            assertTrue("Should only have one result", result.getRows().size() == 1);
            assertTrue("The results's id should be test2", result.getRows().get(0).getId().equals("test2"));
        }
        finally {
            deleteDocuments();
        }
    }


    @Test
    public void testSearchWithSort() {
        createDocuments();

        // create a simple query against the view/search function that we've created
        LuceneQuery query = new LuceneQuery(Const.VIEW_NAME, Const.SEARCH_FUNCTION);
        query.setQuery("field1:test AND field2:here");
        query.setSort("/field1");
        // stale must not be ok, as we've only just loaded the docs
        query.setStaleOk(false);

        try {
            LuceneResult result = connector.queryLucene(query);
            assertNotNull("Expecting a non null result", result);
            assertTrue("Should only have one result", result.getRows().size() == 1);
            assertTrue("The results's id should be test2", result.getRows().get(0).getId().equals("test2"));
            assertNotNull("Result sort order returned by couchdb-lucene", result.getSortOrder());
        }
        finally {
            deleteDocuments();
        }
    }

    private void createDocuments() {
        updateDocument("test1", "field1", "test");
        updateDocument("test2", "field1", "test");
        updateDocument("test2", "field2", "here");
    }

    private void deleteDocuments() {
        deleteDocument("test1");
        deleteDocument("test2");
    }

    private void updateDocument(String name, String field, String value) {
        final OpenCouchDbDocument doc;
        if (connector.contains(name)) {
            doc = connector.get(OpenCouchDbDocument.class, name);
        }
        else {
            doc = new OpenCouchDbDocument();
            doc.setId(name);
            connector.create(doc);
        }

        doc.setAnonymous(field, value);
        connector.update(doc);
    }

    private void deleteDocument(String name) {
        if (connector.contains(name)) {
            OpenCouchDbDocument doc = connector.get(OpenCouchDbDocument.class, name);
            connector.delete(doc);
        }
    }

    @BeforeClass
    public static void setUpClass() {
        indexEverythingJS = IndexUploaderTest.readJSFile(Const.INDEX_EVERYTHING_JS);
        assertNotNull(indexEverythingJS);
    }

    @Before
    public void setUp() {
        HttpClient httpClient = new StdHttpClient.Builder()
                    .host("localhost")
                    .port(5984)
                    .socketTimeout(1000)
                    .build();
        CouchDbInstance instance = new StdCouchDbInstance(httpClient);

        // don't really need a lucene aware couchdb connector, but just testing it in case
        connector = new LuceneAwareCouchDbConnector(Const.TEST_DATABASE, instance);
        connector.createDatabaseIfNotExists();

        // make sure that the indexer is upto date
        IndexUploader uploader = new IndexUploader();
        uploader.updateSearchFunctionIfNecessary(connector, Const.VIEW_NAME, Const.SEARCH_FUNCTION, indexEverythingJS);

    }


    @After
    public void tearDown() {
        connector = null;
    }
}
