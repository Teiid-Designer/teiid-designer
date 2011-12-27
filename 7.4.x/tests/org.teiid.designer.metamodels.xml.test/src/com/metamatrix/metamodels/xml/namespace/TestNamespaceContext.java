/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xml.namespace;

import java.util.Iterator;
import java.util.List;
import junit.extensions.TestSetup;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import com.metamatrix.metamodels.xml.XmlDocument;
import com.metamatrix.metamodels.xml.XmlDocumentFactory;
import com.metamatrix.metamodels.xml.XmlElement;
import com.metamatrix.metamodels.xml.XmlNamespace;
import com.metamatrix.metamodels.xml.XmlRoot;
import com.metamatrix.metamodels.xml.impl.XmlDocumentFactoryImpl;

/**
 * TestNamespaceContext
 */
public class TestNamespaceContext extends TestCase {

    private static final String NAMESPACE1_PREFIX   = "nsone"; //$NON-NLS-1$
    private static final String NAMESPACE1_URI      = "http://www.billy.com/schema1"; //$NON-NLS-1$

    private static final String NAMESPACE2_PREFIX   = "nstwp"; //$NON-NLS-1$
    private static final String NAMESPACE2_URI      = "http://www.jane.com/schema2"; //$NON-NLS-1$

    private static final String NAMESPACE3_PREFIX   = "nsthree"; //$NON-NLS-1$
    private static final String NAMESPACE3_URI      = "http://www.kelly.com/schema3"; //$NON-NLS-1$

    private static final String ROOT                = "root"; //$NON-NLS-1$
    private static final String ELEMENT_1           = ROOT + ".element1"; //$NON-NLS-1$
    private static final String ELEMENT_2           = ROOT + ".element2"; //$NON-NLS-1$
    private static final String ELEMENT_3           = ROOT + ".element3"; //$NON-NLS-1$

    private static final String ELEMENT_1_1         = ELEMENT_1 + ".element1"; //$NON-NLS-1$
    private static final String ELEMENT_1_2         = ELEMENT_1 + ".element2"; //$NON-NLS-1$
    private static final String ELEMENT_1_3         = ELEMENT_1 + ".element3"; //$NON-NLS-1$

    private static final String ELEMENT_1_1_1       = ELEMENT_1_1 + ".element1"; //$NON-NLS-1$
    private static final String ELEMENT_1_1_2       = ELEMENT_1_1 + ".element2"; //$NON-NLS-1$
    private static final String ELEMENT_1_1_3       = ELEMENT_1_1 + ".element3"; //$NON-NLS-1$

    private static final String ELEMENT_1_2_1       = ELEMENT_1_2 + ".element1"; //$NON-NLS-1$
    private static final String ELEMENT_1_2_2       = ELEMENT_1_2 + ".element2"; //$NON-NLS-1$
    private static final String ELEMENT_1_2_3       = ELEMENT_1_2 + ".element3"; //$NON-NLS-1$

    private static final String ELEMENT_2_1         = ELEMENT_2 + ".element1"; //$NON-NLS-1$
    private static final String ELEMENT_2_2         = ELEMENT_2 + ".element2"; //$NON-NLS-1$
    private static final String ELEMENT_2_3         = ELEMENT_2 + ".element3"; //$NON-NLS-1$

    private static final String ELEMENT_3_1         = ELEMENT_3 + ".element1"; //$NON-NLS-1$
    private static final String ELEMENT_3_2         = ELEMENT_3 + ".element2"; //$NON-NLS-1$
    private static final String ELEMENT_3_3         = ELEMENT_3 + ".element3"; //$NON-NLS-1$

    private XmlDocumentFactory factory;
    private XmlDocument doc;
    private XmlNamespace ns1;
    private XmlNamespace ns2;
    private XmlNamespace ns3;
    private XmlRoot root;
    private XmlElement element1;
    private XmlElement element11;
    private XmlElement element111;
    private XmlElement element112;
    private XmlElement element113;
    private XmlElement element12;
    private XmlElement element121;
    private XmlElement element122;
    private XmlElement element123;
    private XmlElement element13;
    private XmlElement element2;
    private XmlElement element21;
    private XmlElement element22;
    private XmlElement element23;
    private XmlElement element3;
    private XmlElement element31;
    private XmlElement element32;
    private XmlElement element33;

