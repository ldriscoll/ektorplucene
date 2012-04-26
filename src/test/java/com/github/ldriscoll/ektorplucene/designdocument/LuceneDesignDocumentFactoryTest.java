package com.github.ldriscoll.ektorplucene.designdocument;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.github.ldriscoll.ektorplucene.designdocument.annotation.FullText;
import com.github.ldriscoll.ektorplucene.designdocument.annotation.Index;


public class LuceneDesignDocumentFactoryTest {

    private LuceneDesignDocumentFactory factory;

    @Before
    public void setup() {
        factory = new LuceneDesignDocumentFactory();
    }

    @Test
    public void testFullTextAnnotationIsVisited() {
        LuceneDesignDocument doc = factory.generateFrom(new ExampleRepoClass());

        Map<String, LuceneIndex> indexes = doc.getFulltext();
        assertEquals(3, indexes.size());

        Set<String> names = indexes.keySet();
        assertTrue(names.contains("foo"));
        assertTrue(names.contains("harry"));
        assertTrue(names.contains("beer"));
    }

    @FullText({
            @Index(name = "foo", index = "bar"),
            @Index(name = "harry", index = "carry"),
            @Index(name = "beer", index = "is good")
    })
    private class ExampleRepoClass {
    }

}
