package com.github.ldriscoll;

import com.github.ldriscoll.ektorplucene.CouchDbRepositorySupportWithLucene;
import com.github.ldriscoll.ektorplucene.LuceneAwareCouchDbConnector;
import com.github.ldriscoll.ektorplucene.LuceneQuery;
import com.github.ldriscoll.ektorplucene.LuceneResult;
import com.github.ldriscoll.ektorplucene.designdocument.LuceneDesignDocument;
import com.github.ldriscoll.ektorplucene.designdocument.annotation.FullText;
import com.github.ldriscoll.ektorplucene.designdocument.annotation.Index;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbInstance;
import org.ektorp.support.DesignDocument;
import org.ektorp.support.OpenCouchDbDocument;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: ldriscoll
 * Date: 4/25/12
 * Time: 3:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class RepositoryTest {

    private LuceneAwareCouchDbConnector connector;
    private Repo repo;

    @Before
    public void setUp() throws IOException {
        System.setProperty(DesignDocument.UPDATE_ON_DIFF, "true");
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
        repo = new Repo(connector, true);
        createDocuments();
    }


    @After
    public void tearDown() {
        deleteDocuments();
        connector = null;
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

    @FullText({
            @Index(
                    name = "by_title",
                    index = "function(doc) { " +
                            "    var res = new Document(); " +
                            "    res.add(doc.field1, {\"field\": \"field1\", \"store\": \"yes\"});" +
                            "    res.add(doc.field2, {\"field\": \"field2\", \"store\": \"yes\"});"+
                            "    return res; " +
                            "}")
    })
    public static class Repo extends CouchDbRepositorySupportWithLucene<LuceneSearchTest.TestDocument> {
        protected Repo(LuceneAwareCouchDbConnector db, boolean createIfNotExists) {
            super(LuceneSearchTest.TestDocument.class, db, createIfNotExists);
            initStandardDesignDocument();
        }

        public LuceneResult findDocs() {
            // create a simple query against the view/search function that we've created
            LuceneDesignDocument designDoc = db.get(LuceneDesignDocument.class, stdDesignDocumentId);
            assertTrue(designDoc != null);
            assertTrue(designDoc.containsIndex("by_title"));

            LuceneQuery query = new LuceneQuery(designDoc.getId(), "by_title");
            query.setQuery("field1:test AND field2:here");
            // stale must not be ok, as we've only just loaded the docs
            query.setStaleOk(false);
            return db.queryLucene(query);
        }
    }


    @Test
    public void testInit() {


        LuceneResult result = repo.findDocs();
        assertNotNull("Expecting a non null result", result);
        assertTrue("Should only have one result", result.getRows().size() == 1);
        assertTrue("The results's id should be test2", result.getRows().get(0).getId().equals("test2"));

    }


}
