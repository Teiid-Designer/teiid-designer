/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.metadata.runtime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.core.runtime.Path;
import com.metamatrix.core.index.IEntryResult;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.internal.core.index.EntryResult;
import com.metamatrix.internal.core.index.WordEntry;
import com.metamatrix.metadata.runtime.impl.RecordFactory;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.metadata.runtime.AnnotationRecord;
import com.metamatrix.modeler.core.metadata.runtime.ColumnRecord;
import com.metamatrix.modeler.core.metadata.runtime.DatatypeRecord;
import com.metamatrix.modeler.core.metadata.runtime.FileRecord;
import com.metamatrix.modeler.core.metadata.runtime.ForeignKeyRecord;
import com.metamatrix.modeler.core.metadata.runtime.ModelRecord;
import com.metamatrix.modeler.core.metadata.runtime.ProcedureParameterRecord;
import com.metamatrix.modeler.core.metadata.runtime.ProcedureRecord;
import com.metamatrix.modeler.core.metadata.runtime.PropertyRecord;
import com.metamatrix.modeler.core.metadata.runtime.TableRecord;
import com.metamatrix.modeler.core.metadata.runtime.UniqueKeyRecord;
import com.metamatrix.modeler.core.metadata.runtime.VdbRecord;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAnnotationAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlDatatypeAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlForeignKeyAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlModelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureParameterAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlUniqueKeyAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlVdbAspect;

/**
 * TestRuntimeAdapter
 */
public class TestRuntimeAdapter extends TestCase {
    
    private static final List WORD_ENTRIES = new ArrayList(7);
    
    // -------------------------------------------------
    // Variables initialized during one-time startup ...
    // -------------------------------------------------
    
    // ---------------------------------------
    // Variables initialized for each test ...
    // ---------------------------------------
    
    // =========================================================================
    //                        F R A M E W O R K
    // =========================================================================
    
    /**
     * Constructor for TestRuntimeAdapter.
     * @param name
     */
    public TestRuntimeAdapter(String name) {
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
        TestSuite suite = new TestSuite("TestRuntimeAdapter"); //$NON-NLS-1$

//        suite.addTest(new TestRuntimeAdapter("testJoinEntryResultsWithContinuations4")); //$NON-NLS-1$
        suite.addTestSuite(TestRuntimeAdapter.class);
    
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
    }
    
    public static void oneTimeSetUp() {
    }
    
    public static void oneTimeTearDown() {
    }
    
    // =========================================================================
    //                      H E L P E R   M E T H O D S
    // =========================================================================
    
    private WordEntry createColumnWord(final SqlColumnAspect aspect, final String modelPath) {
        WORD_ENTRIES.clear();
        RuntimeAdapter.addColumnWord(aspect,null,modelPath,WORD_ENTRIES);
        return (WordEntry)WORD_ENTRIES.get(0);
    }
    
    private WordEntry createVdbWord(final SqlVdbAspect aspect, final String modelPath) {
        WORD_ENTRIES.clear();
        RuntimeAdapter.addVdbWord(aspect,null,modelPath,WORD_ENTRIES);
        return (WordEntry)WORD_ENTRIES.get(0);
    }

    private WordEntry createFileWord(final String pathInVDb) {
        WORD_ENTRIES.clear();
        RuntimeAdapter.addFileIndexWord(pathInVDb, WORD_ENTRIES);
        return (WordEntry)WORD_ENTRIES.get(0);
    }    

    private WordEntry createTableWord(final SqlTableAspect aspect, final String modelPath) {
        WORD_ENTRIES.clear();
        RuntimeAdapter.addTableWord(aspect,null,null,modelPath,WORD_ENTRIES);
        return (WordEntry)WORD_ENTRIES.get(0);
    }
    
    private WordEntry createDatatypeWord(final SqlDatatypeAspect aspect, final String modelPath) {
        WORD_ENTRIES.clear();
        RuntimeAdapter.addDatatypeWord(aspect,null,modelPath,WORD_ENTRIES);
        return (WordEntry)WORD_ENTRIES.get(0);
    }
    
    private WordEntry createModelWord(final SqlModelAspect aspect, final String modelPath) {
        WORD_ENTRIES.clear();
        RuntimeAdapter.addModelWord(aspect,null,modelPath,WORD_ENTRIES);
        return (WordEntry)WORD_ENTRIES.get(0);
    }
    
    private WordEntry createAnnotationWord(final SqlAnnotationAspect aspect, final String modelPath) {
        WORD_ENTRIES.clear();
        RuntimeAdapter.addAnnotationWord(aspect,null,modelPath,WORD_ENTRIES);
        if (WORD_ENTRIES.isEmpty()) {
            return null;
        }
        return (WordEntry)WORD_ENTRIES.get(0);
    }

    private WordEntry createPropertyWord(final String objectID, final String name, final String propName, final String propValue, final String modelPath) {
        WORD_ENTRIES.clear();
        RuntimeAdapter.addPropertyWord(objectID, name, propName, propValue, true, modelPath, WORD_ENTRIES);
        if (WORD_ENTRIES.isEmpty()) {
            return null;
        }
        return (WordEntry)WORD_ENTRIES.get(0);
    }

    private WordEntry createCallableWord(final SqlProcedureAspect aspect, final String modelPath) {
        WORD_ENTRIES.clear();
        RuntimeAdapter.addCallableWord(aspect,null,modelPath,WORD_ENTRIES);
        return (WordEntry)WORD_ENTRIES.get(0);
    }

    private WordEntry createCallableParameterWord(final SqlProcedureParameterAspect aspect, final String modelPath) {
        WORD_ENTRIES.clear();
        RuntimeAdapter.addCallableParameterWord(aspect,null,modelPath,WORD_ENTRIES);
        return (WordEntry)WORD_ENTRIES.get(0);
    }

    private WordEntry createForeignKeyWord(final SqlForeignKeyAspect aspect, final String modelPath) {
        WORD_ENTRIES.clear();
        RuntimeAdapter.addForeignKeyWord(aspect,null,modelPath,WORD_ENTRIES);
        return (WordEntry)WORD_ENTRIES.get(0);
    }

    private WordEntry createUniqueKeyWord(final SqlUniqueKeyAspect aspect, final String modelPath) {
        WORD_ENTRIES.clear();
        RuntimeAdapter.addUniqueKeyWord(aspect,null,modelPath,WORD_ENTRIES);
        return (WordEntry)WORD_ENTRIES.get(0);
    }
    
    private WordEntry helpCreateWordEntry(final String objectID, final String suffix) {
        assertNotNull(objectID);
        assertNotNull(suffix);
        String entryStr = "A"  //$NON-NLS-1$
                        + IndexConstants.RECORD_STRING.RECORD_DELIMITER 
                        + objectID 
                        + IndexConstants.RECORD_STRING.RECORD_DELIMITER 
                        + suffix;
                        
        WordEntry wordEntry = new WordEntry(entryStr.toCharArray());
        assertNotNull(wordEntry);
        System.out.println("  WordEntry = "+entryStr); //$NON-NLS-1$
        return wordEntry;
    }
    
    private List helpTestSplitWordEntry(final String objectID, final WordEntry wordEntry, final int blockSize) {
        assertNotNull(objectID);
        assertNotNull(wordEntry);                        
        List entries = RuntimeAdapter.splitWordEntry(objectID, wordEntry, blockSize);
        assertNotNull(entries);
        printWordEntryList(entries);
        return entries;
    }
    
    private IEntryResult helpTestJoinEntryResults(final List wordEntries, final int blockSize) {
        IEntryResult result = null;
        IEntryResult[] continuationResults = new IEntryResult[wordEntries.size()-1];
        int j = 0;
        for (int i = 0, n = wordEntries.size(); i < n; i++) {
            WordEntry wordEntry = (WordEntry)wordEntries.get(i);
            IEntryResult entryResult = new EntryResult(wordEntry.getWord(),new int[0]);
            if (wordEntry.getWord()[0] != IndexConstants.RECORD_TYPE.RECORD_CONTINUATION) {
                result = entryResult;
            } else {
                continuationResults[j] = entryResult;
                j++;
            }
        }
        result = RecordFactory.joinEntryResults(result, continuationResults, blockSize);
        assertNotNull(result);
        System.out.println("  Joined IEntryResult = "+new String(result.getWord())); //$NON-NLS-1$
        return result;
    }
    
    private void printWordEntryList(final List wordEntries) {
        System.out.println("  WordEntry list size = "+wordEntries.size()); //$NON-NLS-1$
        for (Iterator iter = wordEntries.iterator(); iter.hasNext();) {
            WordEntry wordEntry = (WordEntry)iter.next();
            System.out.println("    "+wordEntry.toString()); //$NON-NLS-1$
        }
    }
    
//    private WordEntry createTransformationWord(final SqlTransformationAspect aspect, final String modelPath) {
//        WORD_ENTRIES.clear();
//        RuntimeAdapter.addTransformationWords(aspect,null,modelPath,WORD_ENTRIES);
//        return (WordEntry)WORD_ENTRIES.get(0);
//    }

    // =========================================================================
    //                         T E S T   C A S E S
    // =========================================================================
    
