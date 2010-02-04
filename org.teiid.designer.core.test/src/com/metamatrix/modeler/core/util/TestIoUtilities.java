/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import com.metamatrix.core.util.SmartTestSuite;

/**
 * @since 3.1
 * @version 3.1
 * @author <a href="mailto:jverhaeg@metamatrix.com">John P. A. Verhaeg</a>
 */
public class TestIoUtilities extends TestCase {
    //############################################################################################################################
	//# Constants                                                                                                                #
	//############################################################################################################################
    
//    private static final String SAVE_PATH = "saveAs_library.ecore"; //$NON-NLS-1$
    private static final String FILE_PATH = SmartTestSuite.getTestDataPath() + File.separator + "samplePlugin.xml"; //$NON-NLS-1$
    
    //############################################################################################################################
    //# Main                                                                                                                     #
    //############################################################################################################################

	/**
	 * @since 3.1
	 */
	public static void main(final String[] arguments) {
        TestRunner.run(suite());
	}
    
    //############################################################################################################################
    //# Static Methods                                                                                                           #
    //############################################################################################################################

    /**
     * @since 3.1
     */
    public static Test suite() {
        final TestSuite suite = new TestSuite(TestIoUtilities.class);
        return new TestSetup(suite);
    }
    
    //############################################################################################################################
	//# Constructors                                                                                                             #
	//############################################################################################################################
    
    /**
     * @since 3.1
     */
    public TestIoUtilities() {
        this("TestIoUtilities"); //$NON-NLS-1$
    }
    
    /**
	 * @since 3.1
	 */
	public TestIoUtilities(final String name) {
		super(name);
	}

    //############################################################################################################################
	//# Methods                                                                                                                  #
	//############################################################################################################################

//    /**
//     * @since 3.1
//     */
//    public void testLoad()
//    throws IOException {
//        EmfTestUtil.loadSampleMetaModel();
//    }
//
//    /**
//	 * @since 3.1
//	 */
//	public void testSave()
//    throws IOException {
//        final ResourceSet resrcSet = EmfTestUtil.loadSampleMetaModel();
//        final Resource resrc = (Resource)resrcSet.getResources().get(0);
//        IoUtilities.save(resrc, UnitTestUtil.Data.getTestScratchPath() + SAVE_PATH);
//        for (final Iterator iter = resrc.getErrors().iterator();  iter.hasNext();) {
//            System.err.println("Error: " + iter.next()); //$NON-NLS-1$
//        }
//        for (final Iterator iter = resrc.getWarnings().iterator();  iter.hasNext();) {
//            System.err.println("Warning: " + iter.next()); //$NON-NLS-1$
//        }
//	}
    
    public void testGetBytes(){
        File file = new File(FILE_PATH);
        if( !file.exists() ){
            fail("Could not find file at " + FILE_PATH); //$NON-NLS-1$
        }
        
        try {
            byte[] bytes = IoUtilities.getResourceContentsAsByteArray(file);
            if(bytes.length == 0){
                fail("File returned 0 bytes."); //$NON-NLS-1$
            }
        } catch (IOException e) {
           fail("IOException reading " + FILE_PATH); //$NON-NLS-1$
        }
    }
    
    public void testGetBytesFromIS(){
        File file = new File(FILE_PATH);
        if( !file.exists() ){
            fail("Could not find file at " + FILE_PATH); //$NON-NLS-1$
        }

        InputStream is = null;
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException e1) {
            fail("Could not create input stream for file at " + FILE_PATH); //$NON-NLS-1$
        } 
        
        if(is == null){
            fail("Could not create input stream for file at " + FILE_PATH); //$NON-NLS-1$
        }      
        
        try {
            byte[] bytes = IoUtilities.getInputStreamAsByteArray(is, -1);
            if(bytes.length == 0){
                fail("File returned 0 bytes."); //$NON-NLS-1$
            }
        } catch (IOException e) {
           fail("IOException reading " + FILE_PATH); //$NON-NLS-1$
        }
    }

    public void testGetBytesFromIsWithLength(){
        File file = new File(FILE_PATH);
        if( !file.exists() ){
            fail("Could not find file at " + FILE_PATH); //$NON-NLS-1$
        }

        InputStream is = null;
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException e1) {
            fail("Could not create input stream for file at " + FILE_PATH); //$NON-NLS-1$
        } 
        
        if(is == null){
            fail("Could not create input stream for file at " + FILE_PATH); //$NON-NLS-1$
        }      
        
        try {
            byte[] bytes = IoUtilities.getInputStreamAsByteArray(is, 50);
            if(bytes.length != 50){
                fail("Expected 50 byte change, but found " + bytes.length); //$NON-NLS-1$
            }
        } catch (IOException e) {
           fail("IOException reading " + FILE_PATH); //$NON-NLS-1$
        }
    }
        
}
