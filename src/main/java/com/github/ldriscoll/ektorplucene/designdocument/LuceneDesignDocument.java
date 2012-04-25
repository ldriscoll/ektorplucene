package com.github.ldriscoll.ektorplucene.designdocument;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.ektorp.support.DesignDocument;
import org.ektorp.util.Assert;

/**
 * Extension of {@link DesignDocument} that supports the "fulltext" attribute
 * for Lucene indexes.
 *
 * @author seanadkinson
 */
public class LuceneDesignDocument extends DesignDocument {

    private static final long serialVersionUID = 5232811585073232156L;
    private Map<String, LuceneDesignDocument.Index> indexes;

    public LuceneDesignDocument() {
        super();
    }

    public LuceneDesignDocument(String id) {
        super(id);
    }

    @JsonProperty
    public Map<String, LuceneDesignDocument.Index> getFulltext() {
        return Collections.unmodifiableMap(indexes());
    }

    @JsonProperty
    void setFulltext(Map<String, LuceneDesignDocument.Index> indexes) {
        this.indexes = indexes;
    }

    private Map<String, LuceneDesignDocument.Index> indexes() {
        if (indexes == null) {
            indexes = new HashMap<String, LuceneDesignDocument.Index>();
        }
        return indexes;
    }

    public boolean containsIndex(String name) {
        return indexes().containsKey(name);
    }

    public LuceneDesignDocument.Index getIndex(String indexName) {
        return indexes().get(indexName);
    }

    public void addIndex(String name, LuceneDesignDocument.Index i) {
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

    private boolean mergeIndexes(Map<String, LuceneDesignDocument.Index> mergeIndexes, boolean updateOnDiff) {
        boolean changed = false;
        for (Map.Entry<String, LuceneDesignDocument.Index> e : mergeIndexes.entrySet()) {
            String name = e.getKey();
            LuceneDesignDocument.Index candidate = e.getValue();
            if (!containsIndex(name)) {
                addIndex(name, candidate);
                changed = true;
            } else if (updateOnDiff) {
                LuceneDesignDocument.Index existing = getIndex(name);
                if (!existing.equals(candidate)) {
                    addIndex(name, candidate);
                    changed = true;
                }
            }
        }
        return changed;
    }

    // TODO: Make super class method PROTECTED
    private boolean updateOnDiff() {
        return Boolean.getBoolean(AUTO_UPDATE_VIEW_ON_CHANGE) || Boolean.getBoolean(UPDATE_ON_DIFF);
    }

    /**
     * Definition of an index in a design document.
     *
     * @author Sean Adkinson
     */
    @JsonSerialize(include = Inclusion.NON_NULL)
    public static class Index {
        @JsonProperty
        private String index;
        @JsonProperty
        private String defaults;
        @JsonProperty
        private String analyzer;

        public Index() {
        }

        public static Index of(com.github.ldriscoll.ektorplucene.designdocument.Index idx) {
            return new LuceneDesignDocument.Index(idx.index(), idx.defaults(), idx.analyzer());
        }

        public Index(String index) {
            Assert.hasText(index, "The index function cannot be null or empty!");
            this.index = index;
        }

        public Index(String index, String defaults, String analyzer) {
            this(index);
            this.defaults = (defaults == null || defaults.trim().length() == 0) ? null : defaults;
            this.analyzer = (analyzer == null || analyzer.trim().length() == 0) ? null : analyzer;
        }

        public String getIndex() {
            return index;
        }

        public void setIndex(String index) {
            this.index = index;
        }

        public String getDefaults() {
            return defaults;
        }

        public void setDefaults(String defaults) {
            this.defaults = defaults;
        }

        public String getAnalyzer() {
            return analyzer;
        }

        public void setAnalyzer(String analyzer) {
            this.analyzer = analyzer;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((index == null) ? 0 : index.hashCode());
            result = prime * result + ((defaults == null) ? 0 : defaults.hashCode());
            result = prime * result + ((analyzer == null) ? 0 : analyzer.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Index other = (Index) obj;
            if (index == null) {
                if (other.index != null)
                    return false;
            } else if (!index.equals(other.index))
                return false;
            if (defaults == null) {
                if (other.defaults != null)
                    return false;
            } else if (!defaults.equals(other.defaults))
                return false;
            if (analyzer == null) {
                if (other.analyzer != null)
                    return false;
            } else if (!analyzer.equals(other.analyzer))
                return false;
            return true;
        }

    }

}
