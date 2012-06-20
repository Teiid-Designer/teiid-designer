/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.viewsupport;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.teiid.core.util.SmartTestDesignerSuite;
import org.teiid.designer.core.workspace.FakeIFile;
import org.teiid.designer.core.workspace.FakeIResource;
import org.teiid.designer.core.workspace.MockFileResource;

/** 
 * @since 5.0.1
 */
public class TestModelUtilities extends TestCase {
    
    public static Test suite() {
        return new TestSuite(TestModelUtilities.class);
    }
    
    /**
     * Constructor for TestModelUtilities.
     * @param theName the test name
     */
    public TestModelUtilities(String theName) {
        super(theName);
    }
    
    /**
     * Make sure null input throws exception
     * @since 5.0.1
     */
    public void testIsModelingRelatedFile1() {
        try {
            ModelUtilities.isModelingRelatedFile(null);
            fail("Null input did not throw exception"); //$NON-NLS-1$
        } catch (IllegalArgumentException theException) {
            // successfull test
        } catch (Exception theException) {
            fail("Wrong type of exception thrown"); //$NON-NLS-1$
        }
    }
    
    /**
     * Make sure objects that aren't IFiles return false. 
     * @since 5.0.1
     */
    public void testIsModelingRelatedFile2() {
        IResource nonFile = new FakeIResource("temp"); //$NON-NLS-1$
        assertFalse("Resource was not an IFile", ModelUtilities.isModelingRelatedFile(nonFile)); //$NON-NLS-1$
    }
    
    /**
     * Make sure an IFile that is not a modeling related file returns false. 
     * @since 5.0.1
     */
    public void testIsModelingRelatedFile3() {
        IFile nonModelFile = new FakeIFile("temp"); //$NON-NLS-1$
        assertFalse("Resource was not a modeling-related file", ModelUtilities.isModelingRelatedFile(nonModelFile)); //$NON-NLS-1$
    }
    
    /**
     * Make sure WSDLs returns true. 
     * @since 5.0.1
     */
    public void testIsModelingRelatedFile4() {
        IFile wsdlFile = new FakeIFile("temp.wsdl"); //$NON-NLS-1$
        assertTrue("WSDL was not considered a modeling-related file", ModelUtilities.isModelingRelatedFile(wsdlFile)); //$NON-NLS-1$
    }
    
    /**
     * Make sure VDBs returns true. 
     * @since 5.0.1
     */
    public void testIsModelingRelatedFile5() {
        IFile vdbFile = new FakeIFile("temp.vdb"); //$NON-NLS-1$
        assertTrue("VDB was not considered a modeling-related file", ModelUtilities.isModelingRelatedFile(vdbFile)); //$NON-NLS-1$
    }
    
    /**
     * Make sure XSDs returns true. 
     * @since 5.0.1
     */
    public void testIsModelingRelatedFile6() {
        File xsdFile = SmartTestDesignerSuite.getTestDataFile(getClass(), "projects/Books Project/Books.xsd"); //$NON-NLS-1$
        IResource xsdResource = buildModelResource(xsdFile);
        assertTrue("XSD was not considered a modeling-related file", ModelUtilities.isModelingRelatedFile(xsdResource)); //$NON-NLS-1$
    }    

    /**
     * Make sure models returns true. 
     * @since 5.0.1
     */
    public void testIsModelingRelatedFile7() {
        File modelFile = SmartTestDesignerSuite.getTestDataFile(getClass(), "projects/Books Project/Books_Oracle.xmi"); //$NON-NLS-1$
        IResource modelResource = buildModelResource(modelFile);
        assertTrue("Model was not considered a modeling-related file", ModelUtilities.isModelingRelatedFile(modelResource)); //$NON-NLS-1$
    }    

    /**
     * Helper method to turn a <code>java.io.File</code> into an <code>IResource</code> that is within a model project.
     * @param theFile the file being converted
     * @return the resource
     * @since 5.0.1
     */
    private IResource buildModelResource(File theFile) {
        MockFileResource resource = new MockFileResource(theFile);
        resource.setModelNature(true);
        
        return resource;
    }

}