    public void testIndexRecordVersioning() {
        System.out.println("TestRuntimeAdapter.testIndexRecordVersioning()"); //$NON-NLS-1$

        Object fieldValue = "value1,value2,value3"; //$NON-NLS-1$
        WordEntry word = RuntimeAdapter.createTestWordEntry('#', 4, (String)fieldValue);
        System.out.println("word = "+word); //$NON-NLS-1$
        assertEquals(RecordFactory.CURRENT_INDEX_VERSION,RecordFactory.getIndexVersion(word.getWord()));

        fieldValue = new ArrayList();
        ((List)fieldValue).add("value1,value2,value3"); //$NON-NLS-1$
        word = RuntimeAdapter.createTestWordEntry('#', 4, (List)fieldValue); 
        System.out.println("word = "+word); //$NON-NLS-1$
        assertEquals(RecordFactory.CURRENT_INDEX_VERSION,RecordFactory.getIndexVersion(word.getWord()));
        
        // Ensure that the field value delimiter switches with index version number
        String str = new String(word.getWord());
        List tokens = StringUtil.split(str,String.valueOf(IndexConstants.RECORD_STRING.RECORD_DELIMITER));
        List values = RecordFactory.getStrings((String)tokens.get(2), RecordFactory.CURRENT_INDEX_VERSION);
        assertEquals(1,values.size());
        assertEquals("value1,value2,value3",values.get(0)); //$NON-NLS-1$
        values = RecordFactory.getStrings((String)tokens.get(2), RecordFactory.NONVERSIONED_RECORD_INDEX_VERSION);
        assertEquals(3,values.size());
        assertEquals("value1",values.get(0)); //$NON-NLS-1$
        assertEquals("value2",values.get(1)); //$NON-NLS-1$
        assertEquals("value3",values.get(2)); //$NON-NLS-1$

        fieldValue = new HashMap();
        ((Map)fieldValue).put("name","value1,value2,value3"); //$NON-NLS-1$ //$NON-NLS-2$
        word = RuntimeAdapter.createTestWordEntry('#', 4, (Map)fieldValue); 
        System.out.println("word = "+word); //$NON-NLS-1$
        assertEquals(RecordFactory.CURRENT_INDEX_VERSION,RecordFactory.getIndexVersion(word.getWord()));
        
        // Ensure that the field value delimiter switches with index version number
        str = new String(word.getWord());
        tokens = StringUtil.split(str,String.valueOf(IndexConstants.RECORD_STRING.RECORD_DELIMITER));
        Properties props = RuntimeAdapter.getProperties((String)tokens.get(2), RecordFactory.CURRENT_INDEX_VERSION);
        assertEquals(1,props.size());
        assertEquals("value1,value2,value3",props.get("name")); //$NON-NLS-1$ //$NON-NLS-2$
        props = RuntimeAdapter.getProperties((String)tokens.get(2), RecordFactory.NONVERSIONED_RECORD_INDEX_VERSION);
        assertEquals(0,props.size());
    }
    
    public void testCreateAbstractWord() {
        System.out.println("TestRuntimeAdapter.testCreateAbstractWord()"); //$NON-NLS-1$

        String modelPath = "myprj/myModel"; //$NON-NLS-1$

        FakeSqlColumnAspect aspect = new FakeSqlColumnAspect();
        WordEntry word = createColumnWord(aspect,modelPath);
        System.out.println("word = "+word); //$NON-NLS-1$

        ColumnRecord record = RecordFactory.createColumnRecord(word.getWord());
        assertEquals(" ",record.getName()); //$NON-NLS-1$
        assertEquals(" ",record.getFullName()); //$NON-NLS-1$
        assertEquals(" ",record.getModelName()); //$NON-NLS-1$
        assertEquals(" ",record.getPathString()); //$NON-NLS-1$
        
// cannot test this as the record constructed from word has "spaces" instead of "nulls" for field values
//        aspect.name = "Column"; //$NON-NLS-1$
//        word   = createColumnWord(aspect,modelPath);
//        record = RuntimeAdapter.createColumnRecord(word.getWord());
//        assertEquals(aspect.name,record.getName());
//        assertEquals(aspect.name,record.getFullName());
//        assertEquals(aspect.name,record.getModelName());
//        assertEquals(aspect.name,record.getPathString());
    }

    public void testCreateColumnWord() {
        System.out.println("TestRuntimeAdapter.testCreateColumnWord()"); //$NON-NLS-1$

        String modelPath = "myprj/myModel"; //$NON-NLS-1$

        FakeSqlColumnAspect aspect = new FakeSqlColumnAspect();
        WordEntry word = createColumnWord(aspect,modelPath);
        System.out.println("word = "+word); //$NON-NLS-1$
        ColumnRecord record = RecordFactory.createColumnRecord(word.getWord());
        assertNull(record.getUUID());
        assertNotNull(record.getPath());

        word   = createColumnWord(aspect,modelPath);
        record = RecordFactory.createColumnRecord(word.getWord());
        assertEquals(null,record.getUUID());

        aspect.uuid = "uuid"; //$NON-NLS-1$
        word   = createColumnWord(aspect,modelPath);
        record = RecordFactory.createColumnRecord(word.getWord());
        assertEquals(aspect.uuid,record.getUUID());

        aspect.path = new Path("Model/Table/Column"); //$NON-NLS-1$
        aspect.fullName = "Model.Table.Column"; //$NON-NLS-1$
        word   = createColumnWord(aspect,modelPath);
        record = RecordFactory.createColumnRecord(word.getWord());
        assertEquals(aspect.path.toString(),record.getPath());

        aspect.name = "Column"; //$NON-NLS-1$
        word   = createColumnWord(aspect,modelPath);
        record = RecordFactory.createColumnRecord(word.getWord());
        assertEquals(aspect.name,record.getName());

        aspect.nameInSource = "nameInSource"; //$NON-NLS-1$
        word   = createColumnWord(aspect,modelPath);
        record = RecordFactory.createColumnRecord(word.getWord());
        assertEquals(aspect.nameInSource,record.getNameInSource());

        aspect.selectable = true; 
        word   = createColumnWord(aspect,modelPath);
        record = RecordFactory.createColumnRecord(word.getWord());
        assertEquals(aspect.selectable,record.isSelectable());
        aspect.selectable = false; 

        aspect.updatable = true; 
        word   = createColumnWord(aspect,modelPath);
        record = RecordFactory.createColumnRecord(word.getWord());
        assertEquals(aspect.updatable,record.isUpdatable());
        aspect.updatable = false; 

        aspect.nullType = 1;
        word   = createColumnWord(aspect,modelPath);
        record = RecordFactory.createColumnRecord(word.getWord());
        assertEquals(aspect.nullType,record.getNullType());
        aspect.nullType = 0;

        aspect.autoIncrementable = true;
        word   = createColumnWord(aspect,modelPath);
        record = RecordFactory.createColumnRecord(word.getWord());
        assertEquals(aspect.autoIncrementable,record.isAutoIncrementable());
        aspect.autoIncrementable = false;

        aspect.caseSensitive = true;
        word   = createColumnWord(aspect,modelPath);
        record = RecordFactory.createColumnRecord(word.getWord());
        assertEquals(aspect.caseSensitive,record.isCaseSensitive());
        aspect.caseSensitive = false;

        aspect.signed = true;
        word   = createColumnWord(aspect,modelPath);
        record = RecordFactory.createColumnRecord(word.getWord());
        assertEquals(aspect.signed,record.isSigned());
        aspect.signed = false;

        aspect.currency = true;
        word   = createColumnWord(aspect,modelPath);
        record = RecordFactory.createColumnRecord(word.getWord());
        assertEquals(aspect.currency,record.isCurrency());
        aspect.currency = false;

        aspect.fixedLength = true;
        word   = createColumnWord(aspect,modelPath);
        record = RecordFactory.createColumnRecord(word.getWord());
        assertEquals(aspect.fixedLength,record.isFixedLength());
        aspect.fixedLength = false;

        aspect.runtimeType = "integer"; //$NON-NLS-1$
        word   = createColumnWord(aspect,modelPath);
        record = RecordFactory.createColumnRecord(word.getWord());
        assertEquals(aspect.runtimeType,record.getRuntimeType());

        aspect.datatypeUUID = "datatypeUUID"; //$NON-NLS-1$
        word   = createColumnWord(aspect,modelPath);
        record = RecordFactory.createColumnRecord(word.getWord());
        assertEquals(aspect.datatypeUUID,record.getDatatypeUUID());

        aspect.searchType = 2; 
        word   = createColumnWord(aspect,modelPath);
        record = RecordFactory.createColumnRecord(word.getWord());
        assertEquals(aspect.searchType,record.getSearchType());

        aspect.defaultValue = "defaultValue"; //$NON-NLS-1$
        word   = createColumnWord(aspect,modelPath);
        record = RecordFactory.createColumnRecord(word.getWord());
        assertEquals(aspect.defaultValue,record.getDefaultValue());

        aspect.minValue = "minValue"; //$NON-NLS-1$
        word   = createColumnWord(aspect,modelPath);
        record = RecordFactory.createColumnRecord(word.getWord());
        assertEquals(aspect.minValue,record.getMinValue());

        aspect.maxValue = "maxValue"; //$NON-NLS-1$
        word   = createColumnWord(aspect,modelPath);
        record = RecordFactory.createColumnRecord(word.getWord());
        assertEquals(aspect.maxValue,record.getMaxValue());

        aspect.format = "format"; //$NON-NLS-1$
        word   = createColumnWord(aspect,modelPath);
        record = RecordFactory.createColumnRecord(word.getWord());
        assertEquals(aspect.format,record.getFormat());

        aspect.length = 5; 
        word   = createColumnWord(aspect,modelPath);
        record = RecordFactory.createColumnRecord(word.getWord());
        assertEquals(aspect.length,record.getLength());

        aspect.scale = 10; 
        word   = createColumnWord(aspect,modelPath);
        record = RecordFactory.createColumnRecord(word.getWord());
        assertEquals(aspect.scale,record.getScale());

        aspect.precision = 15; 
        word   = createColumnWord(aspect,modelPath);
        record = RecordFactory.createColumnRecord(word.getWord());
        assertEquals(aspect.precision,record.getPrecision());

        aspect.position = 7; 
        word   = createColumnWord(aspect,modelPath);
        record = RecordFactory.createColumnRecord(word.getWord());
        assertEquals(aspect.position,record.getPosition());

        aspect.charOctetLength = 20; 
        word   = createColumnWord(aspect,modelPath);
        record = RecordFactory.createColumnRecord(word.getWord());
        assertEquals(aspect.charOctetLength,record.getCharOctetLength());

        aspect.radix = 99; 
        word   = createColumnWord(aspect,modelPath);
        record = RecordFactory.createColumnRecord(word.getWord());
        assertEquals(aspect.radix,record.getRadix());

        aspect.distinctValues = 99; 
        word   = createColumnWord(aspect,modelPath);
        record = RecordFactory.createColumnRecord(word.getWord());
        assertEquals(aspect.distinctValues,record.getDistinctValues());

        aspect.nullValues = 99;
        word   = createColumnWord(aspect,modelPath);
        record = RecordFactory.createColumnRecord(word.getWord());
        assertEquals(aspect.nullValues,record.getNullValues());

        word   = createColumnWord(aspect,modelPath);
        record = RecordFactory.createColumnRecord(word.getWord());
        assertEquals(new Path(modelPath).toString(), record.getResourcePath());
    }
    
