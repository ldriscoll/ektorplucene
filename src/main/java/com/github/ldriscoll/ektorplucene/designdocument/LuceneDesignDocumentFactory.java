package com.github.ldriscoll.ektorplucene.designdocument;

import java.util.HashMap;
import java.util.Map;

import org.ektorp.support.DesignDocument;
import org.ektorp.support.StdDesignDocumentFactory;
import org.ektorp.util.Predicate;
import org.ektorp.util.ReflectionUtils;

import com.github.ldriscoll.ektorplucene.designdocument.annotation.FullText;
import com.github.ldriscoll.ektorplucene.designdocument.annotation.Index;


public class LuceneDesignDocumentFactory extends StdDesignDocumentFactory {

    @Override
    public LuceneDesignDocument generateFrom(Object metaDataSource) {
        LuceneDesignDocument dd = (LuceneDesignDocument) super.generateFrom(metaDataSource);
        Class<?> metaDataClass = metaDataSource.getClass();

        Map<String, LuceneIndex> indexes = createIndexes(metaDataClass);
        dd.setFulltext(indexes);

        return dd;
    }

    private Map<String, LuceneIndex> createIndexes(Class<?> metaDataClass) {
        final Map<String, LuceneIndex> indexes = new HashMap<String, LuceneIndex>();

        ReflectionUtils.eachAnnotation(metaDataClass, FullText.class, new Predicate<FullText>() {

            public boolean apply(FullText input) {
                for (Index idx : input.value()) {
                    indexes.put(idx.name(), LuceneIndex.fromAnnotation(idx));
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
