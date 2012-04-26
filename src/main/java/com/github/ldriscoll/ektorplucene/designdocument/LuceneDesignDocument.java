package com.github.ldriscoll.ektorplucene.designdocument;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.DesignDocument;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Extension of {@link DesignDocument} that supports the "fulltext" attribute
 * for Lucene indexes.
 *
 * @author seanadkinson
 */
public class LuceneDesignDocument extends DesignDocument {

    private static final long serialVersionUID = 5232811585073232156L;
    private Map<String, LuceneIndex> indexes;

    public LuceneDesignDocument() {
        super();
    }

    public LuceneDesignDocument(String id) {
        super(id);
    }

    @JsonProperty
    public Map<String, LuceneIndex> getFulltext() {
        return Collections.unmodifiableMap(indexes());
    }

    @JsonProperty
    void setFulltext(Map<String, LuceneIndex> indexes) {
        this.indexes = indexes;
    }

    private Map<String, LuceneIndex> indexes() {
        if (indexes == null) {
            indexes = new HashMap<String, LuceneIndex>();
        }
        return indexes;
    }

    public boolean containsIndex(String name) {
        return indexes().containsKey(name);
    }

    public LuceneIndex getIndex(String indexName) {
        return indexes().get(indexName);
    }

    public void addIndex(String name, LuceneIndex i) {
        indexes().put(name, i);
    }

    public void removeIndex(String name) {
        indexes().remove(name);
    }

    @Override
    public boolean mergeWith(DesignDocument dd) {
        boolean changed = super.mergeWith(dd);
        if (dd instanceof LuceneDesignDocument) {
            boolean updateOnDiff = updateOnDiff();
            changed = mergeIndexes(((LuceneDesignDocument) dd).indexes(), updateOnDiff) || changed;
        }
        return changed;
    }

    private boolean mergeIndexes(Map<String, LuceneIndex> mergeIndexes, boolean updateOnDiff) {
        boolean changed = false;
        for (Map.Entry<String, LuceneIndex> e : mergeIndexes.entrySet()) {
            String name = e.getKey();
            LuceneIndex candidate = e.getValue();

            //add new indexes
            if (!containsIndex(name)) {
                addIndex(name, candidate);
                changed = true;
            }

            //maybe update existing
            else if (updateOnDiff) {
                LuceneIndex existing = getIndex(name);
                if (!existing.equals(candidate)) {
                    addIndex(name, candidate);
                    changed = true;
                }
            }
        }

        //remove indexes that don't match
        Set<String> toRemove = new HashSet<String>();
        for (String existingIndexName : indexes().keySet()) {
            if (!mergeIndexes.containsKey(existingIndexName)) {
                toRemove.add(existingIndexName);
                changed = true;
            }
        }
        for (String removeIndexName : toRemove) {
            removeIndex(removeIndexName);
        }

        return changed;
    }

    // TODO: Make super class method PROTECTED
    private boolean updateOnDiff() {
        return Boolean.getBoolean(AUTO_UPDATE_VIEW_ON_CHANGE) || Boolean.getBoolean(UPDATE_ON_DIFF);
    }

}