    public void testCreateDatatypeWord() {
        System.out.println("TestRuntimeAdapter.testCreateDatatypeWord()"); //$NON-NLS-1$

        String modelPath = "myprj/myModel"; //$NON-NLS-1$

        FakeSqlDatatypeAspect aspect = new FakeSqlDatatypeAspect();
        WordEntry word = createDatatypeWord(aspect,modelPath);
        System.out.println("word = "+word); //$NON-NLS-1$
        DatatypeRecord record = RecordFactory.createDatatypeRecord(word.getWord());
        assertNull(record.getUUID());
        assertNotNull(record.getPath());

        word   = createDatatypeWord(aspect,modelPath);
        record = RecordFactory.createDatatypeRecord(word.getWord());
        assertEquals(null,record.getUUID());

        aspect.uuid = "uuid"; //$NON-NLS-1$
        word   = createDatatypeWord(aspect,modelPath);
        record = RecordFactory.createDatatypeRecord(word.getWord());
        assertEquals(aspect.uuid,record.getUUID());

        aspect.path = new Path("Model/Table/Column"); //$NON-NLS-1$
        aspect.fullName = "Model.Table.Column"; //$NON-NLS-1$
        word   = createDatatypeWord(aspect,modelPath);
        record = RecordFactory.createDatatypeRecord(word.getWord());
        assertEquals(aspect.path.toString(),record.getPath());

        aspect.name = "Column"; //$NON-NLS-1$
        word   = createDatatypeWord(aspect,modelPath);
        record = RecordFactory.createDatatypeRecord(word.getWord());
        assertEquals(aspect.name,record.getName());

        aspect.nameInSource = "nameInSource"; //$NON-NLS-1$
        word   = createDatatypeWord(aspect,modelPath);
        record = RecordFactory.createDatatypeRecord(word.getWord());
        assertEquals(aspect.nameInSource,record.getNameInSource());

        aspect.isSigned = true; 
        word   = createDatatypeWord(aspect,modelPath);
        record = RecordFactory.createDatatypeRecord(word.getWord());
        assertEquals(aspect.isSigned,record.isSigned());
        aspect.isSigned = false; 

        aspect.isAutoIncrement = true; 
        word   = createDatatypeWord(aspect,modelPath);
        record = RecordFactory.createDatatypeRecord(word.getWord());
        assertEquals(aspect.isAutoIncrement,record.isAutoIncrement());
        aspect.isAutoIncrement = false; 

        aspect.isCaseSensitive = true;
        word   = createDatatypeWord(aspect,modelPath);
        record = RecordFactory.createDatatypeRecord(word.getWord());
        assertEquals(aspect.isCaseSensitive,record.isCaseSensitive());
        aspect.isCaseSensitive = false;

        aspect.length = 2; 
        word   = createDatatypeWord(aspect,modelPath);
        record = RecordFactory.createDatatypeRecord(word.getWord());
        assertEquals(aspect.length,record.getLength());

        aspect.precisionLength = 4; 
        word   = createDatatypeWord(aspect,modelPath);
        record = RecordFactory.createDatatypeRecord(word.getWord());
        assertEquals(aspect.precisionLength,record.getPrecisionLength());

        aspect.scale = 3; 
        word   = createDatatypeWord(aspect,modelPath);
        record = RecordFactory.createDatatypeRecord(word.getWord());
        assertEquals(aspect.scale,record.getScale());

        aspect.radix = 99; 
        word   = createDatatypeWord(aspect,modelPath);
        record = RecordFactory.createDatatypeRecord(word.getWord());
        assertEquals(aspect.radix,record.getRadix());
        
        aspect.type = 33; 
        word   = createDatatypeWord(aspect,modelPath);
        record = RecordFactory.createDatatypeRecord(word.getWord());
        assertEquals(aspect.type,record.getType());

        aspect.searchType = 5; 
        word   = createDatatypeWord(aspect,modelPath);
        record = RecordFactory.createDatatypeRecord(word.getWord());
        assertEquals(aspect.searchType,record.getSearchType());

        aspect.nullType = 6; 
        word   = createDatatypeWord(aspect,modelPath);
        record = RecordFactory.createDatatypeRecord(word.getWord());
        assertEquals(aspect.nullType,record.getNullType());

        aspect.varietyType = 7; 
        word   = createDatatypeWord(aspect,modelPath);
        record = RecordFactory.createDatatypeRecord(word.getWord());
        assertEquals(aspect.varietyType,record.getVarietyType());

        String[] props = new String[]{"memberType1","memberType2"}; //$NON-NLS-1$ //$NON-NLS-2$
        aspect.varietyProps = Arrays.asList(props); 
        word   = createDatatypeWord(aspect,modelPath);
        record = RecordFactory.createDatatypeRecord(word.getWord());
        assertEquals(aspect.varietyProps,record.getVarietyProps());

        String[] props2 = new String[]{"itemType1"}; //$NON-NLS-1$
        aspect.varietyProps = Arrays.asList(props2); 
        word   = createDatatypeWord(aspect,modelPath);
        record = RecordFactory.createDatatypeRecord(word.getWord());
        assertEquals(aspect.varietyProps,record.getVarietyProps());

        String[] props3 = new String[]{""}; //$NON-NLS-1$
        aspect.varietyProps = Arrays.asList(props3); 
        word   = createDatatypeWord(aspect,modelPath);
        record = RecordFactory.createDatatypeRecord(word.getWord());
        assertEquals(0,record.getVarietyProps().size());

        aspect.javaClassName = "java.lang.Short"; //$NON-NLS-1$
        word   = createDatatypeWord(aspect,modelPath);
        record = RecordFactory.createDatatypeRecord(word.getWord());
        assertEquals(aspect.javaClassName,record.getJavaClassName());

        aspect.runtimeTypeName = "long"; //$NON-NLS-1$
        word   = createDatatypeWord(aspect,modelPath);
        record = RecordFactory.createDatatypeRecord(word.getWord());
        assertEquals(aspect.runtimeTypeName,record.getRuntimeTypeName());

        aspect.datatypeID = "http://www.w3.org/2001/XMLSchema#NMTOKEN"; //$NON-NLS-1$
        word   = createDatatypeWord(aspect,modelPath);
        record = RecordFactory.createDatatypeRecord(word.getWord());
        assertEquals(aspect.datatypeID,record.getDatatypeID());

        aspect.basetypeID = "http://www.w3.org/2001/XMLSchema#token"; //$NON-NLS-1$
        word   = createDatatypeWord(aspect,modelPath);
        record = RecordFactory.createDatatypeRecord(word.getWord());
        assertEquals(aspect.basetypeID,record.getBasetypeID());

        aspect.primitiveTypeID = "http://www.w3.org/2001/XMLSchema#string"; //$NON-NLS-1$
        word   = createDatatypeWord(aspect,modelPath);
        record = RecordFactory.createDatatypeRecord(word.getWord());
        assertEquals(aspect.primitiveTypeID,record.getPrimitiveTypeID());

        word   = createDatatypeWord(aspect,modelPath);
        record = RecordFactory.createDatatypeRecord(word.getWord());
        assertEquals(new Path(modelPath).toString(), record.getResourcePath());
    }
    
