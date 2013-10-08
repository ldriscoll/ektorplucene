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

import java.util.LinkedHashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The result of the lucene query.
 */

public class LuceneResult extends CommonLuceneResult {

    private List<Row> rows;

    public List<Row> getRows() {
        return rows;
    }

    @JsonProperty
    public void setRows(List<Row> rows) {
        this.rows = rows;
    }


    public static class Row extends CommonRow {

        private LinkedHashMap<String, Object> doc;

        /**
         * The stored contents of the document (when include_docs=true)
         *
         * @return
         */
        public LinkedHashMap<String, Object> getDoc() {
            return doc;
        }

        @JsonProperty
        public void setDoc(LinkedHashMap<String, Object> doc) {
            this.doc = doc;
        }
    }
}
