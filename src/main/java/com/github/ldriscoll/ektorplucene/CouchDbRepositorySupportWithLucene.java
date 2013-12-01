package com.github.ldriscoll.ektorplucene;

import org.ektorp.support.CouchDbRepositorySupport;

import com.github.ldriscoll.ektorplucene.designdocument.LuceneDesignDocumentFactory;


public class CouchDbRepositorySupportWithLucene<T> extends CouchDbRepositorySupport<T> {

    protected LuceneAwareCouchDbConnector db;

    protected CouchDbRepositorySupportWithLucene(Class<T> type, LuceneAwareCouchDbConnector db) {
        super(type, db);
        this.db = db;
        setDesignDocumentFactory(new LuceneDesignDocumentFactory());
    }

    protected CouchDbRepositorySupportWithLucene(Class<T> type, LuceneAwareCouchDbConnector db, boolean createIfNotExists) {
        super(type, db, createIfNotExists);
        this.db = db;
        setDesignDocumentFactory(new LuceneDesignDocumentFactory());
    }

    protected CouchDbRepositorySupportWithLucene(Class<T> type, LuceneAwareCouchDbConnector db, String designDocName) {
        super(type, db, designDocName);
        this.db = db;
        setDesignDocumentFactory(new LuceneDesignDocumentFactory());
    }

}
