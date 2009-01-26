/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.xsd.ui.wizards;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.util.XSDResourceFactoryImpl;
import org.eclipse.xsd.util.XSDResourceImpl;

import com.metamatrix.core.util.SmartTestSuite;
import com.metamatrix.modeler.internal.ui.viewsupport.RelationalObjectBuilder;


/** 
 * @since 4.2
 */
public class TestGenerateVirtualFromXsdHelper extends TestCase {
    
    private static final String BT = "com.metamatrix.metamodels.relational.impl.BaseTableImpl";//$NON-NLS-1$
    private static final String COL = "com.metamatrix.metamodels.relational.impl.ColumnImpl";//$NON-NLS-1$
    
    private final String testData = SmartTestSuite.getTestDataPath() + File.separator;
    private final String targetPath =  testData + "Junk.xmi";//$NON-NLS-1$
    private final String DLA1 = testData + "DAASC_214_to_IDE_Schema.xsd"; //$NON-NLS-1$
    private final String DLA2 = testData + "214_DAASC_to_IDE_Schema.xsd"; //$NON-NLS-1$
    private final String BOOKS = testData + "Books.xsd"; //$NON-NLS-1$
    
    
    private MultiStatus status;
    private ResourceSet resourceSet;
    private Resource target;
    
    /**
     * Constructor for TestGenerateVirtualFromXsdHelper.
     * @param name
     */
    public TestGenerateVirtualFromXsdHelper(String name) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();
        GenerateVirtualFromXsdHelper.HEADLESS = true;
        RelationalObjectBuilder.HEADLESS = true;
        status = new MultiStatus("com.metamatrix.modeler.xsd.ui",1, "Testing Result", null); //$NON-NLS-1$//$NON-NLS-2$
        
