package com.github.ldriscoll.ektorplucene;

import org.apache.commons.lang.StringUtils;
import org.ektorp.http.URI;

/**
 * Created by IntelliJ IDEA.
 * Modeled off org.ektorp.ViewQuery
 * User: ldriscoll
 * Date: 12/9/10
 * Time: 2:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class LuceneQuery {

    public static enum Operator {OR, AND}

    private String analyzer;
    private String callback;
    private Boolean debug;
    private Operator default_operator;
    private Boolean force_json;
    private Boolean include_docs;
    private Integer limit;
    private String q;
    private Integer skip;
    private String sort;
    private Boolean staleOk;


    private String cachedQuery;
    private String dbPath;
    private final String lucenePrefix;
    private final String designDocument;
    private final String indexFunction;


    public LuceneQuery(String lucenePrefix, String designDocument, String indexFunction) {
        this.lucenePrefix = lucenePrefix;
        this.designDocument = designDocument;
        this.indexFunction = indexFunction;
    }

    /** Override the default analyzer used to parse the q parameter
     *
     * @param analyzer
     */
    public void setAnalyzer(String analyzer) {
        this.analyzer = analyzer;
    }

    /**
     * Specify a JSONP callback wrapper. The full JSON result will be prepended with
     * this parameter and also placed with parentheses."
     * @param callback
     */
    public void setCallback(String callback) {
        this.callback = callback;
    }

    /**
     * Setting this to true disables response caching (the query is executed every time)
     * and indents the JSON response for readability.
     * @param debug
     */
    public void setDebug(Boolean debug) {
        this.debug = debug;
    }

    /**
     * Change the default operator for boolean queries. Defaults to "OR",
     * other permitted value is "AND".
     * @param default_operator
     */
    public void setDefault_operator(Operator default_operator) {
        this.default_operator = default_operator;
    }

    /**
     * Usually couchdb-lucene determines the Content-Type of its response based on the
     * presence of the Accept header. If Accept contains "application/json", you get
     * "application/json" in the response, otherwise you get "text/plain;charset=utf8".
     * Some tools, like JSONView for FireFox, do not send the Accept header but do render
     * "application/json" responses if received. Setting force_json=true forces all response
     * to "application/json" regardless of the Accept header.
     * @param force_json
     */
    public void setForce_json(Boolean force_json) {
        this.force_json = force_json;
    }

    /**
     * whether to include the source docs
     * @param include_docs
     */
    public void setInclude_docs(Boolean include_docs) {
        this.include_docs = include_docs;
    }

    /**
     * the maximum number of results to return
     * @param limit
     */
    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    /**
     * the query to run (e.g, subject:hello). If not specified, the default
     * field is searched. Multiple queries can be supplied, separated by commas;
     * the resulting JSON will be an array of responses.
     * @param q
     */
    public void setQ(String q) {
        this.q = q;
    }

    /**
     * the number of results to skip
     * @param skip
     */
    public void setSkip(Integer skip) {
        this.skip = skip;
    }

    /**
     * the comma-separated fields to sort on. Prefix with / for ascending order
     *  and \ for descending order (ascending is the default if not specified).
     * Type-specific sorting is also available by appending the type between angle
     * brackets (e.g, sort=amount<float>). Supported types are 'float', 'double',
     * 'int', 'long' and 'date'.
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
     * @param staleOk
     */
    public void setStaleOk(Boolean staleOk) {
        this.staleOk = staleOk;
    }


    public void setDbPath(String dbPath) {
        this.dbPath = dbPath;
    }

    private boolean hasValue(Object o) {
        if (o instanceof String) {
            return !StringUtils.isEmpty((String)o);
        }
        return o != null;
    }

    private URI buildQueryPath() {
        URI uri = URI.of(dbPath);
        uri.append(lucenePrefix);
        uri.append(designDocument);
        uri.append(indexFunction);
        return uri;
    }

    private void addParam(URI query, String paramName, Object value) {
        if (hasValue(value)) {
            query.param(paramName, value.toString());
        }
    }

    public String buildQuery() {
        URI query = buildQueryPath();

        if (cachedQuery != null) {
            return cachedQuery;
        }

        addParam(query, "analyzer", analyzer);
        addParam(query, "callback", callback);
        addParam(query, "debug", debug);
        addParam(query, "default_operator", default_operator);
        addParam(query, "force_json", force_json);
        addParam(query, "include_docs", include_docs);
        addParam(query, "limit", limit);
        addParam(query, "q", q);
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
        if (default_operator != that.default_operator) return false;
        if (force_json != null ? !force_json.equals(that.force_json) : that.force_json != null) return false;
        if (include_docs != null ? !include_docs.equals(that.include_docs) : that.include_docs != null) return false;
        if (limit != null ? !limit.equals(that.limit) : that.limit != null) return false;
        if (q != null ? !q.equals(that.q) : that.q != null) return false;
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
        result = 31 * result + (default_operator != null ? default_operator.hashCode() : 0);
        result = 31 * result + (force_json != null ? force_json.hashCode() : 0);
        result = 31 * result + (include_docs != null ? include_docs.hashCode() : 0);
        result = 31 * result + (limit != null ? limit.hashCode() : 0);
        result = 31 * result + (q != null ? q.hashCode() : 0);
        result = 31 * result + (skip != null ? skip.hashCode() : 0);
        result = 31 * result + (sort != null ? sort.hashCode() : 0);
        result = 31 * result + (staleOk != null ? staleOk.hashCode() : 0);
        return result;
    }
}
