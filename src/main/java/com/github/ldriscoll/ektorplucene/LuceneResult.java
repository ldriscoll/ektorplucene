package com.github.ldriscoll.ektorplucene;

/**
 * Copyright 2011 Luke Driscoll
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Based on org.ektorp.ViewResult
 * User: ldriscoll
 * Date: 12/9/10
 * Time: 3:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class LuceneResult implements Serializable {

    private static final String FETCH_DURATION_FIELD_NAME = "fetch_duration";
    private static final String SEARCH_DURATION_FIELD_NAME = "search_duration";
    private static final String TOTAL_ROWS_FIELD_NAME = "total_rows";



    private String analyzer;
    private String etag;
    private int fetchDuration = -1;
    private int limit = -1;
    private String plan;
    private String q;

    private int searchDuration = -1;
    private int skip = -1;

    private int totalRows = -1;
    private List<Row> rows;


    private String getStringValue(JsonNode node, String fieldName) {
        JsonNode resNode = node.get(fieldName);
        if (resNode == null) return null;
        return resNode.getTextValue();
    }


    public String getAnalyzer() {
        return analyzer;
    }

    @JsonProperty
    public void setAnalyzer(String analyzer) {
        this.analyzer = analyzer;
    }

    public String getEtag() {
        return etag;
    }

    @JsonProperty
    public void setEtag(String etag) {
        this.etag = etag;
    }

    public int getFetchDuration() {
        return fetchDuration;
    }

    @JsonProperty(FETCH_DURATION_FIELD_NAME)
    public void setFetchDuration(int fetchDuration) {
        this.fetchDuration = fetchDuration;
    }

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

    public String getQ() {
        return q;
    }

    @JsonProperty
    public void setQ(String q) {
        this.q = q;
    }

    public int getSearchDuration() {
        return searchDuration;
    }

    @JsonProperty(SEARCH_DURATION_FIELD_NAME)
    public void setSearchDuration(int searchDuration) {
        this.searchDuration = searchDuration;
    }

    public int getSkip() {
        return skip;
    }

    @JsonProperty
    public void setSkip(int skip) {
        this.skip = skip;
    }

    public int getTotalRows() {
        return totalRows;
    }

    @JsonProperty(TOTAL_ROWS_FIELD_NAME)
    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }

    public List<Row> getRows() {
        return rows;
    }

    @JsonProperty
    public void setRows(List<Row> rows) {
        this.rows = rows;
    }

    public static class Row {

        private LinkedHashMap<String, Object> fields;
        private String id;
        private float score = -1;


        public LinkedHashMap<String, Object> getFields() {
            return fields;
        }

        @JsonProperty
        public void setFields(LinkedHashMap<String, Object> fields) {
            this.fields = fields;
        }

        public String getId() {
            return id;
        }

        @JsonProperty
        public void setId(String id) {
            this.id = id;
        }

        public float getScore() {
            return score;
        }

        @JsonProperty
        public void setScore(float score) {
            this.score = score;
        }
    }
}
