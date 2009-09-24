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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import com.metamatrix.core.index.IDocument;
import com.metamatrix.core.index.IEntryResult;
import com.metamatrix.core.index.IIndexer;
import com.metamatrix.core.index.IIndexerOutput;
import com.metamatrix.core.index.IQueryResult;
import com.metamatrix.core.util.SmartTestSuite;
import com.metamatrix.core.util.Stopwatch;
import com.metamatrix.internal.core.index.BlocksIndexInput;
import com.metamatrix.internal.core.index.FileDocument;
import com.metamatrix.internal.core.index.Index;
import com.metamatrix.internal.core.index.WordEntry;

/**
 * TestCreateIndexFile
 */
public class TestCreateIndexFile extends TestCase {

    private static final String TEST_INDEX_FILE_NAME = "test.index"; //$NON-NLS-1$
    private static final String TEST_MODEL_FILE_PATH = SmartTestSuite.getTestDataPath() + File.separator + "builtInDataTypes.xml"; //$NON-NLS-1$
    private static final String TEST_INDEX_FILE_PATH = SmartTestSuite.getTestDataPath() + File.separator + TEST_INDEX_FILE_NAME;

    private static final String TEST_LARGER_INDEX_FILE_NAME = "larger_test.index"; //$NON-NLS-1$
    private static final String TEST_LARGER_MODEL_FILE_PATH = SmartTestSuite.getTestDataPath() + File.separator
                                                              + "XAL_XML_TEST.xmi"; //$NON-NLS-1$
    private static final String TEST_LARGER_INDEX_FILE_PATH = SmartTestSuite.getTestDataPath() + File.separator
                                                              + TEST_LARGER_INDEX_FILE_NAME;

    /**
     * Constructor for TestCreateIndexFile.
     * 
     * @param name
     */
    public TestCreateIndexFile( String name ) {
        super(name);
    }

    /**
     * Construct the test suite, which uses a one-time setup call and a one-time tear-down call.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("TestCreateIndexFile"); //$NON-NLS-1$
        suite.addTestSuite(TestCreateIndexFile.class);

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

    public static void main( String args[] ) {
        junit.textui.TestRunner.run(suite());
        System.exit(0);
    }

    @Override
    protected void setUp() throws Exception {
    }

    @Override
    protected void tearDown() throws Exception {
        // Have to clean up for other test cases ...
        final File testIndexFile = new File(TEST_INDEX_FILE_PATH);
        testIndexFile.delete();
    }

    public static void oneTimeSetUp() {
    }

    public static void oneTimeTearDown() {
    }

    /*
     * Create a WordEntry for the specified string
     */
    public WordEntry helpCreateWordEntry( final String word ) {
        assertNotNull(word);
        final WordEntry entry = new WordEntry(word.toCharArray());
        assertNotNull(entry);
        return entry;
    }

    public WordEntry helpCreateWordEntry( final String word,
                                          int fileNum ) {
        final WordEntry entry = helpCreateWordEntry(word);
        entry.addRef(fileNum);
        return entry;
    }

    /*
     * Create a WordEntry for every line in the specified file
     */
    public WordEntry[] helpCreateWordEntries( final File f ) {
        assertNotNull(f);
        assertTrue(f.exists());
        List entries = new ArrayList();
        try {
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);

            System.out.println("Reading " + f); //$NON-NLS-1$
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
        return (WordEntry[])entries.toArray(new WordEntry[entries.size()]);
    }

    public WordEntry[] helpCreateWordEntries( final File f,
                                              int fileNum ) {
        WordEntry[] entries = helpCreateWordEntries(f);
        for (int i = 0; i < entries.length; i++) {
            entries[i].addRef(fileNum);
        }
        return entries;
    }

