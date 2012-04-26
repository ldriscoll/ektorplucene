package com.github.ldriscoll.ektorplucene.designdocument;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.ektorp.util.Assert;

import com.github.ldriscoll.ektorplucene.designdocument.annotation.Index;

/**
 * Definition of an index in a design document.
 * @author Sean Adkinson
 */
@JsonSerialize(include = Inclusion.NON_NULL)
public class LuceneIndex {
    @JsonProperty
    private String index;
    @JsonProperty
    private LuceneDefaults defaults;
    @JsonProperty
    private String analyzer;

    public static LuceneIndex fromAnnotation(Index idx) {
    	Assert.hasText(idx.index(), "The index function can't be null or empty");
    	
        LuceneIndex li =  new LuceneIndex();
		li.setIndex(idx.index());
		li.setDefaults(LuceneDefaults.fromAnnotation(idx.defaults()));
    	if (!StringUtils.isBlank(idx.analyzer())) {
    		li.setAnalyzer(idx.analyzer());
    	}
    	return li;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }
    
	public LuceneDefaults getDefaults() {
		return defaults;
	}
	
	public void setDefaults(LuceneDefaults defaults) {
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
        LuceneIndex other = (LuceneIndex) obj;
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