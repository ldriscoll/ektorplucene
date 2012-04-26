package com.github.ldriscoll.ektorplucene.designdocument;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.ektorp.support.DesignDocument;
import org.junit.AfterClass;
import org.junit.Test;


public class LuceneDesignDocumentTest {

    @AfterClass
    public static void unsetEnvVar() {
        System.clearProperty(DesignDocument.UPDATE_ON_DIFF);
    }

    @Test
    public void testMergeWithUpdate() {
        System.setProperty(DesignDocument.UPDATE_ON_DIFF, "false");
        LuceneDesignDocument doc = performMerge();
        assertEquals(3, doc.getFulltext().size());
        assertNull("This index should have been removed on merge", doc.getFulltext().get("remove_index"));
        assertNotNull("Third index should have been merged", doc.getFulltext().get("third_index"));
        assertNull("Second index should NOT have been updated!", doc.getFulltext().get("second_index").getAnalyzer());
    }

    @Test
    public void testMergeWithoutUpdate() {
        System.setProperty(DesignDocument.UPDATE_ON_DIFF, "true");
        LuceneDesignDocument doc = performMerge();
        assertEquals(3, doc.getFulltext().size());
        assertNull("This index should have been removed on merge", doc.getFulltext().get("remove_index"));
        assertEquals("Second index should have been updated!", "en", doc.getFulltext().get("second_index").getAnalyzer());
    }

    public LuceneDesignDocument performMerge() {
        //create design document that is read from the database
        Map<String, LuceneIndex> existingIndexes = new HashMap<String, LuceneIndex>();
        LuceneIndex firstIndex = new LuceneIndex();
        firstIndex.setIndex("myfirstfunction");
        existingIndexes.put("first_index", firstIndex);

        LuceneIndex secondIndex = new LuceneIndex();
        secondIndex.setIndex("mysecondfunction");
        existingIndexes.put("second_index", secondIndex);

        LuceneIndex removeIndex = new LuceneIndex();
        removeIndex.setIndex("this will be deleted");
        existingIndexes.put("remove_index", removeIndex);

        LuceneDesignDocument existing = new LuceneDesignDocument();
        existing.setFulltext(existingIndexes);

        //create design document that is parsed from the annotations
        Map<String, LuceneIndex> currentIndexes = new HashMap<String, LuceneIndex>();
        LuceneIndex sameFirstIndex = new LuceneIndex();
        sameFirstIndex.setIndex("myfirstfunction");
        currentIndexes.put("first_index", sameFirstIndex);

        LuceneIndex updatedSecondIndex = new LuceneIndex();
        updatedSecondIndex.setIndex("mysecondfunction");
        updatedSecondIndex.setAnalyzer("en");
        currentIndexes.put("second_index", updatedSecondIndex);

        LuceneIndex newThirdIndex = new LuceneIndex();
        newThirdIndex.setIndex("mythirdfunction");
        currentIndexes.put("third_index", newThirdIndex);

        LuceneDesignDocument current = new LuceneDesignDocument();
        current.setFulltext(currentIndexes);

        //merge current design with existing design
        boolean changed = existing.mergeWith(current);
        assertTrue(changed);
        return existing;
    }

}
