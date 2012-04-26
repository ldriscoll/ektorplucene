package com.github.ldriscoll.ektorplucene;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class CustomLuceneResult<T> extends CommonLuceneResult {

    private List<Row<T>> rows;

    @JsonProperty
    public void setRows(List<Row<T>> rows) {
        this.rows = rows;
    }

    public List<Row<T>> getRows() {
        return rows;
    }

    public static class Row<T> extends CommonRow {

        private T doc;

        public T getDoc() {
            return doc;
        }

        @JsonProperty
        public void setDoc(T doc) {
            this.doc = doc;
        }
    }
}
