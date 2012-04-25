package com.github.ldriscoll.ektorplucene.designdocument;

import java.util.HashMap;
import java.util.Map;

import org.ektorp.support.DesignDocument;
import org.ektorp.support.StdDesignDocumentFactory;
import org.ektorp.util.Predicate;
import org.ektorp.util.ReflectionUtils;


public class LuceneDesignDocumentFactory extends StdDesignDocumentFactory {
	
	@Override
	public LuceneDesignDocument generateFrom(Object metaDataSource) {
		LuceneDesignDocument dd = (LuceneDesignDocument) super.generateFrom(metaDataSource);
		Class<?> metaDataClass = metaDataSource.getClass();
		
		Map<String, LuceneDesignDocument.Index> indexes = createIndexes(metaDataClass);
        dd.setFulltext(indexes);
		
		return dd;
	}
	
	private Map<String, LuceneDesignDocument.Index> createIndexes(Class<?> metaDataClass) {
        final Map<String, LuceneDesignDocument.Index> indexes = new HashMap<String, LuceneDesignDocument.Index>();

        ReflectionUtils.eachAnnotation(metaDataClass, FullText.class, new Predicate<FullText>() {
        	
            public boolean apply(FullText input) {
            	for (Index idx : input.value()) {
            		indexes.put(idx.name(), LuceneDesignDocument.Index.of(idx));
            	}
                return true;
            }
        });
		
		return indexes;
	}

	@Override
	protected DesignDocument newDesignDocumentInstance() {
		return new LuceneDesignDocument();
	}
	
}