    /**
     * Constructor for TestNamespaceContext.
     * @param name
     */
    public TestNamespaceContext(String name) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.factory = new XmlDocumentFactoryImpl();
        this.doc = helpCreateDocument(this.factory);

        this.ns1 = factory.createXmlNamespace();
        this.ns1.setPrefix(NAMESPACE1_PREFIX);
        this.ns1.setUri(NAMESPACE1_URI);

        this.ns2 = factory.createXmlNamespace();
        this.ns2.setPrefix(NAMESPACE2_PREFIX);
        this.ns2.setUri(NAMESPACE2_URI);

        this.ns3 = factory.createXmlNamespace();
        this.ns3.setPrefix(NAMESPACE3_PREFIX);
        this.ns3.setUri(NAMESPACE3_URI);

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
        TestSuite suite = new TestSuite("TestNamespaceContext"); //$NON-NLS-1$
        suite.addTestSuite(TestNamespaceContext.class);
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

    public XmlDocument helpCreateDocument(final XmlDocumentFactory factory) {
        this.doc = factory.createXmlDocument();
        
        // Create the root element ...
        root = factory.createXmlRoot();
        root.setName(ROOT);
        doc.setRoot(root);
        
        // Create the root elements ...
        element1 = factory.createXmlElement();
        element1.setName(ELEMENT_1);
        element1.setParent(root);

        element2 = factory.createXmlElement();
        element2.setName(ELEMENT_2);
        element2.setParent(root);

        element3 = factory.createXmlElement();
        element3.setName(ELEMENT_3);
        element3.setParent(root);
    
        // Create the elements under 'element1'
        element11 = factory.createXmlElement();
        element11.setName(ELEMENT_1_1);
        element11.setParent(element1);

        element12 = factory.createXmlElement();
        element12.setName(ELEMENT_1_2);
        element12.setParent(element1);

        element13 = factory.createXmlElement();
        element13.setName(ELEMENT_1_3);
        element13.setParent(element1);
    
        // Create the elements under 'element11'
        element111 = factory.createXmlElement();
        element111.setName(ELEMENT_1_1_1);
        element111.setParent(element11);

        element112 = factory.createXmlElement();
        element112.setName(ELEMENT_1_1_2);
        element112.setParent(element11);

        element113 = factory.createXmlElement();
        element113.setName(ELEMENT_1_1_3);
        element113.setParent(element11);
    
        // Create the elements under 'element12'
        element121 = factory.createXmlElement();
        element121.setName(ELEMENT_1_2_1);
        element121.setParent(element12);

        element122 = factory.createXmlElement();
        element122.setName(ELEMENT_1_2_2);
        element122.setParent(element12);

        element123 = factory.createXmlElement();
        element123.setName(ELEMENT_1_2_3);
        element123.setParent(element12);
    
        // Create the elements under 'element2'
        element21 = factory.createXmlElement();
        element21.setName(ELEMENT_2_1);
        element21.setParent(element2);

        element22 = factory.createXmlElement();
        element22.setName(ELEMENT_2_2);
        element22.setParent(element2);

        element23 = factory.createXmlElement();
        element23.setName(ELEMENT_2_3);
        element23.setParent(element2);
    
        // Create the elements under 'element3'
        element31 = factory.createXmlElement();
        element31.setName(ELEMENT_3_1);
        element31.setParent(element3);

        element32 = factory.createXmlElement();
        element32.setName(ELEMENT_3_2);
        element32.setParent(element3);

        element33 = factory.createXmlElement();
        element33.setName(ELEMENT_3_3);
        element33.setParent(element3);

        return doc;
    }

    /**
     * @param contextElem23
     * @param namespaces
     */
    public void helpCheckAllNamespaces( final NamespaceContext context, final XmlNamespace[] expectedNamespaces) {
        final List all = context.getAllXmlNamespaces();
        Assert.assertEquals(expectedNamespaces.length,all.size());
        
        int index = -1;
        final Iterator iter = all.iterator();
        while (iter.hasNext()) {
            final XmlNamespace actualNamespace = (XmlNamespace)iter.next();
            final XmlNamespace expectedNamespace = expectedNamespaces[++index];
            Assert.assertSame(expectedNamespace,actualNamespace);
        }
    }
    
