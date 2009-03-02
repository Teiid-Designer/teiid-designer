/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.metamatrix.internal.core.index.WordEntry;
import com.metamatrix.modeler.core.index.IndexConstants;

/**
 * TestWordEntryComparator
 */
public class TestWordEntryComparator extends TestCase {
    
    private static char DELIMITER = IndexConstants.RECORD_STRING.RECORD_DELIMITER;
    
    public static String ENTRY1 = "C"+DELIMITER+"SampleRelational/SampleRelationalSchema/MyFirstTable/A"+DELIMITER+" "+DELIMITER+"mmuuid:c67e8dc1-ee4f-1ec5-8100-ad5512432c75"+DELIMITER+"11001100|1|0|0|0|0| | | |VARCHAR| |\\MyPrj\\SampleRelational.mmm|"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
    public static String ENTRY2 = "C"+DELIMITER+"SampleRelational/SampleRelationalSchema/MyFirstTable/B"+DELIMITER+" "+DELIMITER+"mmuuid:c67e8dc1-ee4f-1ec5-8100-ad5512432c75"+DELIMITER+"11001100|1|0|0|0|0| | | |VARCHAR| |\\MyPrj\\SampleRelational.mmm|"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
    public static String ENTRY3 = "C"+DELIMITER+"SampleRelational/SampleRelationalSchema/MySecondTable/A"+DELIMITER+" "+DELIMITER+"mmuuid:c67e8dc1-ee4f-1ec5-8100-ad5512432c75"+DELIMITER+"11001100|1|0|0|0|0| | | |VARCHAR| |\\MyPrj\\SampleRelational.mmm|"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
    public static String ENTRY4 = "C"+DELIMITER+"SampleRelational/SampleRelationalSchema/MySecondTable/B"+DELIMITER+" "+DELIMITER+"mmuuid:c67e8dc1-ee4f-1ec5-8100-ad5512432c75"+DELIMITER+"11001100|1|0|0|0|0| | | |VARCHAR| |\\MyPrj\\SampleRelational.mmm|"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
    public static String ENTRY5 = "C"+DELIMITER+"SampleRelational/SampleRelationalSchema/MyThirdTable/A"+DELIMITER+" "+DELIMITER+"mmuuid:c67e8dc1-ee4f-1ec5-8100-ad5512432c75"+DELIMITER+"11001100|1|0|0|0|0| | | |VARCHAR| |\\MyPrj\\SampleRelational.mmm|"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
    
    // -------------------------------------------------
    // Variables initialized during one-time startup ...
    // -------------------------------------------------
    
    // ---------------------------------------
    // Variables initialized for each test ...
    // ---------------------------------------
    
    List wordEntries = new ArrayList();
    
    // =========================================================================
    //                        F R A M E W O R K
    // =========================================================================
    
    /**
     * Constructor for TestCreateIndexFile.
     * @param name
     */
    public TestWordEntryComparator(String name) {
        super(name);
    }
    
    // =========================================================================
    //                        T E S T   C O N T R O L
    // =========================================================================
    
    /** 
     * Construct the test suite, which uses a one-time setup call
     * and a one-time tear-down call.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("TestWordEntryComparator"); //$NON-NLS-1$
        suite.addTestSuite(TestWordEntryComparator.class);
    
        return new TestSetup(suite) { // junit.extensions package
            // One-time setup and teardown
            @Override
            public void setUp() throws Exception {
                oneTimeSetUp();
            }
            @Override
            public void tearDown() {
                oneTimeTearDown();
            }
        };
    }
    
    // =========================================================================
    //                                 M A I N
    // =========================================================================
    
    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
        System.exit(0);
    }
    
    // =========================================================================
    //                 S E T   U P   A N D   T E A R   D O W N
    // =========================================================================
    
    @Override
    protected void setUp() throws Exception {

    }
    
    @Override
    protected void tearDown() throws Exception {
        if(wordEntries != null) {
            wordEntries.clear();
        }
    }
    
    public static void oneTimeSetUp() {}
    
    public static void oneTimeTearDown() {}
    
    // =========================================================================
    //                      H E L P E R   M E T H O D S
    // =========================================================================
    
    /*
     * Create a WordEntry for the specified string
     */
    public WordEntry helpCreateWordEntry(final String word) {
        assertNotNull(word);
        final WordEntry entry = new WordEntry(word.toCharArray());
        assertNotNull(entry);
        return entry;
    }
    
