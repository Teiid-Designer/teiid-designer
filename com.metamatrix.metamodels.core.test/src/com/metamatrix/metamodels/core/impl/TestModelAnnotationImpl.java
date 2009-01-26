/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.core.impl;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * XsdUtilTest
 */
public class TestModelAnnotationImpl extends TestCase {    

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
     * Constructor for TestMetadataLoadingCache.
     * @param name
     */
    public TestModelAnnotationImpl(String name) {
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
        TestSuite suite = new TestSuite("TestModelAnnotationImpl"); //$NON-NLS-1$
        suite.addTestSuite(TestModelAnnotationImpl.class);
    
        return new TestSetup(suite) { // junit.extensions package
            // One-time setup and teardown
            @Override
            public void setUp() throws Exception {
            }
            @Override
            public void tearDown() {
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
    
    /**
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
    }
    
    /**
     * @see TestCase#tearDown()
     */
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
    
    
    // =========================================================================
    //                         T E S T   C A S E S
    // =========================================================================
    
    public void testDefaults() {
        final ModelAnnotationImpl ma = new ModelAnnotationImpl();
        final StringBuffer result = new StringBuffer();
        
        if(ma.getMaxSetSize() != ModelAnnotationImpl.MAX_SET_SIZE_EDEFAULT) {
            result.append("\nMax criteria set size was not correct"); //$NON-NLS-1$
        }
        
        if(ma.getDescription() != ModelAnnotationImpl.DESCRIPTION_EDEFAULT) {
            result.append("\nDescription default was not correct"); //$NON-NLS-1$
        }
        
        if(ma.getModelType() != ModelAnnotationImpl.MODEL_TYPE_EDEFAULT) {
            result.append("\nModel Type default was not correct"); //$NON-NLS-1$
        }
        
        if(ma.getNameInSource() != ModelAnnotationImpl.NAME_IN_SOURCE_EDEFAULT) {
            result.append("\nName In Source default was not correct"); //$NON-NLS-1$
        }
        
        if(ma.getNamespaceUri() != ModelAnnotationImpl.NAMESPACE_URI_EDEFAULT) {
            result.append("\nNamespace URI default was not correct"); //$NON-NLS-1$
        }
        
        if(ma.getPrimaryMetamodelUri() != ModelAnnotationImpl.PRIMARY_METAMODEL_URI_EDEFAULT) {
            result.append("\nPrimary Metamodel URI default was not correct"); //$NON-NLS-1$
        }
        
        if(ma.getProducerName() != ModelAnnotationImpl.PRODUCER_NAME_EDEFAULT) {
            result.append("\nProducer Name default was not correct"); //$NON-NLS-1$
        }
        
        if(ma.getProducerVersion() != ModelAnnotationImpl.PRODUCER_VERSION_EDEFAULT) {
            result.append("\nProducer Version default was not correct"); //$NON-NLS-1$
        }
        
        
        if(result.length() > 0) {
            fail("Test Results failed:" + result.toString() ); //$NON-NLS-1$
        }
    }
    
}