    /**
     * @param contextElem23
     * @param namespaces
     */
    public void helpCheckNamespaces( final NamespaceContext context, final XmlNamespace[] expectedNamespaces) {
        final List all = context.getXmlNamespaces();
        Assert.assertEquals(expectedNamespaces.length,all.size());
        
        int index = -1;
        final Iterator iter = all.iterator();
        while (iter.hasNext()) {
            final XmlNamespace actualNamespace = (XmlNamespace)iter.next();
            Assert.assertSame(expectedNamespaces[++index],actualNamespace);
        }
    }
    // =========================================================================
    //                         T E S T     C A S E S
    // =========================================================================

//    public void testDataPathSupplied() {
//        Assert.assertTestDataPathSet();
//    }

    public void testSetup() {
        Assert.assertNotNull(this.doc);
        Assert.assertNotNull(this.ns1);
        Assert.assertNotNull(this.ns2);
        Assert.assertNotNull(this.ns3);
        Assert.assertNotNull(this.root);
        Assert.assertNotNull(this.element1);
        Assert.assertNotNull(this.element11);
        Assert.assertNotNull(this.element111);
        Assert.assertNotNull(this.element112);
        Assert.assertNotNull(this.element113);
        Assert.assertNotNull(this.element12);
        Assert.assertNotNull(this.element121);
        Assert.assertNotNull(this.element122);
        Assert.assertNotNull(this.element123);
        Assert.assertNotNull(this.element2);
        Assert.assertNotNull(this.element21);
        Assert.assertNotNull(this.element22);
        Assert.assertNotNull(this.element23);
        Assert.assertNotNull(this.element3);
        Assert.assertNotNull(this.element31);
        Assert.assertNotNull(this.element32);
        Assert.assertNotNull(this.element33);
    }
    
    public void testAddingNamespaces1() {
        root.getDeclaredNamespaces().add(this.ns1);
        root.getDeclaredNamespaces().add(this.ns2);
        element11.getDeclaredNamespaces().add(this.ns3);
        
        final NamespaceContext contextRoot    = new NamespaceContext(this.root,null);
        final NamespaceContext contextElem1   = new NamespaceContext(this.element1,contextRoot);
        final NamespaceContext contextElem11  = new NamespaceContext(this.element11,contextElem1);
        final NamespaceContext contextElem111 = new NamespaceContext(this.element111,contextElem11);
        final NamespaceContext contextElem112 = new NamespaceContext(this.element112,contextElem11);
        final NamespaceContext contextElem113 = new NamespaceContext(this.element113,contextElem11);
        final NamespaceContext contextElem12  = new NamespaceContext(this.element12,contextElem1);
        final NamespaceContext contextElem121 = new NamespaceContext(this.element121,contextElem12);
        final NamespaceContext contextElem122 = new NamespaceContext(this.element122,contextElem12);
        final NamespaceContext contextElem123 = new NamespaceContext(this.element123,contextElem12);
        final NamespaceContext contextElem2   = new NamespaceContext(this.element2,contextRoot);
        final NamespaceContext contextElem21  = new NamespaceContext(this.element21,contextElem2);
        final NamespaceContext contextElem22  = new NamespaceContext(this.element22,contextElem21);
        final NamespaceContext contextElem23  = new NamespaceContext(this.element23,contextElem21);

        // Get the namespaces ...
        helpCheckNamespaces(contextRoot,   new XmlNamespace[]{this.ns1,this.ns2});
        helpCheckNamespaces(contextElem1,  new XmlNamespace[]{});
        helpCheckNamespaces(contextElem11, new XmlNamespace[]{this.ns3});
        helpCheckNamespaces(contextElem111,new XmlNamespace[]{});
        helpCheckNamespaces(contextElem112,new XmlNamespace[]{});
        helpCheckNamespaces(contextElem113,new XmlNamespace[]{});
        helpCheckNamespaces(contextElem12, new XmlNamespace[]{});
        helpCheckNamespaces(contextElem121,new XmlNamespace[]{});
        helpCheckNamespaces(contextElem122,new XmlNamespace[]{});
        helpCheckNamespaces(contextElem123,new XmlNamespace[]{});
        helpCheckNamespaces(contextElem2,  new XmlNamespace[]{});
        helpCheckNamespaces(contextElem21, new XmlNamespace[]{});
        helpCheckNamespaces(contextElem22, new XmlNamespace[]{});
        helpCheckNamespaces(contextElem23, new XmlNamespace[]{});

        // Get all the namespaces ...
        helpCheckAllNamespaces(contextRoot,   new XmlNamespace[]{this.ns1,this.ns2});
        helpCheckAllNamespaces(contextElem1,  new XmlNamespace[]{this.ns1,this.ns2});
        helpCheckAllNamespaces(contextElem11, new XmlNamespace[]{this.ns3,this.ns1,this.ns2});
        helpCheckAllNamespaces(contextElem111,new XmlNamespace[]{this.ns3,this.ns1,this.ns2});
        helpCheckAllNamespaces(contextElem112,new XmlNamespace[]{this.ns3,this.ns1,this.ns2});
        helpCheckAllNamespaces(contextElem113,new XmlNamespace[]{this.ns3,this.ns1,this.ns2});
        helpCheckAllNamespaces(contextElem12, new XmlNamespace[]{this.ns1,this.ns2});
        helpCheckAllNamespaces(contextElem121,new XmlNamespace[]{this.ns1,this.ns2});
        helpCheckAllNamespaces(contextElem122,new XmlNamespace[]{this.ns1,this.ns2});
        helpCheckAllNamespaces(contextElem123,new XmlNamespace[]{this.ns1,this.ns2});
        helpCheckAllNamespaces(contextElem2,  new XmlNamespace[]{this.ns1,this.ns2});
        helpCheckAllNamespaces(contextElem21, new XmlNamespace[]{this.ns1,this.ns2});
        helpCheckAllNamespaces(contextElem22, new XmlNamespace[]{this.ns1,this.ns2});
        helpCheckAllNamespaces(contextElem23, new XmlNamespace[]{this.ns1,this.ns2});
    }