    /*
     * Create a WordEntry for every line in the specified file
     */
    public WordEntry[] helpCreateWordEntries(final File indexFile) {
        assertNotNull(indexFile);
        assertTrue(indexFile.exists());
        List entries = new ArrayList();
        try {
            FileReader fr = new FileReader(indexFile);
            BufferedReader br = new BufferedReader(fr);
            
            System.out.println("Reading "+indexFile); //$NON-NLS-1$
            String record = null;
            while ((record = br.readLine()) != null) {
                String trimmedRecord = record.trim();
                WordEntry entry = helpCreateWordEntry(trimmedRecord);
//                System.out.println("   Creating WordEntry "+entry); //$NON-NLS-1$
                entries.add(entry);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return (WordEntry[]) entries.toArray(new WordEntry[entries.size()]);
    }

    public void testComparator1() {
        wordEntries.add(helpCreateWordEntry(ENTRY1));
        wordEntries.add(helpCreateWordEntry(ENTRY2));
        wordEntries.add(helpCreateWordEntry(ENTRY3));
        wordEntries.add(helpCreateWordEntry(ENTRY4));
        wordEntries.add(helpCreateWordEntry(ENTRY5));

        Collections.shuffle(wordEntries);
        
        WordEntryComparator comparator = new WordEntryComparator();
        Collections.sort(wordEntries, comparator);
        
        WordEntry entry1 = (WordEntry) wordEntries.get(0);
        WordEntry entry2 = (WordEntry) wordEntries.get(1);
        WordEntry entry3 = (WordEntry) wordEntries.get(2);
        WordEntry entry4 = (WordEntry) wordEntries.get(3);
        WordEntry entry5 = (WordEntry) wordEntries.get(4);
        
        assertEquals(ENTRY1, String.valueOf(entry1.getWord())); 
        assertEquals(ENTRY2, String.valueOf(entry2.getWord()));
        assertEquals(ENTRY3, String.valueOf(entry3.getWord()));
        assertEquals(ENTRY4, String.valueOf(entry4.getWord()));
        assertEquals(ENTRY5, String.valueOf(entry5.getWord()));
    }
    
    public void testComparator2() {
        wordEntries.add(helpCreateWordEntry(ENTRY1));
        wordEntries.add(helpCreateWordEntry(ENTRY1));
        wordEntries.add(helpCreateWordEntry(ENTRY2));
        wordEntries.add(helpCreateWordEntry(ENTRY3));
        wordEntries.add(helpCreateWordEntry(ENTRY3));

        Collections.shuffle(wordEntries);
        
        WordEntryComparator comparator = new WordEntryComparator();
        Collections.sort(wordEntries, comparator);
        
        WordEntry entry1 = (WordEntry) wordEntries.get(0);
        WordEntry entry2 = (WordEntry) wordEntries.get(1);
        WordEntry entry3 = (WordEntry) wordEntries.get(2);
        WordEntry entry4 = (WordEntry) wordEntries.get(3);
        WordEntry entry5 = (WordEntry) wordEntries.get(4);
        
        assertEquals(ENTRY1, String.valueOf(entry1.getWord())); 
        assertEquals(ENTRY1, String.valueOf(entry2.getWord()));
        assertEquals(ENTRY2, String.valueOf(entry3.getWord()));
        assertEquals(ENTRY3, String.valueOf(entry4.getWord()));
        assertEquals(ENTRY3, String.valueOf(entry5.getWord()));
    }    

}
