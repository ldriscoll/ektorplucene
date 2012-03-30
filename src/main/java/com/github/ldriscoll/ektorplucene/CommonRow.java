package com.github.ldriscoll.ektorplucene;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.LinkedHashMap;
import java.util.List;

class CommonRow {
    protected LinkedHashMap<String, Object> fields;

    protected String id;
    protected List<String> sortOrder;
    protected float score = -1;


    /**
     * The stored contents of the document indexed fields
     *
     * @return
     */
    public LinkedHashMap<String, Object> getFields() {
        return fields;
    }

    @JsonProperty
    public void setFields(LinkedHashMap<String, Object> fields) {
        this.fields = fields;
    }


    /**
     * Id of the document that matches
     *
     * @return
     */
    public String getId() {
        return id;
    }

    @JsonProperty
    public void setId(String id) {
        this.id = id;
    }

    /**
     * The sort order
     *
     * @return
     */
    public List<String> getSortOrder() {
        return sortOrder;
    }

    @JsonProperty(CommonLuceneResult.SORT_ORDER_FIELD_NAME)
    public void setSortOrder(List<String> sortOrder) {
        this.sortOrder = sortOrder;
    }

    /**
     * Normalized score of the match
     *
     * @return
     */
    public float getScore() {
        return score;
    }

    @JsonProperty
    public void setScore(float score) {
        this.score = score;
    }
}