    public void testAddingNamespaces2() {
        element1.getDeclaredNamespaces().add(this.ns1);
        element11.getDeclaredNamespaces().add(this.ns2);
        element12.getDeclaredNamespaces().add(this.ns3);
        
        final NamespaceContext contextRoot    = new NamespaceContext(this.root,null);
        final NamespaceContext contextElem1   = new NamespaceContext(this.element1,contextRoot);
        final NamespaceContext contextElem11  = new NamespaceContext(this.element11,contextElem1);
        final NamespaceContext contextElem111 = new NamespaceContext(this.element111,contextElem11);
        final NamespaceContext contextElem112 = new NamespaceContext(this.element112,contextElem11);
        final NamespaceContext contextElem113 = new NamespaceContext(this.element113,contextElem11);
        final NamespaceContext contextElem12  = new NamespaceContext(this.element12,contextElem1);
        final NamespaceContext contextElem121 = new NamespaceContext(this.element121,contextElem12);
        final NamespaceContext contextElem122 = new NamespaceContext(this.element122,contextElem12);
        final NamespaceContext contextElem123 = new NamespaceContext(this.element123,contextElem12);
        final NamespaceContext contextElem2   = new NamespaceContext(this.element2,contextRoot);
        final NamespaceContext contextElem21  = new NamespaceContext(this.element21,contextElem2);
        final NamespaceContext contextElem22  = new NamespaceContext(this.element22,contextElem21);
        final NamespaceContext contextElem23  = new NamespaceContext(this.element23,contextElem21);

        // Get the namespaces ...
        helpCheckNamespaces(contextRoot,   new XmlNamespace[]{});
        helpCheckNamespaces(contextElem1,  new XmlNamespace[]{this.ns1});
        helpCheckNamespaces(contextElem11, new XmlNamespace[]{this.ns2});
        helpCheckNamespaces(contextElem111,new XmlNamespace[]{});
        helpCheckNamespaces(contextElem112,new XmlNamespace[]{});
        helpCheckNamespaces(contextElem113,new XmlNamespace[]{});
        helpCheckNamespaces(contextElem12, new XmlNamespace[]{this.ns3});
        helpCheckNamespaces(contextElem121,new XmlNamespace[]{});
        helpCheckNamespaces(contextElem122,new XmlNamespace[]{});
        helpCheckNamespaces(contextElem123,new XmlNamespace[]{});
        helpCheckNamespaces(contextElem2,  new XmlNamespace[]{});
        helpCheckNamespaces(contextElem21, new XmlNamespace[]{});
        helpCheckNamespaces(contextElem22, new XmlNamespace[]{});
        helpCheckNamespaces(contextElem23, new XmlNamespace[]{});

        // Get all the namespaces ...
        helpCheckAllNamespaces(contextRoot,   new XmlNamespace[]{});
        helpCheckAllNamespaces(contextElem1,  new XmlNamespace[]{this.ns1});
        helpCheckAllNamespaces(contextElem11, new XmlNamespace[]{this.ns2,this.ns1});
        helpCheckAllNamespaces(contextElem111,new XmlNamespace[]{this.ns2,this.ns1});
        helpCheckAllNamespaces(contextElem112,new XmlNamespace[]{this.ns2,this.ns1});
        helpCheckAllNamespaces(contextElem113,new XmlNamespace[]{this.ns2,this.ns1});
        helpCheckAllNamespaces(contextElem12, new XmlNamespace[]{this.ns3,this.ns1});
        helpCheckAllNamespaces(contextElem121,new XmlNamespace[]{this.ns3,this.ns1});
        helpCheckAllNamespaces(contextElem122,new XmlNamespace[]{this.ns3,this.ns1});
        helpCheckAllNamespaces(contextElem123,new XmlNamespace[]{this.ns3,this.ns1});
        helpCheckAllNamespaces(contextElem2,  new XmlNamespace[]{});
        helpCheckAllNamespaces(contextElem21, new XmlNamespace[]{});
        helpCheckAllNamespaces(contextElem22, new XmlNamespace[]{});
        helpCheckAllNamespaces(contextElem23, new XmlNamespace[]{});
    }