    public void testCreateTableWord() {
        System.out.println("TestRuntimeAdapter.testCreateTableWord()"); //$NON-NLS-1$

        String modelPath = "myprj/myModel"; //$NON-NLS-1$

        FakeSqlTableAspect aspect = new FakeSqlTableAspect();
        WordEntry word = createTableWord(aspect,modelPath);
        System.out.println("word = "+word); //$NON-NLS-1$
        TableRecord record = RecordFactory.createTableRecord(word.getWord());
        assertNull(record.getUUID());
        assertNotNull(record.getPath());

        word   = createTableWord(aspect,modelPath);
        record = RecordFactory.createTableRecord(word.getWord());
        assertEquals(null,record.getUUID());

        aspect.uuid = "uuid"; //$NON-NLS-1$
        word   = createTableWord(aspect,modelPath);
        record = RecordFactory.createTableRecord(word.getWord());
        assertEquals(aspect.uuid,record.getUUID());

        aspect.path = new Path("Model/Table"); //$NON-NLS-1$
        aspect.fullName = "Model.Table"; //$NON-NLS-1$
        word   = createTableWord(aspect,modelPath);
        record = RecordFactory.createTableRecord(word.getWord());
        assertEquals(aspect.path.toString(),record.getPath());

        aspect.name = "Table"; //$NON-NLS-1$
        word   = createTableWord(aspect,modelPath);
        record = RecordFactory.createTableRecord(word.getWord());
        assertEquals(aspect.name,record.getName());

        aspect.nameInSource = "nameInSource"; //$NON-NLS-1$
        word   = createTableWord(aspect,modelPath);
        record = RecordFactory.createTableRecord(word.getWord());
        assertEquals(aspect.nameInSource,record.getNameInSource());

        aspect.virtual = true; 
        word   = createTableWord(aspect,modelPath);
        record = RecordFactory.createTableRecord(word.getWord());
        assertEquals(aspect.virtual,record.isVirtual());
        aspect.virtual = false; 

        aspect.system = true; 
        word   = createTableWord(aspect,modelPath);
        record = RecordFactory.createTableRecord(word.getWord());
        assertEquals(aspect.system,record.isSystem());
        aspect.system = false; 

        aspect.supportsUpdate = true; 
        word   = createTableWord(aspect,modelPath);
        record = RecordFactory.createTableRecord(word.getWord());
        assertEquals(aspect.supportsUpdate,record.supportsUpdate());
        aspect.supportsUpdate = false; 

        aspect.cardinality = 5; 
        word   = createTableWord(aspect,modelPath);
        record = RecordFactory.createTableRecord(word.getWord());
        assertEquals(aspect.cardinality,record.getCardinality());

        aspect.primaryKey = "pk1"; //$NON-NLS-1$
        word   = createTableWord(aspect,modelPath);
        record = RecordFactory.createTableRecord(word.getWord());
        assertEquals(aspect.primaryKey,record.getPrimaryKeyID());

//        String[] columnIDs = new String[0];
//        aspect.columns = Arrays.asList(columnIDs); 
//        word   = createTableWord(aspect,modelPath);
//        record = RuntimeAdapter.createTableRecord(word.getWord());
//        assertEquals(aspect.columns,record.getColumnIDs());
//
//        columnIDs = new String[]{""}; //$NON-NLS-1$
//        aspect.columns = Arrays.asList(columnIDs); 
//        word   = createTableWord(aspect,modelPath);
//        record = RuntimeAdapter.createTableRecord(word.getWord());
//        assertEquals(Collections.EMPTY_LIST,record.getColumnIDs());
//
//        columnIDs = new String[]{"id1",""}; //$NON-NLS-1$ //$NON-NLS-2$
//        aspect.columns = Arrays.asList(columnIDs); 
//        word   = createTableWord(aspect,modelPath);
//        record = RuntimeAdapter.createTableRecord(word.getWord());
//        columnIDs = new String[]{"id1"}; //$NON-NLS-1$
//        aspect.columns = Arrays.asList(columnIDs); 
//        assertEquals(aspect.columns,record.getColumnIDs()); //$NON-NLS-1$
//
//        columnIDs = new String[]{"id1"}; //$NON-NLS-1$
//        aspect.columns = Arrays.asList(columnIDs); 
//        word   = createTableWord(aspect,modelPath);
//        record = RuntimeAdapter.createTableRecord(word.getWord());
//        assertEquals(aspect.columns,record.getColumnIDs());
//
//        columnIDs = new String[]{"id1","id2"}; //$NON-NLS-1$ //$NON-NLS-2$
//        aspect.columns = Arrays.asList(columnIDs); 
//        word   = createTableWord(aspect,modelPath);
//        record = RuntimeAdapter.createTableRecord(word.getWord());
//        assertEquals(aspect.columns,record.getColumnIDs());

        String[] foreignKeyIDs = new String[0];
        aspect.foreignKeys = Arrays.asList(foreignKeyIDs); 
        word   = createTableWord(aspect,modelPath);
        record = RecordFactory.createTableRecord(word.getWord());
        assertEquals(aspect.foreignKeys,record.getForeignKeyIDs());

        foreignKeyIDs = new String[]{"fk1"}; //$NON-NLS-1$
        aspect.foreignKeys = Arrays.asList(foreignKeyIDs); 
        word   = createTableWord(aspect,modelPath);
        record = RecordFactory.createTableRecord(word.getWord());
        assertEquals(aspect.foreignKeys,record.getForeignKeyIDs());

        foreignKeyIDs = new String[]{"fk1","fk2"}; //$NON-NLS-1$ //$NON-NLS-2$
        aspect.foreignKeys = Arrays.asList(foreignKeyIDs); 
        word   = createTableWord(aspect,modelPath);
        record = RecordFactory.createTableRecord(word.getWord());
        assertEquals(aspect.foreignKeys,record.getForeignKeyIDs());

        String[] indexIDs = new String[0];
        aspect.indexes = Arrays.asList(indexIDs); 
        word   = createTableWord(aspect,modelPath);
        record = RecordFactory.createTableRecord(word.getWord());
        assertEquals(aspect.indexes,record.getIndexIDs());

        indexIDs = new String[]{"index1"}; //$NON-NLS-1$
        aspect.indexes = Arrays.asList(indexIDs); 
        word   = createTableWord(aspect,modelPath);
        record = RecordFactory.createTableRecord(word.getWord());
        assertEquals(aspect.indexes,record.getIndexIDs());

        indexIDs = new String[]{"index1","index2"}; //$NON-NLS-1$ //$NON-NLS-2$
        aspect.indexes = Arrays.asList(indexIDs); 
        word   = createTableWord(aspect,modelPath);
        record = RecordFactory.createTableRecord(word.getWord());
        assertEquals(aspect.indexes,record.getIndexIDs());

        String[] uniqueKeyIDs = new String[0];
        aspect.uniqueKeys = Arrays.asList(uniqueKeyIDs); 
        word   = createTableWord(aspect,modelPath);
        record = RecordFactory.createTableRecord(word.getWord());
        assertEquals(aspect.uniqueKeys,record.getUniqueKeyIDs());

        uniqueKeyIDs = new String[]{"uk1"}; //$NON-NLS-1$
        aspect.uniqueKeys = Arrays.asList(uniqueKeyIDs); 
        word   = createTableWord(aspect,modelPath);
        record = RecordFactory.createTableRecord(word.getWord());
        assertEquals(aspect.uniqueKeys,record.getUniqueKeyIDs());

        uniqueKeyIDs = new String[]{"uk1","uk2"}; //$NON-NLS-1$ //$NON-NLS-2$
        aspect.uniqueKeys = Arrays.asList(uniqueKeyIDs); 
        word   = createTableWord(aspect,modelPath);
        record = RecordFactory.createTableRecord(word.getWord());
        assertEquals(aspect.uniqueKeys,record.getUniqueKeyIDs());

        String[] accessPatternIDs = new String[0];
        aspect.accessPatterns = Arrays.asList(accessPatternIDs); 
        word   = createTableWord(aspect,modelPath);
        record = RecordFactory.createTableRecord(word.getWord());
        assertEquals(aspect.accessPatterns,record.getAccessPatternIDs());

        accessPatternIDs = new String[]{"ac1"}; //$NON-NLS-1$
        aspect.accessPatterns = Arrays.asList(accessPatternIDs); 
        word   = createTableWord(aspect,modelPath);
        record = RecordFactory.createTableRecord(word.getWord());
        assertEquals(aspect.accessPatterns,record.getAccessPatternIDs());

        accessPatternIDs = new String[]{"ac1","ac2"}; //$NON-NLS-1$ //$NON-NLS-2$
        aspect.accessPatterns = Arrays.asList(accessPatternIDs); 
        word   = createTableWord(aspect,modelPath);
        record = RecordFactory.createTableRecord(word.getWord());
        assertEquals(aspect.accessPatterns,record.getAccessPatternIDs());

        word   = createTableWord(aspect,modelPath);
        record = RecordFactory.createTableRecord(word.getWord());
        assertEquals(new Path(modelPath).toString(), record.getResourcePath());
    }
    
    public void testCreateModelWord() {
        System.out.println("TestRuntimeAdapter.testCreateModelWord()"); //$NON-NLS-1$

        String modelPath = "myprj/myModel"; //$NON-NLS-1$

        FakeSqlModelAspect aspect = new FakeSqlModelAspect();
        WordEntry word = createModelWord(aspect,modelPath);
        System.out.println("word = "+word); //$NON-NLS-1$
        ModelRecord record = RecordFactory.createModelRecord(word.getWord());
        assertNull(record.getUUID());
        assertNotNull(record.getPath());

        aspect.uuid = "uuid"; //$NON-NLS-1$
        word   = createModelWord(aspect,modelPath);
        record = RecordFactory.createModelRecord(word.getWord());
        assertEquals(aspect.uuid,record.getUUID());

        aspect.path = new Path("Model"); //$NON-NLS-1$
        aspect.name = "Model"; //$NON-NLS-1$
        word   = createModelWord(aspect,modelPath);
        record = RecordFactory.createModelRecord(word.getWord());
        assertEquals(aspect.path.toString(),record.getPath());

        aspect.name = "Model"; //$NON-NLS-1$
        word   = createModelWord(aspect,modelPath);
        record = RecordFactory.createModelRecord(word.getWord());
        assertEquals(aspect.name,record.getName());

        aspect.nameInSource = "nameInSource"; //$NON-NLS-1$
        word   = createModelWord(aspect,modelPath);
        record = RecordFactory.createModelRecord(word.getWord());
        assertEquals(aspect.nameInSource,record.getNameInSource());

        aspect.isVisible = true; 
        word   = createModelWord(aspect,modelPath);
        record = RecordFactory.createModelRecord(word.getWord());
        assertEquals(aspect.isVisible,record.isVisible());
        aspect.isVisible = false; 

        aspect.maxSetSize = 99; 
        word   = createModelWord(aspect,modelPath);
        record = RecordFactory.createModelRecord(word.getWord());
        assertEquals(aspect.maxSetSize,record.getMaxSetSize());

        aspect.modelType = ModelType.VDB_ARCHIVE; 
        word   = createModelWord(aspect,modelPath);
        record = RecordFactory.createModelRecord(word.getWord());
        assertEquals(aspect.modelType,record.getModelType());

        aspect.primaryMetamodelUri = "http://www.metamatrix.com/metamodels/Relational";  //$NON-NLS-1$
        word   = createModelWord(aspect,modelPath);
        record = RecordFactory.createModelRecord(word.getWord());
        assertEquals(aspect.primaryMetamodelUri,record.getPrimaryMetamodelUri());

        aspect.supportsDistinct = true; 
        word   = createModelWord(aspect,modelPath);
        record = RecordFactory.createModelRecord(word.getWord());
        assertEquals(aspect.supportsDistinct,record.supportsDistinct());
        aspect.supportsDistinct = false; 

        aspect.supportsJoin = true; 
        word   = createModelWord(aspect,modelPath);
        record = RecordFactory.createModelRecord(word.getWord());
        assertEquals(aspect.supportsJoin,record.supportsJoin());
        aspect.supportsJoin = false; 

        aspect.supportsOrderBy = true; 
        word   = createModelWord(aspect,modelPath);
        record = RecordFactory.createModelRecord(word.getWord());
        assertEquals(aspect.supportsOrderBy,record.supportsOrderBy());
        aspect.supportsOrderBy = false; 

        aspect.supportsOuterJoin = true; 
        word   = createModelWord(aspect,modelPath);
        record = RecordFactory.createModelRecord(word.getWord());
        assertEquals(aspect.supportsOuterJoin,record.supportsOuterJoin());
        aspect.supportsOuterJoin = false; 

        aspect.supportsWhereAll = true; 
        word   = createModelWord(aspect,modelPath);
        record = RecordFactory.createModelRecord(word.getWord());
        assertEquals(aspect.supportsWhereAll,record.supportsWhereAll());
        aspect.supportsWhereAll = false; 
    }