        resourceSet = new ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl()); //$NON-NLS-1$
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xsd", new XSDResourceFactoryImpl()); //$NON-NLS-1$
        
        target = resourceSet.createResource(URI.createFileURI(targetPath));
    }

    /*
     * @see TestCase#tearDown()
     */
    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        GenerateVirtualFromXsdHelper.HEADLESS = false;
        RelationalObjectBuilder.HEADLESS = false;
        status = null;
        resourceSet = null;
        
        File targetFile = new File(targetPath);
        if(targetFile.exists() ) {
            targetFile.delete();
        }
    }

    /**
     * Test suite, with one-time setup.
     */
    public static Test suite() {
        TestSuite suite = new SmartTestSuite("com.metamatrix.modeler.xsd.ui", "TestGenerateVirtualFromXsdHelper"); //$NON-NLS-1$ //$NON-NLS-2$
        suite.addTestSuite(TestGenerateVirtualFromXsdHelper.class);
        // One-time setup and teardown
        return new TestSetup(suite) {

            @Override
            public void setUp() {
            }

            @Override
            public void tearDown() {
            }
        };
    }

    // =========================================================================
    //                      H E L P E R   M E T H O D S
    // =========================================================================
    private void helpValidateResult(final int expectedTableCount, final int expectedColCount) {
        final StringBuffer msgs = new StringBuffer();
        if(!status.isOK() ) {
            msgs.append("Test failed with problems:");//$NON-NLS-1$
            final IStatus[] children = status.getChildren();
            for (int i = 0; i < children.length; i++) {
                final IStatus status = children[i];
                msgs.append("\n" + status.getMessage() );//$NON-NLS-1$
            } // for
        }
        
        
        final Iterator allContents = target.getAllContents();
        int colCount = 0;
        int tableCount = 0;
        while (allContents.hasNext()) {
            final Object next = allContents.next();
            if(BT.equals(next.getClass().getName() )) {
                tableCount++;
            }else if(COL.equals(next.getClass().getName() )) {
                colCount++;
            }
            
        } // while
        
        if(colCount != expectedColCount) {
            msgs.append("\nCreated wrong number of Columns.  Expected " + expectedColCount + " but got " + colCount); //$NON-NLS-1$ //$NON-NLS-2$
        }
        
        if(tableCount != expectedTableCount) {
            msgs.append("\nCreated wrong number of Tables.  Expected " + expectedTableCount + " but got " + tableCount); //$NON-NLS-1$//$NON-NLS-2$
            
        }
        
        if(msgs.length() > 0) {
            fail(msgs.toString() );
        }
    }
    
    private Collection helpGetTypes(final String modelPath) {
        final ArrayList types = new ArrayList();
        try {
            final XSDResourceImpl rsrc = new XSDResourceImpl(URI.createFileURI(modelPath) );
            rsrc.load(new HashMap() );
            
            final XSDSchema schema = rsrc.getSchema();
            final Iterator contents = schema.getContents().iterator();
            while (contents.hasNext()) {
                final Object next = contents.next();
                if(next instanceof XSDTypeDefinition) {
                    types.add(next);
                }
                
            } // while
        } catch (Exception err) {
            fail("Error loading XSD");//$NON-NLS-1$
        }
        return types;
    }
    
    private Collection helpGetElements(final String modelPath) {
        final ArrayList elements = new ArrayList();
        try {
            final XSDResourceImpl rsrc = new XSDResourceImpl(URI.createFileURI(modelPath) );
            rsrc.load(new HashMap() );
            
            final XSDSchema schema = rsrc.getSchema();
            final Iterator contents = schema.getContents().iterator();
            while (contents.hasNext()) {
                final Object next = contents.next();
                if(next instanceof XSDElementDeclaration) {
                    elements.add(next);
                }
                
            } // while
        } catch (Exception err) {
            fail("Error loading XSD");//$NON-NLS-1$
        }
        return elements;
    }
    
    // =========================================================================
    //                         T E S T     C A S E S
    // =========================================================================
    public void testDLA1Schema() {        
        try {
            final Collection types = helpGetTypes(DLA1);
            final GenerateVirtualFromXsdHelper helper = new GenerateVirtualFromXsdHelper(status, target, types);
            
            helper.doBuild(null);
            
            helpValidateResult(6,226);
        } catch (RuntimeException err) {
            err.printStackTrace();
            fail("Unexpected error");//$NON-NLS-1$
        }
    }
    
    public void testDLA2Schema() {
        try {
            final Collection types = helpGetTypes(DLA2);
            final GenerateVirtualFromXsdHelper helper = new GenerateVirtualFromXsdHelper(status, target, types);
            
            helper.doBuild(null);
            
            helpValidateResult(6,226);
        } catch (RuntimeException err) {
            err.printStackTrace();
            fail("Unexpected error");//$NON-NLS-1$
        }
    }
    
    public void testBooksSchema() {
        try {
            final Collection types = helpGetTypes(BOOKS);
            final GenerateVirtualFromXsdHelper helper = new GenerateVirtualFromXsdHelper(status, target, types);
            
            helper.doBuild(null);
            
            helpValidateResult(10,54);
        } catch (RuntimeException err) {
            err.printStackTrace();
            fail("Unexpected error");//$NON-NLS-1$
        }
    }
    
    public void testDLA1SchemaElements() {        
        try {
            final Collection types = helpGetElements(DLA1);
            final GenerateVirtualFromXsdHelper helper = new GenerateVirtualFromXsdHelper(status, target, types);
            
            helper.doBuild(null);
            
            helpValidateResult(1,98);
        } catch (RuntimeException err) {
            err.printStackTrace();
            fail("Unexpected error");//$NON-NLS-1$
        }
    }
    
    public void testDLA2SchemaElements() {
        try {
            final Collection types = helpGetElements(DLA2);
            final GenerateVirtualFromXsdHelper helper = new GenerateVirtualFromXsdHelper(status, target, types);
            
            helper.doBuild(null);
            
            helpValidateResult(1,98);
        } catch (RuntimeException err) {
            err.printStackTrace();
            fail("Unexpected error");//$NON-NLS-1$
        }
    }
    
    public void testBooksSchemaElements() {
        try {
            final Collection types = helpGetElements(BOOKS);
            final GenerateVirtualFromXsdHelper helper = new GenerateVirtualFromXsdHelper(status, target, types);
            
            helper.doBuild(null);
            
            helpValidateResult(4,34);
        } catch (RuntimeException err) {
            err.printStackTrace();
            fail("Unexpected error");//$NON-NLS-1$
        }
    }
}
