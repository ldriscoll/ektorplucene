package com.github.ldriscoll;

import com.github.ldriscoll.ektorplucene.LuceneAwareCouchDbConnector;
import com.github.ldriscoll.ektorplucene.util.IndexUploader;

import org.apache.commons.io.IOUtils;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbInstance;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: ldriscoll
 * Date: 5/6/11
 * Time: 9:44 AM
 * To change this template use File | Settings | File Templates.
 */
public class IndexUploaderTest {
    private final IndexUploader uploader = new IndexUploader();


    private static String indexEverythingJS;
    private static String indexNothingJS;

    private CouchDbConnector connector;




    @Test
    public void testChangeWorks() {
        uploader.updateSearchFunctionIfNecessary(connector, Const.VIEW_NAME, Const.SEARCH_FUNCTION, indexNothingJS);

        // now make sure that when we load the index everything it has changed
        boolean updated;
        updated = uploader.updateSearchFunctionIfNecessary(connector, Const.VIEW_NAME, Const.SEARCH_FUNCTION, indexEverythingJS);
        assertTrue("View should update", updated);

        // now make sure that it doesn't change this time
        updated = uploader.updateSearchFunctionIfNecessary(connector, Const.VIEW_NAME, Const.SEARCH_FUNCTION, indexEverythingJS);
        assertFalse("View should not update", updated);
    }


    @BeforeClass
    public static void setUpClass() {
        indexEverythingJS = readJSFile(Const.INDEX_EVERYTHING_JS);
        assertNotNull(indexEverythingJS);

        indexNothingJS = readJSFile(Const.INDEX_NOTHING_JS);
        assertNotNull(indexNothingJS);
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
    }

    @After
    public void tearDown() {
        connector = null;
    }



    /**
     * Reads in the given js file name form the classpath
     * @param jsFileName
     * @return
     */
    protected static String readJSFile(String jsFileName) {

        // load the js out of the classpath
        InputStream is = IndexUploaderTest.class.getResourceAsStream("/" + jsFileName);

        try {
            String js = IOUtils.toString(is);
            return js;
        } catch (IOException e) {
            throw new RuntimeException("Problem loading the indexing js function from the package", e);
        }
    }
}