    public void testCreateAnnotationWord() {
        System.out.println("TestRuntimeAdapter.testCreateAnnotationWord()"); //$NON-NLS-1$

        String modelPath = "myprj/myModel"; //$NON-NLS-1$

        // An AnnotationWord (i.e. WordEntry) will NOT be created if the description, keywords, 
        // or tags are null or empty

        FakeSqlAnnotationAspect aspect = new FakeSqlAnnotationAspect();
        WordEntry word = createAnnotationWord(aspect,modelPath);
        assertNull(word);

        aspect.uuid = "uuid"; //$NON-NLS-1$
        word   = createAnnotationWord(aspect,modelPath);
        assertNull(word);

        aspect.path = new Path("Model"); //$NON-NLS-1$
        aspect.fullName = "Model"; //$NON-NLS-1$
        word   = createAnnotationWord(aspect,modelPath);
        assertNull(word);

        aspect.name = "Model"; //$NON-NLS-1$
        word   = createAnnotationWord(aspect,modelPath);
        assertNull(word);

        aspect.nameInSource = "nameInSource"; //$NON-NLS-1$
        word   = createAnnotationWord(aspect,modelPath);
        assertNull(word);

        // An AnnotationWord (i.e. WordEntry) will be created if the description, keywords, 
        // or tags are not null and have content

        aspect.description = "description";  //$NON-NLS-1$
        word   = createAnnotationWord(aspect,modelPath);
        AnnotationRecord record = RecordFactory.createAnnotationRecord(word.getWord());
        assertEquals(aspect.description,record.getDescription());
    }

    public void testCreatePropertyWord() {
        System.out.println("TestRuntimeAdapter.testCreatePropertyWord()"); //$NON-NLS-1$

        String modelPath = "myprj/myModel"; //$NON-NLS-1$
        String objectID = "objectID"; //$NON-NLS-1$
        String name = "name"; //$NON-NLS-1$

        // An PropertyWord (i.e. WordEntry) will NOT be created if the proopertyName or propertyValue 
        // are null or empty

        WordEntry word = createPropertyWord(objectID, name, "propName", "propValue", modelPath); //$NON-NLS-1$ //$NON-NLS-2$
        assertNotNull(word);
        PropertyRecord record = RecordFactory.createPropertyRecord(word.getWord());
        assertEquals(objectID, record.getUUID());
        assertEquals(name, record.getName());
        assertEquals("propName", record.getPropertyName()); //$NON-NLS-1$
        assertEquals("propValue", record.getPropertyValue()); //$NON-NLS-1$
        assertTrue(record.isExtension()); 

        word = createPropertyWord(objectID, name, null, "propValue", modelPath); //$NON-NLS-1$ 
        assertNull(word);

        word = createPropertyWord(objectID, name, "propName", null, modelPath); //$NON-NLS-1$ 
        assertNull(word);

        word = createPropertyWord(objectID, name, "propName", "", modelPath); //$NON-NLS-1$ //$NON-NLS-2$
        assertNull(word);

        word = createPropertyWord(objectID, name, "", "propValue", modelPath); //$NON-NLS-1$ //$NON-NLS-2$
        assertNull(word);

    }

    public void testCreateVdbWord() {
        System.out.println("TestRuntimeAdapter.testCreateVdbWord()"); //$NON-NLS-1$

        String modelPath = "myprj/myModel"; //$NON-NLS-1$

        FakeSqlVdbAspect aspect = new FakeSqlVdbAspect();
        WordEntry word = createVdbWord(aspect,modelPath);
        System.out.println("word = "+word); //$NON-NLS-1$
        VdbRecord record = RecordFactory.createVdbRecord(word.getWord());
        assertNull(record.getUUID());
        assertNotNull(record.getPath());

        aspect.uuid = "uuid"; //$NON-NLS-1$
        word   = createVdbWord(aspect,modelPath);
        record = RecordFactory.createVdbRecord(word.getWord());
        assertEquals(aspect.uuid,record.getUUID());

        aspect.path = new Path("Model"); //$NON-NLS-1$
        aspect.fullName = "Model"; //$NON-NLS-1$
        word   = createVdbWord(aspect,modelPath);
        record = RecordFactory.createVdbRecord(word.getWord());
        assertEquals(aspect.path.toString(),record.getPath());

        aspect.name = "Model"; //$NON-NLS-1$
        word   = createVdbWord(aspect,modelPath);
        record = RecordFactory.createVdbRecord(word.getWord());
        assertEquals(aspect.name,record.getName());

        aspect.nameInSource = "nameInSource"; //$NON-NLS-1$
        word   = createVdbWord(aspect,modelPath);
        record = RecordFactory.createVdbRecord(word.getWord());
        assertEquals(aspect.nameInSource,record.getNameInSource());

        aspect.version = "1.2.3";  //$NON-NLS-1$
        word   = createVdbWord(aspect,modelPath);
        record = RecordFactory.createVdbRecord(word.getWord());
        assertEquals(aspect.version,record.getVersion());

        aspect.identifier = "myVdbIdentifer";  //$NON-NLS-1$
        word   = createVdbWord(aspect,modelPath);
        record = RecordFactory.createVdbRecord(word.getWord());
        assertEquals(aspect.identifier,record.getIdentifier());

        aspect.description = "myVdbIdentifer";  //$NON-NLS-1$
        word   = createVdbWord(aspect,modelPath);
        record = RecordFactory.createVdbRecord(word.getWord());
        assertEquals(aspect.description,record.getDescription());

        aspect.producerName = "myProducername";  //$NON-NLS-1$
        word   = createVdbWord(aspect,modelPath);
        record = RecordFactory.createVdbRecord(word.getWord());
        assertEquals(aspect.producerName,record.getProducerName());

        aspect.producerVersion = "myProducerVersion";  //$NON-NLS-1$
        word   = createVdbWord(aspect,modelPath);
        record = RecordFactory.createVdbRecord(word.getWord());
        assertEquals(aspect.producerVersion,record.getProducerVersion());

        aspect.provider = "myProvider";  //$NON-NLS-1$
        word   = createVdbWord(aspect,modelPath);
        record = RecordFactory.createVdbRecord(word.getWord());
        assertEquals(aspect.provider,record.getProvider());

        aspect.timeLastChanged = "2003-10-21T15:58:44.125-06:00";  //$NON-NLS-1$
        word   = createVdbWord(aspect,modelPath);
        record = RecordFactory.createVdbRecord(word.getWord());
        assertEquals(aspect.timeLastChanged,record.getTimeLastChanged());

        aspect.timeLastProduced = "2003-03-04T12:11:31.431-06:00";  //$NON-NLS-1$
        word   = createVdbWord(aspect,modelPath);
        record = RecordFactory.createVdbRecord(word.getWord());
        assertEquals(aspect.timeLastProduced,record.getTimeLastProduced());

        String[] modelIDs = new String[]{"mdlID1","mdlID2"}; //$NON-NLS-1$ //$NON-NLS-2$
        aspect.models = Arrays.asList(modelIDs); 
        word   = createVdbWord(aspect,modelPath);
        record = RecordFactory.createVdbRecord(word.getWord());
        assertEquals(aspect.models,record.getModelIDs());

    }

