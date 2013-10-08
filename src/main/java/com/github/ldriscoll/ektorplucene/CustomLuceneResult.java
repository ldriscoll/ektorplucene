package com.github.ldriscoll.ektorplucene;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

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
