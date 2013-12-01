package com.github.ldriscoll.ektorplucene;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

class CommonLuceneResult implements Serializable {

    static final String FETCH_DURATION_FIELD_NAME = "fetch_duration";
    static final String SEARCH_DURATION_FIELD_NAME = "search_duration";
    static final String TOTAL_ROWS_FIELD_NAME = "total_rows";
    static final String SORT_ORDER_FIELD_NAME = "sort_order";
    static final String QUERY_FIELD_NAME = "q";


    protected String analyzer;
    protected String etag;
    protected int fetchDuration = -1;
    protected int limit = -1;
    protected String plan;
    protected List<SortDetail> sortOrder;
    protected String query;

    protected int searchDuration = -1;
    protected int skip = -1;

    protected int totalRows = -1;

    private String getStringValue(JsonNode node, String fieldName) {
        JsonNode resNode = node.get(fieldName);
        if (resNode == null) return null;
        return resNode.asText();
    }


    /**
     * The analyzer that was used during processing.  Please refer to https://github.com/rnewson/couchdb-lucene
     * for more details
     *
     * @return
     */
    public String getAnalyzer() {
        return analyzer;
    }

    @JsonProperty
    public void setAnalyzer(String analyzer) {
        this.analyzer = analyzer;
    }

    /**
     * Token that reflects the current version of the index
     *
     * @return
     */
    public String getEtag() {
        return etag;
    }

    @JsonProperty
    public void setEtag(String etag) {
        this.etag = etag;
    }

    /**
     * Number of milliseconds spent retrieving the documents
     *
     * @return
     */
    public int getFetchDuration() {
        return fetchDuration;
    }

    @JsonProperty(FETCH_DURATION_FIELD_NAME)
    public void setFetchDuration(int fetchDuration) {
        this.fetchDuration = fetchDuration;
    }

    /**
     * The maximum number of results that are returned
     *
     * @return
     */
    public int getLimit() {
        return limit;
    }

    @JsonProperty
    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getPlan() {
        return plan;
    }

    @JsonProperty
    public void setPlan(String plan) {
        this.plan = plan;
    }

    /**
     * The query that was executed
     *
     * @return
     */
    public String getQuery() {
        return query;
    }

    @JsonProperty(QUERY_FIELD_NAME)
    public void setQuery(String query) {
        this.query = query;
    }

    /**
     * The sort order
     *
     * @return
     */
    public List<SortDetail> getSortOrder() {
        return sortOrder;
    }

    @JsonProperty(SORT_ORDER_FIELD_NAME)
    public void setSortOrder(List<SortDetail> sortOrder) {
        this.sortOrder = sortOrder;
    }

    /**
     * Number of milliseconds spent performing the search
     *
     * @return
     */
    public int getSearchDuration() {
        return searchDuration;
    }

    @JsonProperty(SEARCH_DURATION_FIELD_NAME)
    public void setSearchDuration(int searchDuration) {
        this.searchDuration = searchDuration;
    }

    /**
     * The number of initial matches that were skipped
     *
     * @return
     */
    public int getSkip() {
        return skip;
    }

    @JsonProperty
    public void setSkip(int skip) {
        this.skip = skip;
    }

    /**
     * Total number of rows that match the query
     *
     * @return
     */
    public int getTotalRows() {
        return totalRows;
    }

    @JsonProperty(TOTAL_ROWS_FIELD_NAME)
    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }

    public static class SortDetail {
        private String field;
        private boolean reverse = false;
        private String type;

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public boolean isReverse() {
            return reverse;
        }

        public void setReverse(boolean reverse) {
            this.reverse = reverse;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}