    public void testCreateProcedureWord() {
        System.out.println("TestRuntimeAdapter.testCreateProcedureWord()"); //$NON-NLS-1$

        String modelPath = "myprj/myModel"; //$NON-NLS-1$

        FakeSqlProcedureAspect aspect = new FakeSqlProcedureAspect();
        WordEntry word = createCallableWord(aspect,modelPath);
        System.out.println("word = "+word); //$NON-NLS-1$
        ProcedureRecord record = RecordFactory.createProcedureRecord(word.getWord());
        assertNull(record.getUUID());
        assertNotNull(record.getPath());

        word   = createCallableWord(aspect,modelPath);
        record = RecordFactory.createProcedureRecord(word.getWord());
        assertEquals(null,record.getUUID());

        aspect.uuid = "uuid"; //$NON-NLS-1$
        word   = createCallableWord(aspect,modelPath);
        record = RecordFactory.createProcedureRecord(word.getWord());
        assertEquals(aspect.uuid,record.getUUID());

        aspect.path = new Path("Model/Proc"); //$NON-NLS-1$
        aspect.fullName = "Model.Proc"; //$NON-NLS-1$
        word   = createCallableWord(aspect,modelPath);
        record = RecordFactory.createProcedureRecord(word.getWord());
        assertEquals(aspect.path.toString(),record.getPath());

        aspect.name = "Proc"; //$NON-NLS-1$
        word   = createCallableWord(aspect,modelPath);
        record = RecordFactory.createProcedureRecord(word.getWord());
        assertEquals(aspect.name,record.getName());

        aspect.nameInSource = "nameInSource"; //$NON-NLS-1$
        word   = createCallableWord(aspect,modelPath);
        record = RecordFactory.createProcedureRecord(word.getWord());
        assertEquals(aspect.nameInSource,record.getNameInSource());

        aspect.function = true; 
        word   = createCallableWord(aspect,modelPath);
        record = RecordFactory.createProcedureRecord(word.getWord());
        assertEquals(aspect.function,record.isFunction());
        aspect.function = false;

        aspect.virtual = true;
        word   = createCallableWord(aspect,modelPath);
        record = RecordFactory.createProcedureRecord(word.getWord());
        assertEquals(aspect.virtual,record.isVirtual());
        aspect.virtual = false;

        aspect.result = "result"; //$NON-NLS-1$
        word   = createCallableWord(aspect,modelPath);
        record = RecordFactory.createProcedureRecord(word.getWord());
        assertEquals(aspect.result,record.getResultSetID());

        String[] paramIDs = new String[]{"id1","id2"}; //$NON-NLS-1$ //$NON-NLS-2$
        aspect.parameters = Arrays.asList(paramIDs); 
        word   = createCallableWord(aspect,modelPath);
        record = RecordFactory.createProcedureRecord(word.getWord());
        assertEquals(aspect.parameters,record.getParameterIDs());

        word   = createCallableWord(aspect,modelPath);
        record = RecordFactory.createProcedureRecord(word.getWord());
        assertEquals(new Path(modelPath).toString(), record.getResourcePath());

        aspect.updateCount = 3; // Corresponds to com.metamatrix.metamodels.relational.ProcedureUpdateCount#MULTIPLE
        word   = createCallableWord(aspect,modelPath);
        record = RecordFactory.createProcedureRecord(word.getWord());
        assertEquals(aspect.updateCount,record.getUpdateCount());
        aspect.updateCount = 0;
    }

    public void testCreateProcedureParameterWord() {
        System.out.println("TestRuntimeAdapter.testCreateProcedureParameterWord()"); //$NON-NLS-1$

        String modelPath = "myprj/myModel"; //$NON-NLS-1$

        FakeSqlProcedureParamAspect aspect = new FakeSqlProcedureParamAspect();
        WordEntry word = createCallableParameterWord(aspect,modelPath);
        System.out.println("word = "+word); //$NON-NLS-1$
        ProcedureParameterRecord record = RecordFactory.createProcedureParameterRecord(word.getWord());
        assertNull(record.getUUID());
        assertNotNull(record.getPath());

        word   = createCallableParameterWord(aspect,modelPath);
        record = RecordFactory.createProcedureParameterRecord(word.getWord());
        assertEquals(null,record.getUUID());

        aspect.uuid = "uuid"; //$NON-NLS-1$
        word   = createCallableParameterWord(aspect,modelPath);
        record = RecordFactory.createProcedureParameterRecord(word.getWord());
        assertEquals(aspect.uuid,record.getUUID());

        aspect.path = new Path("Model/Proc/Param"); //$NON-NLS-1$
        aspect.fullName = "Model.Proc.Param"; //$NON-NLS-1$
        word   = createCallableParameterWord(aspect,modelPath);
        record = RecordFactory.createProcedureParameterRecord(word.getWord());
        assertEquals(aspect.path.toString(),record.getPath());

        aspect.name = "Param"; //$NON-NLS-1$
        word   = createCallableParameterWord(aspect,modelPath);
        record = RecordFactory.createProcedureParameterRecord(word.getWord());
        assertEquals(aspect.name,record.getName());

        aspect.nameInSource = "nameInSource"; //$NON-NLS-1$
        word   = createCallableParameterWord(aspect,modelPath);
        record = RecordFactory.createProcedureParameterRecord(word.getWord());
        assertEquals(aspect.nameInSource,record.getNameInSource());

        aspect.nullType = 1;
        word   = createCallableParameterWord(aspect,modelPath);
        record = RecordFactory.createProcedureParameterRecord(word.getWord());
        assertEquals(aspect.nullType,record.getNullType());
        aspect.nullType = 0;

        aspect.runtimeType = "integer"; //$NON-NLS-1$
        word   = createCallableParameterWord(aspect,modelPath);
        record = RecordFactory.createProcedureParameterRecord(word.getWord());
        assertEquals(aspect.runtimeType,record.getRuntimeType());

        aspect.datatypeUUID = "datatypeUUID"; //$NON-NLS-1$
        word   = createCallableParameterWord(aspect,modelPath);
        record = RecordFactory.createProcedureParameterRecord(word.getWord());
        assertEquals(aspect.datatypeUUID,record.getDatatypeUUID());

        aspect.defaultValue = "defaultValue"; //$NON-NLS-1$
        word   = createCallableParameterWord(aspect,modelPath);
        record = RecordFactory.createProcedureParameterRecord(word.getWord());
        assertEquals(aspect.defaultValue,record.getDefaultValue());

        aspect.length = 5; 
        word   = createCallableParameterWord(aspect,modelPath);
        record = RecordFactory.createProcedureParameterRecord(word.getWord());
        assertEquals(aspect.length,record.getLength());

        aspect.scale = 10; 
        word   = createCallableParameterWord(aspect,modelPath);
        record = RecordFactory.createProcedureParameterRecord(word.getWord());
        assertEquals(aspect.scale,record.getScale());

        aspect.precision = 15; 
        word   = createCallableParameterWord(aspect,modelPath);
        record = RecordFactory.createProcedureParameterRecord(word.getWord());
        assertEquals(aspect.precision,record.getPrecision());

        aspect.position = 11;
        word   = createCallableParameterWord(aspect,modelPath);
        record = RecordFactory.createProcedureParameterRecord(word.getWord());
        assertEquals(aspect.position,record.getPosition());

        aspect.radix = 99;
        word   = createCallableParameterWord(aspect,modelPath);
        record = RecordFactory.createProcedureParameterRecord(word.getWord());
        assertEquals(aspect.radix,record.getRadix());

        aspect.optional = true;
        word   = createCallableParameterWord(aspect,modelPath);
        record = RecordFactory.createProcedureParameterRecord(word.getWord());
        assertEquals(aspect.optional,record.isOptional());

        word   = createCallableParameterWord(aspect,modelPath);
        record = RecordFactory.createProcedureParameterRecord(word.getWord());
        assertEquals(new Path(modelPath).toString(), record.getResourcePath());
    }
    
    public void testForeignKeyWord() {
        System.out.println("TestRuntimeAdapter.testForeignKeyWord()"); //$NON-NLS-1$

        String modelPath = "myprj/myModel"; //$NON-NLS-1$

        FakeForeignKeyAspect aspect = new FakeForeignKeyAspect();
        WordEntry word = createForeignKeyWord(aspect,modelPath);
        System.out.println("word = "+word); //$NON-NLS-1$
        ForeignKeyRecord record = RecordFactory.createForeignKeyRecord(word.getWord());
        assertNull(record.getUUID());
        assertNotNull(record.getPath());

        word   = createForeignKeyWord(aspect,modelPath);
        record = RecordFactory.createForeignKeyRecord(word.getWord());
        assertEquals(null,record.getUUID());

        aspect.uuid = "uuid"; //$NON-NLS-1$
        word   = createForeignKeyWord(aspect,modelPath);
        record = RecordFactory.createForeignKeyRecord(word.getWord());
        assertEquals(aspect.uuid,record.getUUID());

        aspect.path = new Path("Model/Table/PK"); //$NON-NLS-1$
        aspect.fullName = "Model.Table.PK"; //$NON-NLS-1$
        word   = createForeignKeyWord(aspect,modelPath);
        record = RecordFactory.createForeignKeyRecord(word.getWord());
        assertEquals(aspect.path.toString(),record.getPath());

        aspect.name = "PK"; //$NON-NLS-1$
        word   = createForeignKeyWord(aspect,modelPath);
        record = RecordFactory.createForeignKeyRecord(word.getWord());
        assertEquals(aspect.name,record.getName());

        aspect.nameInSource = "nameInSource"; //$NON-NLS-1$
        word   = createForeignKeyWord(aspect,modelPath);
        record = RecordFactory.createForeignKeyRecord(word.getWord());
        assertEquals(aspect.nameInSource,record.getNameInSource());

        String[] columnIDs = new String[0];
        aspect.columns = Arrays.asList(columnIDs); 
        word   = createForeignKeyWord(aspect,modelPath);
        record = RecordFactory.createForeignKeyRecord(word.getWord());
        assertEquals(aspect.columns,record.getColumnIDs());

        columnIDs = new String[]{""}; //$NON-NLS-1$
        aspect.columns = Arrays.asList(columnIDs); 
        word   = createForeignKeyWord(aspect,modelPath);
        record = RecordFactory.createForeignKeyRecord(word.getWord());
        assertEquals(Collections.EMPTY_LIST,record.getColumnIDs());

        columnIDs = new String[]{"id1",""}; //$NON-NLS-1$ //$NON-NLS-2$
        aspect.columns = Arrays.asList(columnIDs); 
        word   = createForeignKeyWord(aspect,modelPath);
        record = RecordFactory.createForeignKeyRecord(word.getWord());
        columnIDs = new String[]{"id1"}; //$NON-NLS-1$
        aspect.columns = Arrays.asList(columnIDs); 
        assertEquals(aspect.columns,record.getColumnIDs()); 

        columnIDs = new String[]{"id1"}; //$NON-NLS-1$
        aspect.columns = Arrays.asList(columnIDs); 
        word   = createForeignKeyWord(aspect,modelPath);
        record = RecordFactory.createForeignKeyRecord(word.getWord());
        assertEquals(aspect.columns,record.getColumnIDs());

        columnIDs = new String[]{"id1","id2"}; //$NON-NLS-1$ //$NON-NLS-2$
        aspect.columns = Arrays.asList(columnIDs); 
        word   = createForeignKeyWord(aspect,modelPath);
        record = RecordFactory.createForeignKeyRecord(word.getWord());
        assertEquals(aspect.columns,record.getColumnIDs());

        aspect.uniqueKey = "uniqueKey"; //$NON-NLS-1$
        word   = createForeignKeyWord(aspect,modelPath);
        record = RecordFactory.createForeignKeyRecord(word.getWord());
        assertEquals(aspect.uniqueKey,record.getUniqueKeyID());
    }

