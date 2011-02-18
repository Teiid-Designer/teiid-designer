/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.webservice.ui.wizard;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import com.metamatrix.common.protocol.URLHelper;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.modeler.internal.webservice.ui.wizard.WsdlSelectionPage;

/**
 * @since 4.2
 */
public class TestWsdlSelectionPage extends TestCase {

    final static String XSD_SUFFIX = ".xsd"; //$NON-NLS-1$

    List urlList = new ArrayList();

    /**
     * Constructor for TestBasicWsdlGenerator.
     * 
     * @param name
     */
    public TestWsdlSelectionPage( String name ) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {

    }

    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    // =========================================================================
    // H E L P E R M E T H O D S
    // =========================================================================

    // =========================================================================
    // T E S T C A S E S
    // =========================================================================

    public void testSetup() {
    }

    /**
     * Defect 24097 - test for wsdl urls with preceding slash(es).
     */
    public void testFormatPath() throws Exception {
        helpTestFormatPath(""); //$NON-NLS-1$  
        helpTestFormatPath("/"); //$NON-NLS-1$  
        helpTestFormatPath("/////"); //$NON-NLS-1$
    }

    public void helpTestFormatPath( String pathSegment ) throws Exception {
        String path = System.getProperty("user.dir").replace('\\', '/'); //$NON-NLS-1$
        if (!path.endsWith("/")) { //$NON-NLS-1$
            path = path + '/';
        }
        URL newUrl = new URL("file", "", path + pathSegment + "testdata/HelloService.wsdl"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        String formattedPath = WsdlSelectionPage.formatPath(newUrl);
        File wsdlFile = URLHelper.createFileFromUrl(newUrl, CoreStringUtil.createFileName(formattedPath), XSD_SUFFIX);
        String name = wsdlFile.getName();

        if (name.startsWith("_")) { //$NON-NLS-1$
            fail("File name starts with an underscore."); //$NON-NLS-1$
        }
    }
}