    public void printQueryResults( IQueryResult[] results ) {
        System.err.println("\nIQueryResult[].length = " + (results == null ? "null" : Integer.toString(results.length))); //$NON-NLS-1$ //$NON-NLS-2$
        if (results != null) {
            for (int i = 0; i < results.length; i++) {
                System.err.println("  IQueryResult[] = " + results[i]); //$NON-NLS-1$
            }
        }
    }

    public void printQueryResults( IEntryResult[] results ) {
        System.err.println("\nIEntryResult[].length = " + (results == null ? "null" : Integer.toString(results.length))); //$NON-NLS-1$ //$NON-NLS-2$
        if (results != null) {
            for (int i = 0; i < results.length; i++) {
                System.err.println("  IEntryResult[] = " + results[i]); //$NON-NLS-1$
            }
        }
    }

    public void testQueryPerformance_0() {
        System.out.println("\n============================ START ===================================)"); //$NON-NLS-1$
        System.out.println("    ---->> TestCreateIndexFile.testQueryPerformance_0(builtInDatatypes.xml)"); //$NON-NLS-1$
        int nQueries = 5000;
        System.out.println(TEST_INDEX_FILE_PATH);
        final File testModelFile = new File(TEST_MODEL_FILE_PATH);
        final File indexDirectory = new File(SmartTestSuite.getTestDataPath());

        final IDocument doc = new TestFileDocument(testModelFile);
        TestIndex index = null;
        try {
            index = new TestIndex(indexDirectory, TEST_INDEX_FILE_NAME, false);
            index.add(doc, new TestIndexer());
            index.save();
            assertEquals(1, index.getNumDocuments());

            // Find the IndexFile(s) that contain entries matching this prefix
            String word = "*mmuuid:88b13dc0-e702-1e20*"; //$NON-NLS-1$
            char[] charWord = word.toCharArray();

            // printQueryResults( index.queryEntriesMatching(charWord) );

            // Original Index/Query without improvements
            Stopwatch watch = new Stopwatch();
            watch.start();
            for (int i = 0; i < nQueries; i++) {
                index.queryEntriesMatching(charWord);
            }
            watch.stopPrintIncrementAndRestart("     ---->> TestCreateIndexFile.testQueryPerformance_0() \n" + //$NON-NLS-1$
                                               "    NO Indexes cached.  NQueries = " + nQueries + //$NON-NLS-1$
                                               " DTime = "); //$NON-NLS-1$

            // index.setDoCache(true);
            // printQueryResults( index.queryEntriesMatching(charWord) );
            // index.close();

            index.setDoCache(true);
            watch.start(true);
            for (int i = 0; i < nQueries; i++) {
                index.queryEntriesMatching(charWord);
            }
            index.close();
            watch.stopPrintIncrementAndRestart("    ---->> TestCreateIndexFile.testQueryPerformance_0() \n" + //$NON-NLS-1$
                                               "    INDEXES Cached.  NQueries = " + nQueries + //$NON-NLS-1$
                                               " DTime = "); //$NON-NLS-1$

        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(index);
        System.out.println("============================ END ===================================\n)"); //$NON-NLS-1$
    }

    public void testQueryPerformance_1() {
        System.out.println("\n============================ START ===================================)"); //$NON-NLS-1$
        Stopwatch watch = new Stopwatch();
        watch.start();
        System.out.println("    ---->> TestCreateIndexFile.testQueryPerformance_1(XAL_XML_TEST.xmi)"); //$NON-NLS-1$
        int nQueries = 1000;
        System.out.println(TEST_LARGER_INDEX_FILE_PATH);
        final File testModelFile = new File(TEST_LARGER_MODEL_FILE_PATH);
        final File indexDirectory = new File(SmartTestSuite.getTestDataPath());

        final IDocument doc = new TestFileDocument(testModelFile);
        TestIndex index = null;
        try {

            index = new TestIndex(indexDirectory, TEST_LARGER_INDEX_FILE_NAME, false);
            watch.stopPrintIncrementAndRestart("    ---->> TestCreateIndexFile.testQueryPerformance_1() \n" + //$NON-NLS-1$
                                               "    Created Index File" + //$NON-NLS-1$
                                               " DTime = "); //$NON-NLS-1$
            index.add(doc, new TestIndexer());
            watch.stopPrintIncrementAndRestart("    ---->> TestCreateIndexFile.testQueryPerformance_1() \n" + //$NON-NLS-1$
                                               "    Indexed the File  = " + testModelFile + //$NON-NLS-1$
                                               " DTime = "); //$NON-NLS-1$
            index.save();
            watch.stopPrintIncrementAndRestart("    ---->> TestCreateIndexFile.testQueryPerformance_1() \n" + //$NON-NLS-1$
                                               "    Saved the Index File" + //$NON-NLS-1$
                                               " DTime = "); //$NON-NLS-1$
            assertEquals(1, index.getNumDocuments());

            // Find the IndexFile(s) that contain entries matching this prefix
            String word = "*mmuuid:29336a49-fe97-102f*"; //$NON-NLS-1$
            char[] charWord = word.toCharArray();

            // printQueryResults( index.queryEntriesMatching(charWord) );

            // Original Index/Query without improvements

            for (int i = 0; i < nQueries; i++) {
                index.queryEntriesMatching(charWord);
            }
            watch.stopPrintIncrementAndRestart("    ---->> TestCreateIndexFile.testQueryPerformance_1() \n" + //$NON-NLS-1$
                                               "    NO Indexes cached.  NQueries = " + nQueries + //$NON-NLS-1$
                                               " DTime = "); //$NON-NLS-1$

            // index.setDoCache(true);
            // printQueryResults( index.queryEntriesMatching(charWord) );
            // index.close();

            index.setDoCache(true);
            watch.start(true);
            for (int i = 0; i < nQueries; i++) {
                index.queryEntriesMatching(charWord);
            }
            index.close();
            watch.stopPrintIncrementAndRestart("    ---->> TestCreateIndexFile.testQueryPerformance_1() \n" + //$NON-NLS-1$
                                               "    INDEXES Cached.  NQueries = " + nQueries + //$NON-NLS-1$
                                               " DTime = "); //$NON-NLS-1$

        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(index);
        System.out.println("============================ END ===================================\n)"); //$NON-NLS-1$
    }

    public void testQueryPerformance_2() {
        System.out.println("\n============================ START ===================================)"); //$NON-NLS-1$
        Stopwatch watch = new Stopwatch();
        watch.start();
        System.out.println("    ---->> TestCreateIndexFile.testQueryPerformance_2(XAL_XML_TEST.xmi)  NO EXPECTED MATCH"); //$NON-NLS-1$
        int nQueries = 1000;
        System.out.println(TEST_LARGER_INDEX_FILE_PATH);
        final File testModelFile = new File(TEST_LARGER_MODEL_FILE_PATH);
        final File indexDirectory = new File(SmartTestSuite.getTestDataPath());

        final IDocument doc = new TestFileDocument(testModelFile);
        TestIndex index = null;
        try {

            index = new TestIndex(indexDirectory, TEST_LARGER_INDEX_FILE_NAME, false);
            watch.stopPrintIncrementAndRestart("    ---->> TestCreateIndexFile.testQueryPerformance_1() \n" + //$NON-NLS-1$
                                               "    Created Index File" + //$NON-NLS-1$
                                               " DTime = "); //$NON-NLS-1$
            index.add(doc, new TestIndexer());
            watch.stopPrintIncrementAndRestart("    ---->> TestCreateIndexFile.testQueryPerformance_1() \n" + //$NON-NLS-1$
                                               "    Indexed the File  = " + testModelFile + //$NON-NLS-1$
                                               " DTime = "); //$NON-NLS-1$
            index.save();
            watch.stopPrintIncrementAndRestart("    ---->> TestCreateIndexFile.testQueryPerformance_1() \n" + //$NON-NLS-1$
                                               "    Saved the Index File" + //$NON-NLS-1$
                                               " DTime = "); //$NON-NLS-1$
            assertEquals(1, index.getNumDocuments());

            // Find the IndexFile(s) that contain entries matching this prefix
            String word = "*mmxxid:1234567890123456*"; //$NON-NLS-1$
            char[] charWord = word.toCharArray();

            // printQueryResults( index.queryEntriesMatching(charWord) );

            // Original Index/Query without improvements

            for (int i = 0; i < nQueries; i++) {
                index.queryEntriesMatching(charWord);
            }
            watch.stopPrintIncrementAndRestart("    ---->> TestCreateIndexFile.testQueryPerformance_1() \n" + //$NON-NLS-1$
                                               "    NO Indexes cached.  NQueries = " + nQueries + //$NON-NLS-1$
                                               " DTime = "); //$NON-NLS-1$

            // index.setDoCache(true);
            // printQueryResults( index.queryEntriesMatching(charWord) );
            // index.close();

            index.setDoCache(true);
            watch.start(true);
            for (int i = 0; i < nQueries; i++) {
                index.queryEntriesMatching(charWord);
            }
            index.close();
            watch.stopPrintIncrementAndRestart("    ---->> TestCreateIndexFile.testQueryPerformance_1() \n" + //$NON-NLS-1$
                                               "    INDEXES Cached.  NQueries = " + nQueries + //$NON-NLS-1$
                                               " DTime = "); //$NON-NLS-1$

        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(index);
        System.out.println("============================ END ===================================\n)"); //$NON-NLS-1$
    }

    public void testOpenCloseBlockIndexInput() {
        System.out.println("\n============================ START ===================================)"); //$NON-NLS-1$
        Stopwatch watch = new Stopwatch();
        watch.start();
        System.out.println("    ---->> TestCreateIndexFile.testOpenCloseBlockIndexInput(XAL_XML_TEST.xmi)"); //$NON-NLS-1$
        int nOpens = 1000;
        System.out.println(TEST_LARGER_INDEX_FILE_PATH);
        final File testModelFile = new File(TEST_LARGER_MODEL_FILE_PATH);
        final File indexDirectory = new File(SmartTestSuite.getTestDataPath());

        final IDocument doc = new TestFileDocument(testModelFile);
        TestIndex index = null;
        try {

            index = new TestIndex(indexDirectory, TEST_LARGER_INDEX_FILE_NAME, false);
            watch.stopPrintIncrementAndRestart("    ---->> TestCreateIndexFile.testOpenCloseBlockIndexInput() \n" + //$NON-NLS-1$
                                               "    Created Index File" + //$NON-NLS-1$
                                               " DTime = "); //$NON-NLS-1$
            index.add(doc, new TestIndexer());
            watch.stopPrintIncrementAndRestart("    ---->> TestCreateIndexFile.testOpenCloseBlockIndexInput() \n" + //$NON-NLS-1$
                                               "    Indexed the File  = " + testModelFile + //$NON-NLS-1$
                                               " DTime = "); //$NON-NLS-1$
            index.save();
            watch.stopPrintIncrementAndRestart("    ---->> TestCreateIndexFile.testOpenCloseBlockIndexInput() \n" + //$NON-NLS-1$
                                               "    Saved the Index File" + //$NON-NLS-1$
                                               " DTime = "); //$NON-NLS-1$
            assertEquals(1, index.getNumDocuments());

            BlocksIndexInput input = index.getBlocksIndexInput();
            input.close();

            for (int i = 0; i < nOpens; i++) {
                // Close it right away.
                try {
                    input.open();
                } finally {
                    input.close();
                }
            }
            watch.stopPrintIncrementAndRestart("    ---->> TestCreateIndexFile.testOpenCloseBlockIndexInput() \n" + //$NON-NLS-1$
                                               "    Opened/Closed Index file # Times = " + nOpens + //$NON-NLS-1$
                                               " DTime = "); //$NON-NLS-1$

            for (int i = 0; i < nOpens * 2; i++) {
                // Close it right away.
                try {
                    input.open();
                } finally {
                    input.close();
                }
            }
            watch.stopPrintIncrementAndRestart("    ---->> TestCreateIndexFile.testOpenCloseBlockIndexInput() \n" + //$NON-NLS-1$
                                               "    Opened/Closed Index file # Times = " + nOpens * 2 + //$NON-NLS-1$
                                               " DTime = "); //$NON-NLS-1$

            for (int i = 0; i < nOpens * 5; i++) {
                // Close it right away.
                try {
                    input.open();
                } finally {
                    input.close();
                }
            }
            watch.stopPrintIncrementAndRestart("    ---->> TestCreateIndexFile.testOpenCloseBlockIndexInput() \n" + //$NON-NLS-1$
                                               "    Opened/Closed Index file # Times = " + nOpens * 5 + //$NON-NLS-1$
                                               " DTime = "); //$NON-NLS-1$

            for (int i = 0; i < nOpens * 10; i++) {
                // Close it right away.
                try {
                    input.open();
                } finally {
                    input.close();
                }
            }
            watch.stopPrintIncrementAndRestart("    ---->> TestCreateIndexFile.testOpenCloseBlockIndexInput() \n" + //$NON-NLS-1$
                                               "    Opened/Closed Index file # Times = " + nOpens * 10 + //$NON-NLS-1$
                                               " DTime = "); //$NON-NLS-1$
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("============================ END ===================================\n)"); //$NON-NLS-1$
    }

    class TestIndexer implements IIndexer {
        public String[] getFileTypes() {
            return null;
        }

        public void index( IDocument document,
                           IIndexerOutput output ) {
            output.addDocument(document);
            if (document instanceof TestFileDocument) {
                File file = ((TestFileDocument)document).getFile();
                WordEntry[] entries = helpCreateWordEntries(file);
                for (int i = 0; i < entries.length; i++) {
                    output.addRef(entries[i].getWord());
                }
            }
        }

        public void setFileTypes( String[] fileTypes ) {
        }

        public boolean shouldIndex( IDocument document ) {
            return true;
        }
    }

    private class TestFileDocument extends FileDocument {
        private File file;

        public TestFileDocument( File file ) {
            super(file);
            this.file = file;
        }

        public File getFile() {
            return this.file;
        }
    }

    private class TestIndex extends Index {
        public TestIndex( File indexDirectory,
                          String indexName,
                          boolean reuseExistingFile ) throws IOException {
            super(indexDirectory, indexName, reuseExistingFile);
        }

        public IEntryResult[] queryEntriesMatching( char[] prefix ) throws IOException {
            // save();
            BlocksIndexInput input = getBlocksIndexInput();
            try {
                return input.queryEntriesMatching(prefix, true);
            } finally {
                if (!this.doCache) {
                    input.close();
                }
            }
        }

        @Override
        protected BlocksIndexInput getBlocksIndexInput() {
            if (doCache) {
                if (getCachedInput() == null) {
                    boolean wasLoaded = false;
                    try {
                        if (getCachedInput() == null) {
                            setCachedInput(new TestBlocksIndexInput(super.getIndexFile()));
                            getCachedInput().open();
                            wasLoaded = true;
                        }
                    } catch (IOException theException) {

                    } finally {
                        if (wasLoaded && getCachedInput() != null) {
                            return getCachedInput();
                        }
                        setCachedInput(null);
                    }
                } else {
                    return getCachedInput();
                }
            }

            return new TestBlocksIndexInput(super.getIndexFile());
        }
    }

    private class TestBlocksIndexInput extends BlocksIndexInput {
        public TestBlocksIndexInput( File inputFile ) {
            super(inputFile);
        }
    }
}