    public void testUniqueKeyWord() {
        System.out.println("TestRuntimeAdapter.testForeignKeyWord()"); //$NON-NLS-1$

        String modelPath = "myprj/myModel"; //$NON-NLS-1$

        FakeUniqueKeyAspect aspect = new FakeUniqueKeyAspect();
        WordEntry word = createUniqueKeyWord(aspect,modelPath);
        System.out.println("word = "+word); //$NON-NLS-1$
        UniqueKeyRecord record = RecordFactory.createUniqueKeyRecord(word.getWord());
        assertNull(record.getUUID());
        assertNotNull(record.getPath());

        word   = createUniqueKeyWord(aspect,modelPath);
        record = RecordFactory.createUniqueKeyRecord(word.getWord());
        assertEquals(null,record.getUUID());

        aspect.uuid = "uuid"; //$NON-NLS-1$
        word   = createUniqueKeyWord(aspect,modelPath);
        record = RecordFactory.createUniqueKeyRecord(word.getWord());
        assertEquals(aspect.uuid,record.getUUID());

        aspect.path = new Path("Model/Table/UK"); //$NON-NLS-1$
        aspect.fullName = "Model.Table.UK"; //$NON-NLS-1$
        word   = createUniqueKeyWord(aspect,modelPath);
        record = RecordFactory.createUniqueKeyRecord(word.getWord());
        assertEquals(aspect.path.toString(),record.getPath());

        aspect.name = "UK"; //$NON-NLS-1$
        word   = createUniqueKeyWord(aspect,modelPath);
        record = RecordFactory.createUniqueKeyRecord(word.getWord());
        assertEquals(aspect.name,record.getName());

        aspect.nameInSource = "nameInSource"; //$NON-NLS-1$
        word   = createUniqueKeyWord(aspect,modelPath);
        record = RecordFactory.createUniqueKeyRecord(word.getWord());
        assertEquals(aspect.nameInSource,record.getNameInSource());

        String[] columnIDs = new String[0];
        aspect.columns = Arrays.asList(columnIDs); 
        word   = createUniqueKeyWord(aspect,modelPath);
        record = RecordFactory.createUniqueKeyRecord(word.getWord());
        assertEquals(aspect.columns,record.getColumnIDs());

        columnIDs = new String[]{""}; //$NON-NLS-1$
        aspect.columns = Arrays.asList(columnIDs); 
        word   = createUniqueKeyWord(aspect,modelPath);
        record = RecordFactory.createUniqueKeyRecord(word.getWord());
        assertEquals(Collections.EMPTY_LIST,record.getColumnIDs());

        columnIDs = new String[]{"id1",""}; //$NON-NLS-1$ //$NON-NLS-2$
        aspect.columns = Arrays.asList(columnIDs); 
        word   = createUniqueKeyWord(aspect,modelPath);
        record = RecordFactory.createUniqueKeyRecord(word.getWord());
        columnIDs = new String[]{"id1"}; //$NON-NLS-1$
        aspect.columns = Arrays.asList(columnIDs); 
        assertEquals(aspect.columns,record.getColumnIDs()); 

        columnIDs = new String[]{"id1"}; //$NON-NLS-1$
        aspect.columns = Arrays.asList(columnIDs); 
        word   = createUniqueKeyWord(aspect,modelPath);
        record = RecordFactory.createUniqueKeyRecord(word.getWord());
        assertEquals(aspect.columns,record.getColumnIDs());

        columnIDs = new String[]{"id1","id2"}; //$NON-NLS-1$ //$NON-NLS-2$
        aspect.columns = Arrays.asList(columnIDs); 
        word   = createUniqueKeyWord(aspect,modelPath);
        record = RecordFactory.createUniqueKeyRecord(word.getWord());
        assertEquals(aspect.columns,record.getColumnIDs());

        String[] fkIDs = new String[0];
        aspect.foreignKeys = Arrays.asList(fkIDs); 
        word   = createUniqueKeyWord(aspect,modelPath);
        record = RecordFactory.createUniqueKeyRecord(word.getWord());
        assertEquals(Collections.EMPTY_LIST,record.getForeignKeyIDs());

        fkIDs = new String[]{""}; //$NON-NLS-1$
        aspect.foreignKeys = Arrays.asList(fkIDs); 
        word   = createUniqueKeyWord(aspect,modelPath);
        record = RecordFactory.createUniqueKeyRecord(word.getWord());
        assertEquals(Collections.EMPTY_LIST,record.getForeignKeyIDs());

        fkIDs = new String[]{"fkid1",""}; //$NON-NLS-1$ //$NON-NLS-2$
        aspect.foreignKeys = Arrays.asList(fkIDs); 
        word   = createUniqueKeyWord(aspect,modelPath);
        record = RecordFactory.createUniqueKeyRecord(word.getWord());
        fkIDs = new String[]{"fkid1"}; //$NON-NLS-1$
        aspect.foreignKeys = Arrays.asList(fkIDs); 
        assertEquals(aspect.foreignKeys,record.getForeignKeyIDs()); 

        fkIDs = new String[]{"fkid1"}; //$NON-NLS-1$
        aspect.foreignKeys = Arrays.asList(fkIDs); 
        word   = createUniqueKeyWord(aspect,modelPath);
        record = RecordFactory.createUniqueKeyRecord(word.getWord());
        assertEquals(aspect.foreignKeys,record.getForeignKeyIDs());

        fkIDs = new String[]{"fkid1","fkid2"}; //$NON-NLS-1$ //$NON-NLS-2$
        aspect.foreignKeys = Arrays.asList(fkIDs); 
        word   = createUniqueKeyWord(aspect,modelPath);
        record = RecordFactory.createUniqueKeyRecord(word.getWord());
        assertEquals(aspect.foreignKeys,record.getForeignKeyIDs());
    }

    public void testFileWord() {
        System.out.println("TestRuntimeAdapter.testFileWord()"); //$NON-NLS-1$

        String filePath = "myprj/myModel"; //$NON-NLS-1$

        WordEntry word = createFileWord(filePath);
        System.out.println("word = "+word); //$NON-NLS-1$
        FileRecord record = RecordFactory.createFileRecord(word.getWord());
        assertNotNull(record.getPathInVdb());
    }

    public void testSplitWordEntryWithBadArgs() {
        System.out.println("TestRuntimeAdapter.testSplitWordEntryWithBadArgs()"); //$NON-NLS-1$
        try {
            RuntimeAdapter.splitWordEntry(null, new WordEntry("abcd".toCharArray()), 50); //$NON-NLS-1$
            fail("Did not throw expected MyExpectedException"); //$NON-NLS-1$
        } catch (AssertionError e) {
            // do nothing � this is what was expected
        }
        
        try {
            RuntimeAdapter.splitWordEntry("", null, 50); //$NON-NLS-1$
            fail("Did not throw expected MyExpectedException"); //$NON-NLS-1$
        } catch (AssertionError e) {
            // do nothing � this is what was expected
        }
        
        try {
            RuntimeAdapter.splitWordEntry("mmuuid:123", new WordEntry("abcd".toCharArray()), 2); //$NON-NLS-1$ //$NON-NLS-2$
            fail("Did not throw expected MyExpectedException"); //$NON-NLS-1$
        } catch (AssertionError e) {
            // do nothing � this is what was expected
        }
    }
    
    public void testSplitWordEntryWithEntrySizeLessThanBlockSize() {
        System.out.println("TestRuntimeAdapter.testSplitWordEntryWithEntrySizeLessThanBlockSize()"); //$NON-NLS-1$
        WordEntry wordEntry = helpCreateWordEntry("mmuuid:123", "abc");  //$NON-NLS-1$//$NON-NLS-2$
        List entries = helpTestSplitWordEntry("mmuuid:123", wordEntry, 20);  //$NON-NLS-1$
        assertEquals(1,entries.size());
    }
    
    public void testSplitWordEntryWithEntrySizeEqualToBlockSizeMinusOne() {
        System.out.println("TestRuntimeAdapter.testSplitWordEntryWithEntrySizeEqualToBlockSizeMinusOne()"); //$NON-NLS-1$
        WordEntry wordEntry = helpCreateWordEntry("mmuuid:123", "abcd");  //$NON-NLS-1$//$NON-NLS-2$
        List entries = helpTestSplitWordEntry("mmuuid:123", wordEntry, 18);  //$NON-NLS-1$
        assertEquals(1,entries.size());
    }
    
