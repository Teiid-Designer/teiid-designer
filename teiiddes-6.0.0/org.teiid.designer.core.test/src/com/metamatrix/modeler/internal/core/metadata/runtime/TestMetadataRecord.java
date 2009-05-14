/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.metadata.runtime;

import org.eclipse.core.runtime.Path;

import com.metamatrix.metadata.runtime.impl.ColumnRecordImpl;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestMetadataRecord extends TestCase {

    // =========================================================================
    //                        F R A M E W O R K
    // =========================================================================
    
    /**
     * Constructor for TestMetadataRecord.
     * @param name
     */
    public TestMetadataRecord(String name) {
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
        TestSuite suite = new TestSuite("TestMetadataRecord"); //$NON-NLS-1$
        suite.addTestSuite(TestMetadataRecord.class);
        return new TestSetup(suite);
    }
    
    public void testNullFullName() {
        ColumnRecordImpl record = new ColumnRecordImpl();
        assertEquals(null, record.getFullName());
        assertEquals(null, record.getName());
        assertEquals(null, record.getModelName());
        assertEquals(null, record.getPathString());
        assertEquals(null, record.getPath());
    }
    
    public void testNullFullNameNoNullName() {
        ColumnRecordImpl record = new ColumnRecordImpl();
        String name = "c"; //$NON-NLS-1$
        record.setName(name);
        assertEquals(name, record.getFullName());
        assertEquals(name, record.getName());
        assertEquals(name, record.getModelName());
        assertEquals(name, record.getPathString());
        assertEquals(new Path(record.getPathString()), new Path(record.getPath()));
    }
    
    public void testNoNullFullNameNoNullName() {
        ColumnRecordImpl record = new ColumnRecordImpl();
        String name = "c"; //$NON-NLS-1$
        record.setName(name);
        String fullName = "a.b.c"; //$NON-NLS-1$
        record.setFullName(fullName);        
        assertEquals(fullName, record.getFullName());
        assertEquals(name, record.getName());
        assertEquals("a", record.getModelName()); //$NON-NLS-1$
        assertEquals("a/b/c", record.getPathString()); //$NON-NLS-1$
        assertEquals(new Path(record.getPathString()), new Path(record.getPath()));
    }    

    public void testNullName() {
        ColumnRecordImpl record = new ColumnRecordImpl();
        String fullName = "a.b.c"; //$NON-NLS-1$
        record.setFullName(fullName);
        assertEquals(fullName, record.getFullName());
        assertEquals("c", record.getName()); //$NON-NLS-1$
        assertEquals("a", record.getModelName()); //$NON-NLS-1$
        assertEquals("a/b/c", record.getPathString()); //$NON-NLS-1$
        assertEquals(new Path(record.getPathString()), new Path(record.getPath()));
    }
}