    public void testAddingNamespaces3() {
        element1.getDeclaredNamespaces().add(this.ns1);
        element11.getDeclaredNamespaces().add(this.ns2);
        element2.getDeclaredNamespaces().add(this.ns3);
        
        final NamespaceContext contextRoot    = new NamespaceContext(this.root,null);
        final NamespaceContext contextElem1   = new NamespaceContext(this.element1,contextRoot);
        final NamespaceContext contextElem11  = new NamespaceContext(this.element11,contextElem1);
        final NamespaceContext contextElem111 = new NamespaceContext(this.element111,contextElem11);
        final NamespaceContext contextElem112 = new NamespaceContext(this.element112,contextElem11);
        final NamespaceContext contextElem113 = new NamespaceContext(this.element113,contextElem11);
        final NamespaceContext contextElem12  = new NamespaceContext(this.element12,contextElem1);
        final NamespaceContext contextElem121 = new NamespaceContext(this.element121,contextElem12);
        final NamespaceContext contextElem122 = new NamespaceContext(this.element122,contextElem12);
        final NamespaceContext contextElem123 = new NamespaceContext(this.element123,contextElem12);
        final NamespaceContext contextElem2   = new NamespaceContext(this.element2,contextRoot);
        final NamespaceContext contextElem21  = new NamespaceContext(this.element21,contextElem2);
        final NamespaceContext contextElem22  = new NamespaceContext(this.element22,contextElem21);
        final NamespaceContext contextElem23  = new NamespaceContext(this.element23,contextElem21);

        // Get the namespaces ...
        helpCheckNamespaces(contextRoot,   new XmlNamespace[]{});
        helpCheckNamespaces(contextElem1,  new XmlNamespace[]{this.ns1});
        helpCheckNamespaces(contextElem11, new XmlNamespace[]{this.ns2});
        helpCheckNamespaces(contextElem111,new XmlNamespace[]{});
        helpCheckNamespaces(contextElem112,new XmlNamespace[]{});
        helpCheckNamespaces(contextElem113,new XmlNamespace[]{});
        helpCheckNamespaces(contextElem12, new XmlNamespace[]{});
        helpCheckNamespaces(contextElem121,new XmlNamespace[]{});
        helpCheckNamespaces(contextElem122,new XmlNamespace[]{});
        helpCheckNamespaces(contextElem123,new XmlNamespace[]{});
        helpCheckNamespaces(contextElem2,  new XmlNamespace[]{this.ns3});
        helpCheckNamespaces(contextElem21, new XmlNamespace[]{});
        helpCheckNamespaces(contextElem22, new XmlNamespace[]{});
        helpCheckNamespaces(contextElem23, new XmlNamespace[]{});

        // Get all the namespaces ...
        helpCheckAllNamespaces(contextRoot,   new XmlNamespace[]{});
        helpCheckAllNamespaces(contextElem1,  new XmlNamespace[]{this.ns1});
        helpCheckAllNamespaces(contextElem11, new XmlNamespace[]{this.ns2,this.ns1});
        helpCheckAllNamespaces(contextElem111,new XmlNamespace[]{this.ns2,this.ns1});
        helpCheckAllNamespaces(contextElem112,new XmlNamespace[]{this.ns2,this.ns1});
        helpCheckAllNamespaces(contextElem113,new XmlNamespace[]{this.ns2,this.ns1});
        helpCheckAllNamespaces(contextElem12, new XmlNamespace[]{this.ns1});
        helpCheckAllNamespaces(contextElem121,new XmlNamespace[]{this.ns1});
        helpCheckAllNamespaces(contextElem122,new XmlNamespace[]{this.ns1});
        helpCheckAllNamespaces(contextElem123,new XmlNamespace[]{this.ns1});
        helpCheckAllNamespaces(contextElem2,  new XmlNamespace[]{this.ns3});
        helpCheckAllNamespaces(contextElem21, new XmlNamespace[]{this.ns3});
        helpCheckAllNamespaces(contextElem22, new XmlNamespace[]{this.ns3});
        helpCheckAllNamespaces(contextElem23, new XmlNamespace[]{this.ns3});
    }