    public void testSplitWordEntryWithEntrySizeEqualToBlockSize() {
        System.out.println("TestRuntimeAdapter.testSplitWordEntryWithEntrySizeEqualToBlockSize()"); //$NON-NLS-1$
        WordEntry wordEntry = helpCreateWordEntry("mmuuid:123", "abcde");  //$NON-NLS-1$//$NON-NLS-2$
        List entries = helpTestSplitWordEntry("mmuuid:123", wordEntry, 18);  //$NON-NLS-1$
        assertEquals(2,entries.size());
    }
    
    public void testSplitWordEntryWithEntrySizeEqualToBlockSizePlusOne() {
        System.out.println("TestRuntimeAdapter.testSplitWordEntryWithEntrySizeEqualToBlockSizePlusOne()"); //$NON-NLS-1$
        WordEntry wordEntry = helpCreateWordEntry("mmuuid:123", "abcdef");  //$NON-NLS-1$//$NON-NLS-2$
        List entries = helpTestSplitWordEntry("mmuuid:123", wordEntry, 18);  //$NON-NLS-1$
        assertEquals(3,entries.size());
    }
    
    public void testSplitWordEntryWithEntrySizeGreaterThanBlockSize() {
        System.out.println("TestRuntimeAdapter.testSplitWordEntryWithEntrySizeGreaterThanBlockSize()"); //$NON-NLS-1$
        WordEntry wordEntry = helpCreateWordEntry("mmuuid:123", "abcdefghijklmnopqrstuvwxyz");  //$NON-NLS-1$//$NON-NLS-2$
        List entries = helpTestSplitWordEntry("mmuuid:123", wordEntry, 21);  //$NON-NLS-1$
        assertEquals(6,entries.size());
    }
    
    public void testJoinEntryResultsWithBadArgs() {
        System.out.println("TestRuntimeAdapter.testJoinEntryResultsWithBadArgs()"); //$NON-NLS-1$
        try {
            RecordFactory.joinEntryResults(null, new EntryResult[0], 50); 
            fail("Did not throw expected MyExpectedException"); //$NON-NLS-1$
        } catch (AssertionError e) {
            // do nothing � this is what was expected
        }
    }
    
    public void testJoinEntryResultsWithNoContinuations() {
        System.out.println("TestRuntimeAdapter.testJoinEntryResultsWithNoContinuations()"); //$NON-NLS-1$
        WordEntry wordEntry = helpCreateWordEntry("mmuuid:123", "abc");  //$NON-NLS-1$//$NON-NLS-2$
        List entries = helpTestSplitWordEntry("mmuuid:123", wordEntry, 20);  //$NON-NLS-1$
        assertEquals(1,entries.size());
        
        IEntryResult result = helpTestJoinEntryResults(entries,20);
        assertEquals(wordEntry.getWord().length, result.getWord().length);
    }
    
    public void testJoinEntryResultsWithNoContinuations2() {
        System.out.println("TestRuntimeAdapter.testJoinEntryResultsWithNoContinuations2()"); //$NON-NLS-1$
        WordEntry wordEntry = helpCreateWordEntry("mmuuid:123", "abcd");  //$NON-NLS-1$//$NON-NLS-2$
        List entries = helpTestSplitWordEntry("mmuuid:123", wordEntry, 18);  //$NON-NLS-1$
        assertEquals(1,entries.size());
        
        IEntryResult result = helpTestJoinEntryResults(entries,18);
        assertEquals(wordEntry.getWord().length, result.getWord().length);
    }
    
    public void testJoinEntryResultsWithOneContinuations() {
        System.out.println("TestRuntimeAdapter.testJoinEntryResultsWithOneContinuations()"); //$NON-NLS-1$
        WordEntry wordEntry = helpCreateWordEntry("mmuuid:123", "abcde");  //$NON-NLS-1$//$NON-NLS-2$
        List entries = helpTestSplitWordEntry("mmuuid:123", wordEntry, 18);  //$NON-NLS-1$
        assertEquals(2,entries.size());
        
        IEntryResult result = helpTestJoinEntryResults(entries,18);
        assertEquals(wordEntry.getWord().length, result.getWord().length);
    }
    
    public void testJoinEntryResultsWithTwoContinuations() {
        System.out.println("TestRuntimeAdapter.testJoinEntryResultsWithTwoContinuations()"); //$NON-NLS-1$
        WordEntry wordEntry = helpCreateWordEntry("mmuuid:123", "abcdef");  //$NON-NLS-1$//$NON-NLS-2$
        List entries = helpTestSplitWordEntry("mmuuid:123", wordEntry, 18);  //$NON-NLS-1$
        assertEquals(3,entries.size());
        
        IEntryResult result = helpTestJoinEntryResults(entries,18);
        assertEquals(wordEntry.getWord().length, result.getWord().length);
    }
    
    public void testJoinEntryResultsWith19Continuations() {
        System.out.println("TestRuntimeAdapter.testJoinEntryResultsWith19Continuations()"); //$NON-NLS-1$
        WordEntry wordEntry = helpCreateWordEntry("mmuuid:123", "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz");  //$NON-NLS-1$//$NON-NLS-2$
        List entries = helpTestSplitWordEntry("mmuuid:123", wordEntry, 20);  //$NON-NLS-1$
        assertEquals(20,entries.size());

        // Add a random ordering to the split entries
        Collections.shuffle(entries);
        
        IEntryResult result = helpTestJoinEntryResults(entries,20);
        assertEquals(wordEntry.getWord().length, result.getWord().length);
    }
    
    public void testJoinEntryResultsWith198Continuations() {
        System.out.println("TestRuntimeAdapter.testJoinEntryResultsWith198Continuations()"); //$NON-NLS-1$
        StringBuffer sb = new StringBuffer(1000);
        sb.append("abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz"); //$NON-NLS-1$
        sb.append("abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz"); //$NON-NLS-1$
        sb.append("abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz"); //$NON-NLS-1$
        sb.append("abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz"); //$NON-NLS-1$
        sb.append("abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz"); //$NON-NLS-1$
        sb.append("abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz"); //$NON-NLS-1$
        WordEntry wordEntry = helpCreateWordEntry("mmuuid:123", sb.toString());  //$NON-NLS-1$
        List entries = helpTestSplitWordEntry("mmuuid:123", wordEntry, 20);  //$NON-NLS-1$
        assertEquals(199,entries.size());

        // Add a random ordering to the split entries
        Collections.shuffle(entries);
        
        IEntryResult result = helpTestJoinEntryResults(entries,20);
        assertEquals(wordEntry.getWord().length, result.getWord().length);
    }
    
    public void testExtractUUIDString1() {
        System.out.println("TestRuntimeAdapter.testExtractUUIDString1()"); //$NON-NLS-1$
        
        String uuid = "mmuuid:123"; //$NON-NLS-1$
        String entryStr = uuid;

        IEntryResult entryResult = new EntryResult(entryStr.toCharArray(),new int[0]);
        String result = RecordFactory.extractUUIDString(entryResult);
        assertEquals(uuid,result);
        assertEquals(uuid.length(),result.length());
    }
    
    public void testExtractUUIDString2() {
        System.out.println("TestRuntimeAdapter.testExtractUUIDString2()"); //$NON-NLS-1$
        
        String uuid = "mmuuid:123"; //$NON-NLS-1$
        String entryStr = ""  //$NON-NLS-1$
                        + IndexConstants.RECORD_STRING.RECORD_DELIMITER 
                        + uuid 
                        + IndexConstants.RECORD_STRING.RECORD_DELIMITER 
                        + "abcdefghijklmnopqrstuvwxyz" //$NON-NLS-1$
                        + IndexConstants.RECORD_STRING.RECORD_DELIMITER 
                        + "abcdefghijklmnopqrstuvwxyz"; //$NON-NLS-1$

        IEntryResult entryResult = new EntryResult(entryStr.toCharArray(),new int[0]);
        String result = RecordFactory.extractUUIDString(entryResult);
        assertEquals(uuid,result);
        assertEquals(uuid.length(),result.length());
    }
    
    public void testExtractUUIDString3() {
        System.out.println("TestRuntimeAdapter.testExtractUUIDString3()"); //$NON-NLS-1$
        
        String uuid = "mmuuid:123"; //$NON-NLS-1$
        String entryStr = "A"  //$NON-NLS-1$
                        + IndexConstants.RECORD_STRING.RECORD_DELIMITER 
                        + uuid 
                        + IndexConstants.RECORD_STRING.RECORD_DELIMITER 
                        + "abcdefghijklmnopqrstuvwxyz" //$NON-NLS-1$
                        + IndexConstants.RECORD_STRING.RECORD_DELIMITER 
                        + "abcdefghijklmnopqrstuvwxyz"; //$NON-NLS-1$

        IEntryResult entryResult = new EntryResult(entryStr.toCharArray(),new int[0]);
        String result = RecordFactory.extractUUIDString(entryResult);
        assertEquals(uuid,result);
        assertEquals(uuid.length(),result.length());
    }

//    public void testCreateTransformationWord() {
//        System.out.println("TestRuntimeAdapter.testCreateTransformationWord()"); //$NON-NLS-1$
//        
//        String modelPath = "myprj/myModel/mygroup"; //$NON-NLS-1$        
//        
//        FakeSqlTransformationAspect aspect = new FakeSqlTransformationAspect();
//        WordEntry word = createTransformationWord(aspect,modelPath);
//        System.out.println("word = "+word); //$NON-NLS-1$
//        TransformationRecord record = RuntimeAdapter.createTransformationRecord(word.getWord());
//        assertNull(record.getUUID());
//        assertNotNull(record.getPath());
//    }
     
}
