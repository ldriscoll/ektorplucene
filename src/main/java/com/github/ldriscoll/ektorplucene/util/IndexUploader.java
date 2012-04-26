package com.github.ldriscoll.ektorplucene.util;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.DesignDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 * Created by IntelliJ IDEA.
 * User: ldriscoll
 * Date: 5/6/11
 * Time: 9:18 AM
 * This class can be used to upload indexing functions to couchdb.  A sample is index_everything.js.
 */
public class IndexUploader {

    private final Logger log = LoggerFactory.getLogger(IndexUploader.class);
    private static final String CHECKSUM_SUFFIX = "_checksum";

    /**
     * Ensures taht the given body of the index function matches that in the database, otherwise update it.
     *
     * @param db                          Connection to couchdb
     * @param viewName                    Name of the view that we're updating
     * @param searchFunction              Name of the search function, within that view
     * @param javascriptIndexFunctionBody The body of the javascript for that search function
     * @return whether or not the body of the document was updated.
     */
    public boolean updateSearchFunctionIfNecessary(CouchDbConnector db, String viewName, String searchFunction,
                                                   String javascriptIndexFunctionBody) {

        boolean updatePerformed = false;

        String designDocName = viewName.startsWith(DesignDocument.ID_PREFIX)
                ? viewName : (DesignDocument.ID_PREFIX + viewName);

        Checksum chk = new CRC32();
        byte[] bytes = javascriptIndexFunctionBody.getBytes();
        chk.update(bytes, 0, bytes.length);
        long actualChk = chk.getValue();

        if (!db.contains(designDocName)) {
            // it doesn't exist, let's put one in
            DesignDocument doc = new DesignDocument(designDocName);
            db.create(doc);
            updateSearchFunction(db, doc, searchFunction, javascriptIndexFunctionBody, actualChk);
            updatePerformed = true;
        } else {
            // make sure it's up to date
            DesignDocument doc = db.get(DesignDocument.class, designDocName);
            Number docChk = (Number) doc.getAnonymous().get(getChecksumFieldName(searchFunction));
            if (docChk == null || !(docChk.longValue() == actualChk)) {
                log.info("Updating the index function");
                updateSearchFunction(db, doc, searchFunction, javascriptIndexFunctionBody, actualChk);
                updatePerformed = true;
            }
        }

        return updatePerformed;
    }

    /**
     * In case there are multiple search functions in this design document, we want to ensure that we checksum the right one
     *
     * @param searchFunction the name of the search function for which the checksum applies
     * @return
     */
    private String getChecksumFieldName(String searchFunction) {
        return searchFunction + CHECKSUM_SUFFIX;
    }


    /**
     * Creates the design document that we use when searching.  This contains the index function that is used by lucene
     * in the future we will probably want to support customer specific instances, as well as adding a checksum
     * to the object, so that we can test if an index needs to be upgraded
     *
     * @param db                 Couch connection
     * @param doc                The design document that we're updating
     * @param searchFunctionName The name of the search function that the user wants to index couch with
     * @param javascript         The body of the javascript of the search function
     * @param jsChecksum         The checksum of the search function body
     */
    private void updateSearchFunction(CouchDbConnector db, DesignDocument doc, String searchFunctionName,
                                      String javascript, long jsChecksum) {

        // get the 'fulltext' object from the design document, this is what couchdb-lucene uses to index couch
        Map<String, Map<String, String>> fullText = (Map<String, Map<String, String>>) doc.getAnonymous().get("fulltext");
        if (fullText == null) {
            fullText = new HashMap<String, Map<String, String>>();
            doc.setAnonymous("fulltext", fullText);
        }
        // now grab the search function that the user wants to use to index couch
        Map<String, String> searchObject = fullText.get(searchFunctionName);
        if (searchObject == null) {
            searchObject = new HashMap<String, String>();
            fullText.put(searchFunctionName, searchObject);
        }

        // now set the contents of the index function
        searchObject.put("index", javascript);
        // now set the checksum, so that we can make sure that we only update it when we need to
        doc.setAnonymous(getChecksumFieldName(searchFunctionName), jsChecksum);

        // save out the document
        db.update(doc);

    }


}