    public void testAddingNamespaces4() {
        final NamespaceContext contextRoot    = new NamespaceContext(this.root,null);
        final NamespaceContext contextElem1   = new NamespaceContext(this.element1,contextRoot);
        final NamespaceContext contextElem11  = new NamespaceContext(this.element11,contextElem1);
        final NamespaceContext contextElem111 = new NamespaceContext(this.element111,contextElem11);
        final NamespaceContext contextElem112 = new NamespaceContext(this.element112,contextElem11);
        final NamespaceContext contextElem113 = new NamespaceContext(this.element113,contextElem11);
        final NamespaceContext contextElem12  = new NamespaceContext(this.element12,contextElem1);
        final NamespaceContext contextElem121 = new NamespaceContext(this.element121,contextElem12);
        final NamespaceContext contextElem122 = new NamespaceContext(this.element122,contextElem12);
        final NamespaceContext contextElem123 = new NamespaceContext(this.element123,contextElem12);
        final NamespaceContext contextElem2   = new NamespaceContext(this.element2,contextRoot);
        final NamespaceContext contextElem21  = new NamespaceContext(this.element21,contextElem2);
        final NamespaceContext contextElem22  = new NamespaceContext(this.element22,contextElem21);
        final NamespaceContext contextElem23  = new NamespaceContext(this.element23,contextElem21);

        // Get the namespaces ...
        helpCheckNamespaces(contextRoot,   new XmlNamespace[]{});
        helpCheckNamespaces(contextElem1,  new XmlNamespace[]{});
        helpCheckNamespaces(contextElem11, new XmlNamespace[]{});
        helpCheckNamespaces(contextElem111,new XmlNamespace[]{});
        helpCheckNamespaces(contextElem112,new XmlNamespace[]{});
        helpCheckNamespaces(contextElem113,new XmlNamespace[]{});
        helpCheckNamespaces(contextElem12, new XmlNamespace[]{});
        helpCheckNamespaces(contextElem121,new XmlNamespace[]{});
        helpCheckNamespaces(contextElem122,new XmlNamespace[]{});
        helpCheckNamespaces(contextElem123,new XmlNamespace[]{});
        helpCheckNamespaces(contextElem2,  new XmlNamespace[]{});
        helpCheckNamespaces(contextElem21, new XmlNamespace[]{});
        helpCheckNamespaces(contextElem22, new XmlNamespace[]{});
        helpCheckNamespaces(contextElem23, new XmlNamespace[]{});

        // Get all the namespaces ...
        helpCheckAllNamespaces(contextRoot,   new XmlNamespace[]{});
        helpCheckAllNamespaces(contextElem1,  new XmlNamespace[]{});
        helpCheckAllNamespaces(contextElem11, new XmlNamespace[]{});
        helpCheckAllNamespaces(contextElem111,new XmlNamespace[]{});
        helpCheckAllNamespaces(contextElem112,new XmlNamespace[]{});
        helpCheckAllNamespaces(contextElem113,new XmlNamespace[]{});
        helpCheckAllNamespaces(contextElem12, new XmlNamespace[]{});
        helpCheckAllNamespaces(contextElem121,new XmlNamespace[]{});
        helpCheckAllNamespaces(contextElem122,new XmlNamespace[]{});
        helpCheckAllNamespaces(contextElem123,new XmlNamespace[]{});
        helpCheckAllNamespaces(contextElem2,  new XmlNamespace[]{});
        helpCheckAllNamespaces(contextElem21, new XmlNamespace[]{});
        helpCheckAllNamespaces(contextElem22, new XmlNamespace[]{});
        helpCheckAllNamespaces(contextElem23, new XmlNamespace[]{});
    }

