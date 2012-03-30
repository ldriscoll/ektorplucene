package com.github.ldriscoll;

import com.github.ldriscoll.ektorplucene.CustomLuceneResult;
import com.github.ldriscoll.ektorplucene.LuceneAwareCouchDbConnector;
import com.github.ldriscoll.ektorplucene.LuceneQuery;
import com.github.ldriscoll.ektorplucene.LuceneResult;
import com.github.ldriscoll.ektorplucene.util.IndexUploader;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.type.TypeReference;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbInstance;
import org.ektorp.support.OpenCouchDbDocument;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static junit.framework.Assert.assertEquals;
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

    @Test
    public void testSearchWithCustomResultType(){
        createDocuments();
        LuceneQuery query = new LuceneQuery(Const.VIEW_NAME, Const.SEARCH_FUNCTION);
        query.setQuery("field1:test AND field2:here");
        query.setIncludeDocs(true);

        try {
            TypeReference resultDocType = new TypeReference<CustomLuceneResult<TestDocument>>() {};
            CustomLuceneResult customLuceneResult = connector.queryLucene(query, resultDocType);
            assertNotNull("Expecting a non null result", customLuceneResult);
            assertTrue("Should only have one result", customLuceneResult.getRows().size() == 1);

            List<CustomLuceneResult.Row<TestDocument>> resultRows = customLuceneResult.getRows();
            assertEquals("TestDocument", resultRows.get(0).getDoc().getClass().getSimpleName());
            assertTrue("The result's id should be test2", resultRows.get(0).getId().equals("test2"));
            assertTrue("The result's field1 should be test", resultRows.get(0).getDoc().getField1().equals("test"));
            assertTrue("The result's field2 should be here", resultRows.get(0).getDoc().getField2().equals("here"));
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

    public static class TestDocument {
        private String _id = UUID.randomUUID().toString();
        private String _rev;
        private String field1;
        private String field2;

        @JsonProperty
        public void set_id(String _id) {
            this._id = _id;
        }

        public void set_rev(String _rev) {
            this._rev = _rev;
        }

        public String getField1() {
            return field1;
        }

        public void setField1(String field1) {
            this.field1 = field1;
        }

        public String getField2() {
            return field2;
        }

        public void setField2(String field2) {
            this.field2 = field2;
        }
    }

    @BeforeClass
    public static void setUpClass() {
        indexEverythingJS = IndexUploaderTest.readJSFile(Const.INDEX_EVERYTHING_JS);
        assertNotNull(indexEverythingJS);
    }

    @Before
    public void setUp() throws IOException {
        HttpClient httpClient = new StdHttpClient.Builder()
                    .host("localhost")
                    .port(5984)
                    .socketTimeout(1000)
                    .username("testadmin").password("testpass")
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
