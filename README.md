<h1>Background</h1>
I created this project to enable the use of <a href="http://github.com/rnewson/couchdb-lucene">couchdb-lucene</a> with <a href="http://www.ektorp.org">ektorp</a> for couchdb.
The Ektorp project does not directly support couchdb-lucene as it is not core to couchdb lucene.

<h1>Minimum System Requirement</h1>
Java 1.5 (or Above) is required.  Personnaly I use Sun Java 6

<h1>Issue Tracking</h1>

Issue tracking at <a href="http://github.com/ldriscoll/ektorplucene/issues">github</a>.

<h1>Overview/Usage</h1>
This is a really basic package.  Please Read <a href="http://www.ektorp.org/reference_documentation.html#d98e237">Ektorp Minimal Configuration</a> first.
LuceneAwareCouchDbConnector extends, and basically replaces, StdCouchDbConnector in that case:
<pre>
    HttpClient httpClient = new StdHttpClient.Builder().build()
    CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
    LuceneAwareCouchDbConnector db = new LuceneAwareCouchDbConnector("my_first_database", dbInstance);
    db.createDatabaseIfNotExists();
</pre>

LuceneQuery is then a tool that builds the couchdb-lucene query, anything in that pojo should match the arguments for couchdb-lucene.

Using a LuceneQuery one can then call luceneAwareCouchDbConnector.queryLucene.  This returns a LuceneResult, that should match the response from couchdb-lucene.