    public void testAddingNamespaces5() {
        final NamespaceContext contextRoot    = new NamespaceContext(this.root,null);
        final NamespaceContext contextElem1   = new NamespaceContext(this.element1,contextRoot);
        final NamespaceContext contextElem11  = new NamespaceContext(this.element11,contextElem1);
        final NamespaceContext contextElem111 = new NamespaceContext(this.element111,contextElem11);
        final NamespaceContext contextElem112 = new NamespaceContext(this.element112,contextElem11);
        final NamespaceContext contextElem113 = new NamespaceContext(this.element113,contextElem11);
        final NamespaceContext contextElem12  = new NamespaceContext(this.element12,contextElem1);
        final NamespaceContext contextElem121 = new NamespaceContext(this.element121,contextElem12);
        final NamespaceContext contextElem122 = new NamespaceContext(this.element122,contextElem12);
        final NamespaceContext contextElem123 = new NamespaceContext(this.element123,contextElem12);
        final NamespaceContext contextElem2   = new NamespaceContext(this.element2,contextRoot);
        final NamespaceContext contextElem21  = new NamespaceContext(this.element21,contextElem2);
        final NamespaceContext contextElem22  = new NamespaceContext(this.element22,contextElem21);
        final NamespaceContext contextElem23  = new NamespaceContext(this.element23,contextElem21);

        contextElem1.addXmlNamespace(this.ns1);
        contextElem11.addXmlNamespace(this.ns2);
        contextElem2.addXmlNamespace(this.ns3);
        

        // Get the namespaces ...
        helpCheckNamespaces(contextRoot,   new XmlNamespace[]{});
        helpCheckNamespaces(contextElem1,  new XmlNamespace[]{this.ns1});
        helpCheckNamespaces(contextElem11, new XmlNamespace[]{this.ns2});
        helpCheckNamespaces(contextElem111,new XmlNamespace[]{});
        helpCheckNamespaces(contextElem112,new XmlNamespace[]{});
        helpCheckNamespaces(contextElem113,new XmlNamespace[]{});
        helpCheckNamespaces(contextElem12, new XmlNamespace[]{});
        helpCheckNamespaces(contextElem121,new XmlNamespace[]{});
        helpCheckNamespaces(contextElem122,new XmlNamespace[]{});
        helpCheckNamespaces(contextElem123,new XmlNamespace[]{});
        helpCheckNamespaces(contextElem2,  new XmlNamespace[]{this.ns3});
        helpCheckNamespaces(contextElem21, new XmlNamespace[]{});
        helpCheckNamespaces(contextElem22, new XmlNamespace[]{});
        helpCheckNamespaces(contextElem23, new XmlNamespace[]{});

        // Get all the namespaces ...
        helpCheckAllNamespaces(contextRoot,   new XmlNamespace[]{});
        helpCheckAllNamespaces(contextElem1,  new XmlNamespace[]{this.ns1});
        helpCheckAllNamespaces(contextElem11, new XmlNamespace[]{this.ns2,this.ns1});
        helpCheckAllNamespaces(contextElem111,new XmlNamespace[]{this.ns2,this.ns1});
        helpCheckAllNamespaces(contextElem112,new XmlNamespace[]{this.ns2,this.ns1});
        helpCheckAllNamespaces(contextElem113,new XmlNamespace[]{this.ns2,this.ns1});
        helpCheckAllNamespaces(contextElem12, new XmlNamespace[]{this.ns1});
        helpCheckAllNamespaces(contextElem121,new XmlNamespace[]{this.ns1});
        helpCheckAllNamespaces(contextElem122,new XmlNamespace[]{this.ns1});
        helpCheckAllNamespaces(contextElem123,new XmlNamespace[]{this.ns1});
        helpCheckAllNamespaces(contextElem2,  new XmlNamespace[]{this.ns3});
        helpCheckAllNamespaces(contextElem21, new XmlNamespace[]{this.ns3});
        helpCheckAllNamespaces(contextElem22, new XmlNamespace[]{this.ns3});
        helpCheckAllNamespaces(contextElem23, new XmlNamespace[]{this.ns3});
    }

}
