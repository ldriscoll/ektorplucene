package com.github.ldriscoll;

import org.ektorp.support.DesignDocument;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

import com.github.ldriscoll.ektorplucene.CouchDbRepositorySupportWithLucene;
import com.github.ldriscoll.ektorplucene.LuceneAwareCouchDbConnector;
import com.github.ldriscoll.ektorplucene.designdocument.LuceneDesignDocument;
import com.github.ldriscoll.ektorplucene.designdocument.LuceneDesignDocumentFactory;


public class CouchDbRepositorySupportWithLuceneTest {

	private Mockery context;
	private CouchDbRepositorySupportWithLucene<Example> dbSupport;
	
	private LuceneAwareCouchDbConnector db;
	private LuceneDesignDocumentFactory factory;
	
	@Before
	public void setup() {
		context = new Mockery() {{
			setImposteriser(ClassImposteriser.INSTANCE);
		}};
		
		db = context.mock(LuceneAwareCouchDbConnector.class);
		factory = context.mock(LuceneDesignDocumentFactory.class);
		
		dbSupport = new ExampleRepository(db);
		dbSupport.setDesignDocumentFactory(factory);
	}
	
	@Test
	public void testNewDesignDocumentGenerated() {
		final DesignDocument designDoc = context.mock(LuceneDesignDocument.class);
		
		context.checking(new Expectations() {{
			//it will ask if we have the design doc, and we will say no
			oneOf(db).contains("_design/Example");
			will(returnValue(false));
			
			//pretend to generate a design document from the support class
			oneOf(factory).generateFrom(dbSupport);
			will(returnValue(designDoc));
			
			//the lucene design doc will be updated
			oneOf(db).update(designDoc);
		}});
		
		dbSupport.initStandardDesignDocument();
	}
	
	@Test
	public void testWhenDesignDocumentExistsTheGeneratedOneIsMerged() {
		final LuceneDesignDocument existingDoc = context.mock(LuceneDesignDocument.class, "existing");
		final DesignDocument generatedDoc = context.mock(LuceneDesignDocument.class, "generated");
		
		context.checking(new Expectations() {{
			//it will ask if we have the design doc, and we will say yes
			oneOf(db).contains("_design/Example");
			will(returnValue(true));
			
			//return the mocked lucene design document
			oneOf(db).get(LuceneDesignDocument.class, "_design/Example");
			will(returnValue(existingDoc));
			
			//pretend to generate a design document from the support class
			oneOf(factory).generateFrom(dbSupport);
			will(returnValue(generatedDoc));
			
			//the lucene document will be asked to merge with the generated one
			oneOf(existingDoc).mergeWith(generatedDoc);
			will(returnValue(true));
			
			//the lucene design doc will be updated
			oneOf(db).update(existingDoc);
		}});
		
		dbSupport.initStandardDesignDocument();
	}

	
	
	private class Example {}
	
	private class ExampleRepository extends CouchDbRepositorySupportWithLucene<Example> {
		
		public ExampleRepository(LuceneAwareCouchDbConnector db) {
			super(Example.class, db, false);
		}
	}
	
}
