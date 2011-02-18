/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl.io;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xsd.XSDPackage;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.impl.XSDSchemaImpl;
import org.eclipse.xsd.util.XSDConstants;
import org.jdom.JDOMException;
import com.metamatrix.common.protocol.URLHelper;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.metamodels.wsdl.WsdlPackage;
import com.metamatrix.metamodels.wsdl.http.HttpPackage;
import com.metamatrix.metamodels.wsdl.mime.MimePackage;
import com.metamatrix.metamodels.wsdl.soap.SoapPackage;


/** 
 * @since 4.2
 */
public class TestWsdlHelper extends TestCase {

    /**
     * Constructor for TestWsdlIo.
     * @param name
     */
    public TestWsdlHelper(String name) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    /**
    * Test suite, with one-time setup.
    */
    public static Test suite() {
        TestSuite suite = new TestSuite("TestWsdlHelper"); //$NON-NLS-1$
        suite.addTestSuite(TestWsdlHelper.class);
        // One-time setup and teardown
        return new TestSetup(suite) {
            @Override
            public void setUp() {
                try {
                    // Register the 4 WSDL metamodels ...
                    EPackage.Registry.INSTANCE.put(WsdlPackage.eNS_URI,WsdlPackage.eINSTANCE);
                    EPackage.Registry.INSTANCE.put(MimePackage.eNS_URI,MimePackage.eINSTANCE);
                    EPackage.Registry.INSTANCE.put(HttpPackage.eNS_URI,HttpPackage.eINSTANCE);
                    EPackage.Registry.INSTANCE.put(SoapPackage.eNS_URI,SoapPackage.eINSTANCE);
                    
                    // Register the XSD metamodel ...
                    EPackage.Registry.INSTANCE.put(XSDPackage.eNS_URI,XSDPackage.eINSTANCE);

                    // Load the XSD Schema of Schemas ...
                    final ResourceSet xsdGlobalResourceSet = XSDSchemaImpl.getGlobalResourceSet();
                    final XSDSchema schema1 = XSDSchemaImpl.getSchemaForSchema(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001);
                    final XSDSchema schema2 = XSDSchemaImpl.getSchemaForSchema(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2000_10);
                    final XSDSchema schema3 = XSDSchemaImpl.getSchemaForSchema(XSDConstants.SCHEMA_FOR_SCHEMA_URI_1999);
                    final XSDSchema schema4 = XSDSchemaImpl.getSchemaInstance(XSDConstants.SCHEMA_INSTANCE_URI_2001);
                    final XSDSchema magicSchema1 = XSDSchemaImpl.getMagicSchemaForSchema(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001);
                    final XSDSchema magicSchema2 = XSDSchemaImpl.getMagicSchemaForSchema(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2000_10);
                    final XSDSchema magicSchema3 = XSDSchemaImpl.getMagicSchemaForSchema(XSDConstants.SCHEMA_FOR_SCHEMA_URI_1999);

                    assertNotNull(schema1);
                    assertNotNull(schema2);
                    assertNotNull(schema3);
                    assertNotNull(schema4);
                    assertNotNull(magicSchema1);
                    assertNotNull(magicSchema2);
                    assertNotNull(magicSchema3);
                    
                    final Map uriMap = xsdGlobalResourceSet.getURIConverter().getURIMap();
                    uriMap.put(URI.createURI(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001),schema1.eResource().getURI());
                    uriMap.put(URI.createURI(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2000_10),schema2.eResource().getURI());
                    uriMap.put(URI.createURI(XSDConstants.SCHEMA_FOR_SCHEMA_URI_1999),schema3.eResource().getURI());
                    uriMap.put(URI.createURI(XSDConstants.SCHEMA_INSTANCE_URI_2001),schema4.eResource().getURI());
                    uriMap.put(URI.createURI(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001),magicSchema1.eResource().getURI());
                    uriMap.put(URI.createURI(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2000_10),magicSchema2.eResource().getURI());
                    uriMap.put(URI.createURI(XSDConstants.SCHEMA_FOR_SCHEMA_URI_1999),magicSchema3.eResource().getURI());
                } catch (Throwable err) {
                	fail(err.getMessage());
                }

            }
            @Override
            public void tearDown() {
            }
        };
    }
    

    // =========================================================================
    //                          H E L P E R   M E T H O D S
    // =========================================================================
    
    public String helpParseUrlAbsolute(String url) {
    	return url.substring(0,url.lastIndexOf("/")+1);     //$NON-NLS-1$
    }


   
    
    //===================================================================
    //                             T E S T     C A S E S
    // =========================================================================

    public void testConvertImportsToAbsolutePaths() {
    	URL url = null;
    	try {
            String path = System.getProperty("user.dir").replace('\\', '/'); //$NON-NLS-1$
            if (!path.endsWith("/")) { //$NON-NLS-1$
                path = path + '/';
            }
    		url = new URL("file", "localhost", path + "testdata/HelloService.wsdl"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		} catch (MalformedURLException e) {
			fail(e.getMessage());
		}
		ArrayList arrayList = new ArrayList();
		Map map = new HashMap();
		File file=null;
		try {
			file = URLHelper.createFileFromUrl(url, CoreStringUtil.createFileName(url.getPath()),".wsdl"); //$NON-NLS-1$ 
		} catch (MalformedURLException e) {
			fail(e.getMessage());
		} catch (IOException e) {
			fail(e.getMessage());
		}
		
		try {
			WsdlHelper.convertImportsToAbsolutePaths(file, url.toExternalForm(), arrayList, map, true);
			System.out.print(((File)arrayList.get(0)).getAbsolutePath());
		} catch (JDOMException e) {
			fail(e.getMessage());
		} catch (IOException e) {
			fail(e.getMessage());
		}    	
    }
}

