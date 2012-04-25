package com.github.ldriscoll.ektorplucene;

import java.util.Random;

import org.ektorp.CouchDbConnector;
import org.ektorp.UpdateConflictException;
import org.ektorp.support.CouchDbRepositorySupport;

import com.github.ldriscoll.ektorplucene.designdocument.LuceneDesignDocument;
import com.github.ldriscoll.ektorplucene.designdocument.LuceneDesignDocumentFactory;


public class CouchDbRepositorySupportWithLucene<T> extends CouchDbRepositorySupport<T> {

    private LuceneDesignDocumentFactory luceneDesignDocumentFactory;

    protected CouchDbRepositorySupportWithLucene(Class<T> type, CouchDbConnector db) {
        super(type, db);
    }

    protected CouchDbRepositorySupportWithLucene(Class<T> type, CouchDbConnector db, boolean createIfNotExists) {
        super(type, db, createIfNotExists);
    }

    protected CouchDbRepositorySupportWithLucene(Class<T> type, CouchDbConnector db, String designDocName) {
        super(type, db, designDocName);
    }

    @Override
    public void initStandardDesignDocument() {
        initDesignDocInternal(0);
    }

    private void initDesignDocInternal(int invocations) {
        LuceneDesignDocument designDoc;
        if (db.contains(stdDesignDocumentId)) {
            designDoc = db.get(LuceneDesignDocument.class, stdDesignDocumentId);
        } else {
            designDoc = new LuceneDesignDocument(stdDesignDocumentId);
        }
        log.debug("Generating DesignDocument for {}", type);
        LuceneDesignDocument generated = getDesignDocumentFactory().generateFrom(this);
        boolean changed = designDoc.mergeWith(generated);
        if (log.isDebugEnabled()) {
            debugDesignDoc(designDoc);
        }
        if (changed) {
            log.debug("DesignDocument changed or new. Updating database");
            try {
                db.update(designDoc);
            } catch (UpdateConflictException e) {
                log.warn("Update conflict occurred when trying to update design document: {}", designDoc.getId());
                if (invocations == 0) {
                    backOff();
                    log.info("retrying initStandardDesignDocument for design document: {}", designDoc.getId());
                    initDesignDocInternal(1);
                }
            }
        } else if (log.isDebugEnabled()) {
            log.debug("DesignDocument was unchanged. Database was not updated.");
        }
    }

    /**
     * Wait a short while in order to prevent racing initializations from other repositories.
     */
    private void backOff() {
        try {
            Thread.sleep(new Random().nextInt(400));
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            return;
        }
    }

    @Override
    protected LuceneDesignDocumentFactory getDesignDocumentFactory() {
        if (luceneDesignDocumentFactory == null) {
            luceneDesignDocumentFactory = new LuceneDesignDocumentFactory();
        }
        return luceneDesignDocumentFactory;
    }

}
