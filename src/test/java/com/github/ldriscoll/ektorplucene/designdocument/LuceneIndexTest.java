package com.github.ldriscoll.ektorplucene.designdocument;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import com.github.ldriscoll.ektorplucene.designdocument.annotation.Defaults;
import com.github.ldriscoll.ektorplucene.designdocument.annotation.FullText;
import com.github.ldriscoll.ektorplucene.designdocument.annotation.Index;


public class LuceneIndexTest {

    private Map<String, LuceneIndex> parseIndexesFromClass(Class<?> cls) {
        Map<String, LuceneIndex> indexes = new HashMap<String, LuceneIndex>();
        FullText ft = cls.getAnnotation(FullText.class);
        for (Index idx : ft.value()) {
            indexes.put(idx.name(), LuceneIndex.fromAnnotation(idx));
        }
        return indexes;
    }


    @FullText({
            @Index(name = "Simple", index = "Index")
    })
    private class SimpleAnnotationExample {
    }

    @Test
    public void testAnnotationToObjectFunctionSimple() {
        Map<String, LuceneIndex> indexes = parseIndexesFromClass(SimpleAnnotationExample.class);
        assertEquals(1, indexes.size());
        Entry<String, LuceneIndex> idx = indexes.entrySet().iterator().next();
        assertEquals("Simple", idx.getKey());
        assertEquals("Index", idx.getValue().getIndex());
        assertNull(idx.getValue().getDefaults());
        assertNull(idx.getValue().getAnalyzer());
    }


    @FullText({
            @Index(name = "Annotation", index = "With Defaults",
                    defaults = @Defaults(field = "thefield", type = "double", store = "yes", index = "not_analyzed"))
    })
    private class DefaultsAnnotationExample {
    }

    @Test
    public void testAnnotationWithDefaults() {
        Map<String, LuceneIndex> indexes = parseIndexesFromClass(DefaultsAnnotationExample.class);
        assertEquals(1, indexes.size());
        Entry<String, LuceneIndex> idx = indexes.entrySet().iterator().next();
        assertEquals("Annotation", idx.getKey());
        assertEquals("With Defaults", idx.getValue().getIndex());
        assertNull(idx.getValue().getAnalyzer());

        LuceneDefaults defaults = idx.getValue().getDefaults();
        assertNotNull(defaults);
        assertEquals("thefield", defaults.getField());
        assertEquals("double", defaults.getType());
        assertEquals("yes", defaults.getStore());
        assertEquals("not_analyzed", defaults.getIndex());
    }


    @FullText({
            @Index(name = "Name1", index = "Index1"),
            @Index(name = "Name2", index = "Index2", analyzer = "chinese"),
            @Index(name = "Name3", index = "Index3",
                    defaults = @Defaults(store = "no", index = "not_analyzed_no_norms"))
    })
    private class MultipleIndexesExample {
    }


    @Test
    public void testMultipleIndexAnnotations() {
        Map<String, LuceneIndex> indexes = parseIndexesFromClass(MultipleIndexesExample.class);
        assertEquals(3, indexes.size());

        for (Entry<String, LuceneIndex> entry : indexes.entrySet()) {
            String name = entry.getKey();
            LuceneIndex idx = entry.getValue();

            if ("Name1".equals(name)) {
                assertEquals("Index1", idx.getIndex());
                assertNull(idx.getAnalyzer());
                assertNull(idx.getDefaults());
            }
            if ("Name2".equals(name)) {
                assertEquals("Index2", idx.getIndex());
                assertEquals("chinese", idx.getAnalyzer());
                assertNull(idx.getDefaults());
            }
            if ("Name3".equals(name)) {
                assertEquals("Index3", idx.getIndex());
                assertNull(idx.getAnalyzer());

                LuceneDefaults defaults = idx.getDefaults();
                assertNotNull(defaults);
                assertNull(defaults.getField());
                assertNull(defaults.getType());
                assertEquals("no", defaults.getStore());
                assertEquals("not_analyzed_no_norms", defaults.getIndex());
            }
        }
    }

    @Test
    public void testJsonSerializer() throws Exception {
        LuceneDefaults defaults = new LuceneDefaults();
        defaults.setStore("yes");
        defaults.setIndex("not_analyzed");

        LuceneIndex index = new LuceneIndex();
        index.setIndex("function() { return new Document(); }");
        index.setAnalyzer("en");
        index.setDefaults(defaults);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(index);
        LuceneIndex convertedIndex = mapper.readValue(json, LuceneIndex.class);

        assertEquals(index, convertedIndex);
    }

}
