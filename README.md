# Background #
I created this project to enable the use of [couchdb-lucene](http://github.com/rnewson/couchdb-lucene) with [ektorp](http://www.ektorp.org) for couchdb.
The Ektorp project does not directly support couchdb-lucene as it is not core to couchdb lucene.

# Minimum System Requirement #
Java 1.5 (or Above) is required.  Personnaly I use Sun Java 6

# Issue Tracking #

Issue tracking at [github](http://github.com/ldriscoll/ektorplucene/issues).

# Overview/Usage #
This is a really basic package.  Please Read [Ektorp Minimal Configuration](http://www.ektorp.org/reference_documentation.html#d98e237) first.
LuceneAwareCouchDbConnector extends, and basically replaces, StdCouchDbConnector in that case:

    HttpClient httpClient = new StdHttpClient.Builder().build()
    CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
    LuceneAwareCouchDbConnector db = new LuceneAwareCouchDbConnector("my_first_database", dbInstance);
    db.createDatabaseIfNotExists();

LuceneQuery is then a tool that builds the couchdb-lucene query, anything in that pojo should match the arguments for couchdb-lucene.

Using a LuceneQuery one can then call luceneAwareCouchDbConnector.queryLucene.  This returns a LuceneResult, that should match the response from couchdb-lucene.

# Maven #
EktorpLucene is now available in the maven repositories.  Please at this to your dependencies:
    <dependency>
      <groupId>com.github.ldriscoll</groupId>
      <artifactId>ektorplucene</artifactId>
      <version>0.1.1</version>
    </dependency>
