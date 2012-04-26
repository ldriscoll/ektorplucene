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


import org.apache.commons.lang.StringUtils;
import org.ektorp.http.URI;
import org.ektorp.support.DesignDocument;

/**
 * Modeled off org.ektorp.ViewQuery: A LuceneQuery is the basic tool for querying couchdb-lucene (https://github.com/rnewson/couchdb-lucene).
 * it depends on couchdb-lucene being installed.  The names of the fields in this class match up with the
 * names of the fields used in couchdb-lucene, this may change to become more java like.
 * The index function and design document are well described on couchdb-lucene's website
 * so I will not re-write that here, however we do have a sample 'index_everything.js' in the package, that
 * can be used, in conjunction with IndexUploader, to create your initial indexing tool.
 */
public class LuceneQuery {

    public static enum Operator {OR, AND}

    private String analyzer;
    private String callback;
    private Boolean debug;
    private Operator defaultOperator;
    private Boolean forceJSON;
    private Boolean includeDocs;
    private Integer limit;
    private String query;
    private Integer skip;
    private String sort;
    private Boolean staleOk;


    private String cachedQuery;

    private final String designDocument;
    private final String indexFunction;


    /**
     * Creates a Lucene Query that will run against couchdb-lucene
     *
     * @param designDocument This is the name of the design document used to index couchdb in couchdb-lucene
     * @param indexFunction
     */
    public LuceneQuery(String designDocument, String indexFunction) {
        this.designDocument = designDocument.startsWith(DesignDocument.ID_PREFIX)
                ? designDocument : DesignDocument.ID_PREFIX + designDocument;
        this.indexFunction = indexFunction;
    }

    /**
     * Override the default analyzer used to parse the q parameter
     *
     * @param analyzer
     */
    public void setAnalyzer(String analyzer) {
        this.analyzer = analyzer;
    }

    /**
     * Specify a JSONP callback wrapper. The full JSON result will be prepended with
     * this parameter and also placed with parentheses."
     *
     * @param callback
     */
    public void setCallback(String callback) {
        this.callback = callback;
    }

    /**
     * Setting this to true disables response caching (the query is executed every time)
     * and indents the JSON response for readability.
     *
     * @param debug
     */
    public void setDebug(Boolean debug) {
        this.debug = debug;
    }

    /**
     * Change the default operator for boolean queries. Defaults to "OR",
     * other permitted value is "AND".
     *
     * @param defaultOperator
     */
    public void setDefaultOperator(Operator defaultOperator) {
        this.defaultOperator = defaultOperator;
    }

    /**
     * Usually couchdb-lucene determines the Content-Type of its response based on the
     * presence of the Accept header. If Accept contains "application/json", you get
     * "application/json" in the response, otherwise you get "text/plain;charset=utf8".
     * Some tools, like JSONView for FireFox, do not send the Accept header but do render
     * "application/json" responses if received. Setting force_json=true forces all response
     * to "application/json" regardless of the Accept header.
     *
     * @param forceJSON
     */
    public void setForceJSON(Boolean forceJSON) {
        this.forceJSON = forceJSON;
    }

    /**
     * whether to include the source docs
     *
     * @param includeDocs
     */
    public void setIncludeDocs(Boolean includeDocs) {
        this.includeDocs = includeDocs;
    }

    /**
     * the maximum number of results to return
     *
     * @param limit
     */
    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    /**
     * the query to run (e.g, subject:hello). If not specified, the default
     * field is searched. Multiple queries can be supplied, separated by commas;
     * the resulting JSON will be an array of responses.
     *
     * @param query
     */
    public void setQuery(String query) {
        this.query = query;
    }

    /**
     * the number of results to skip
     *
     * @param skip
     */
    public void setSkip(Integer skip) {
        this.skip = skip;
    }

    /**
     * the comma-separated fields to sort on. Prefix with / for ascending order
     * and \ for descending order (ascending is the default if not specified).
     * Type-specific sorting is also available by appending the type between angle
     * brackets (e.g, sort=amount<float>). Supported types are 'float', 'double',
     * 'int', 'long' and 'date'.
     *
     * @param sort
     */
    public void setSort(String sort) {
        this.sort = sort;
    }

    /**
     * If you set the stale option to ok, couchdb-lucene will not block if the
     * index is not up to date and it will immediately return results. Therefore
     * searches may be faster as Lucene caches important data (especially for sorting).
     * A query without stale=ok will block and use the latest data committed to the index.
     * Unlike CouchDBs stale=ok option for views, couchdb-lucene will trigger an index
     * update unless one is already running.
     *
     * @param staleOk
     */
    public void setStaleOk(Boolean staleOk) {
        this.staleOk = staleOk;
    }


    private boolean hasValue(Object o) {
        if (o instanceof String) {
            return !StringUtils.isEmpty((String) o);
        }
        return o != null;
    }


    private void addParam(URI query, String paramName, Object value) {
        if (hasValue(value)) {
            query.param(paramName, value.toString());
        }
    }

    String buildQuery(final URI query) {
        if (cachedQuery != null) {
            return cachedQuery;
        }
        query.append(designDocument);
        query.append(indexFunction);


        addParam(query, "analyzer", analyzer);
        addParam(query, "callback", callback);
        addParam(query, "debug", debug);
        addParam(query, "default_operator", defaultOperator);
        addParam(query, "force_json", forceJSON);
        addParam(query, "include_docs", includeDocs);
        addParam(query, "limit", limit);
        addParam(query, "q", this.query);
        addParam(query, "skip", skip);
        addParam(query, "sort", sort);
        if (staleOk != null && staleOk) {
            query.param("stale", "ok");
        }

        cachedQuery = query.toString();
        return cachedQuery;
    }

    public void reset() {
        this.cachedQuery = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LuceneQuery that = (LuceneQuery) o;

        if (analyzer != null ? !analyzer.equals(that.analyzer) : that.analyzer != null) return false;
        if (callback != null ? !callback.equals(that.callback) : that.callback != null) return false;
        if (debug != null ? !debug.equals(that.debug) : that.debug != null) return false;
        if (defaultOperator != that.defaultOperator) return false;
        if (forceJSON != null ? !forceJSON.equals(that.forceJSON) : that.forceJSON != null) return false;
        if (includeDocs != null ? !includeDocs.equals(that.includeDocs) : that.includeDocs != null) return false;
        if (limit != null ? !limit.equals(that.limit) : that.limit != null) return false;
        if (query != null ? !query.equals(that.query) : that.query != null) return false;
        if (skip != null ? !skip.equals(that.skip) : that.skip != null) return false;
        if (sort != null ? !sort.equals(that.sort) : that.sort != null) return false;
        if (staleOk != null ? !staleOk.equals(that.staleOk) : that.staleOk != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = analyzer != null ? analyzer.hashCode() : 0;
        result = 31 * result + (callback != null ? callback.hashCode() : 0);
        result = 31 * result + (debug != null ? debug.hashCode() : 0);
        result = 31 * result + (defaultOperator != null ? defaultOperator.hashCode() : 0);
        result = 31 * result + (forceJSON != null ? forceJSON.hashCode() : 0);
        result = 31 * result + (includeDocs != null ? includeDocs.hashCode() : 0);
        result = 31 * result + (limit != null ? limit.hashCode() : 0);
        result = 31 * result + (query != null ? query.hashCode() : 0);
        result = 31 * result + (skip != null ? skip.hashCode() : 0);
        result = 31 * result + (sort != null ? sort.hashCode() : 0);
        result = 31 * result + (staleOk != null ? staleOk.hashCode() : 0);
        return result;
    }
}
