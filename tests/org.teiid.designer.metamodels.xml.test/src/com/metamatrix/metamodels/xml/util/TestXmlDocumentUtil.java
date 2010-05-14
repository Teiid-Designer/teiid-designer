/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xml.util;

import junit.extensions.TestSetup;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import com.metamatrix.metamodels.xml.ProcessingInstruction;
import com.metamatrix.metamodels.xml.XmlAttribute;
import com.metamatrix.metamodels.xml.XmlComment;
import com.metamatrix.metamodels.xml.XmlDocument;
import com.metamatrix.metamodels.xml.XmlDocumentEntity;
import com.metamatrix.metamodels.xml.XmlDocumentFactory;
import com.metamatrix.metamodels.xml.XmlElement;
import com.metamatrix.metamodels.xml.XmlNamespace;
import com.metamatrix.metamodels.xml.XmlRoot;
import com.metamatrix.metamodels.xml.XmlSequence;
import com.metamatrix.metamodels.xml.impl.XmlDocumentFactoryImpl;

/**
 * TestXmlDocumentUtil
 */
public class TestXmlDocumentUtil extends TestCase {
    
    private XmlDocumentFactory factory;
    
    private XmlDocument     doc;
    private XmlRoot         root;
    private XmlElement      e1;
    private XmlElement      e2;
    private XmlElement      e3;
    private XmlElement      e1_e1;
    private XmlElement      e1_e2;
    private XmlNamespace    e1_ns1;
    private XmlNamespace    e1_ns2;
    private XmlNamespace    e1_e2_ns1;
    private XmlAttribute    e1_a1;
    private XmlAttribute    e2_a1;
    private XmlSequence     e2_s1;
    private XmlElement      e2_s1_e1;
    private ProcessingInstruction e3_pi1;
    private ProcessingInstruction e3_pi2;
    private XmlComment e3_comment1;
    private XmlComment e3_comment2;

    /**
     * Constructor for TestXmlDocumentUtil.
     * @param name
     */
    public TestXmlDocumentUtil(String name) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.factory = new XmlDocumentFactoryImpl();

        this.doc        = this.factory.createXmlDocument();
        this.root       = this.factory.createXmlRoot();
        this.e1         = this.factory.createXmlElement();
        this.e2         = this.factory.createXmlElement();
        this.e3         = this.factory.createXmlElement();
        this.e1_e1      = this.factory.createXmlElement();
        this.e1_e2      = this.factory.createXmlElement();
        this.e1_ns1     = this.factory.createXmlNamespace();
        this.e1_ns2     = this.factory.createXmlNamespace();
        this.e1_e2_ns1  = this.factory.createXmlNamespace();
        this.e1_a1      = this.factory.createXmlAttribute();
        this.e2_a1      = this.factory.createXmlAttribute();
        this.e2_s1      = this.factory.createXmlSequence();
        this.e2_s1_e1   = this.factory.createXmlElement();
        this.e3_pi1     = this.factory.createProcessingInstruction();
        this.e3_pi2     = this.factory.createProcessingInstruction();
        this.e3_comment1= this.factory.createXmlComment();
        this.e3_comment2= this.factory.createXmlComment();

        this.root.setName("root"); //$NON-NLS-1$
        this.e1.setName("e1"); //$NON-NLS-1$
        this.e2.setName("e2"); //$NON-NLS-1$
        this.e3.setName("e3"); //$NON-NLS-1$
        this.e1_e1.setName("e1_e1"); //$NON-NLS-1$
        this.e1_e2.setName("e1_e2"); //$NON-NLS-1$
        this.e1_ns1.setPrefix("e1_ns1"); //$NON-NLS-1$
        this.e1_ns2.setPrefix("e1_ns2"); //$NON-NLS-1$
        this.e1_e2_ns1.setPrefix("e1_e2_ns1"); //$NON-NLS-1$
        this.e1_a1.setName("e1_a1"); //$NON-NLS-1$
        this.e2_a1.setName("e2_a1"); //$NON-NLS-1$
        this.e2_s1_e1.setName("e2_s1_e1"); //$NON-NLS-1$
        this.e3_pi1.setTarget("target"); //$NON-NLS-1$
        this.e3_pi1.setRawText("RAW_TEXT"); //$NON-NLS-1$
        this.e3_pi2.setRawText("RAW_TEXT"); //$NON-NLS-1$
        this.e3_comment1.setText("COMMENT_TEXT"); //$NON-NLS-1$

        this.root.setFragment(this.doc);
        this.e1.setParent(this.root);
        this.e2.setParent(this.root);
        this.e3.setParent(this.root);
        this.e1_e1.setParent(this.e1);
        this.e1_e2.setParent(this.e1);
        this.e1_ns1.setElement(this.e1);
        this.e1_ns2.setElement(this.e1);
        this.e1_e2_ns1.setElement(this.e1_e2);
        this.e1_a1.setElement(this.e1);
        this.e2_a1.setElement(this.e2);
        this.e2_s1.setParent(this.e2);
        this.e2_s1_e1.setParent(this.e2_s1);
        this.e3_pi1.setParent(this.e3);
        this.e3_pi2.setParent(this.e3);
        this.e3_comment1.setParent(this.e3);
        this.e3_comment2.setParent(this.e3);
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
        TestSuite suite = new TestSuite("TestXmlDocumentUtil"); //$NON-NLS-1$
        suite.addTestSuite(TestXmlDocumentUtil.class);
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
    
    public void helpTestPathInDocument( final XmlDocumentEntity entity, final String expectedPath ) {
        Assert.assertNotNull(entity);
        Assert.assertNotNull(expectedPath);
        final String pathStr = XmlDocumentUtil.getPathInDocument(entity);
        Assert.assertNotNull(pathStr);
        if ( !expectedPath.equals(pathStr) ) {
            System.out.println("Failure to match: \"" + expectedPath + "\"" );  //$NON-NLS-1$//$NON-NLS-2$
            System.out.println("              and \"" + pathStr + "\"" );  //$NON-NLS-1$//$NON-NLS-2$
        }
        Assert.assertEquals(expectedPath,pathStr);
    }

    public void helpTestXPath( final XmlDocumentEntity entity, final String expectedXPath ) {
        Assert.assertNotNull(entity);
        Assert.assertNotNull(expectedXPath);
        final String pathStr = XmlDocumentUtil.getXPath(entity);
        Assert.assertNotNull(pathStr);
        if ( !expectedXPath.equals(pathStr) ) {
            System.out.println("Failure to match: \"" + expectedXPath + "\"" );  //$NON-NLS-1$//$NON-NLS-2$
            System.out.println("              and \"" + pathStr + "\"" );  //$NON-NLS-1$//$NON-NLS-2$
        }
        Assert.assertEquals(expectedXPath,pathStr);
    }

    // =========================================================================
    //                         T E S T     C A S E S
    // =========================================================================

//    public void testDataPathSupplied() {
//        Assert.assertTestDataPathSet();
//    }

    public void testCreateXmlPrefixFromUri1() {
        final String uri = "http://namespace.worldnet.ml.com/EDSI/Product/Equities/EquityInstrument_v1_0"; //$NON-NLS-1$
        final String expected = "EquityInstrument_v1_0"; //$NON-NLS-1$
        final String actual = XmlDocumentUtil.createXmlPrefixFromUri(uri);
        Assert.assertEquals(expected,actual);
    }

    public void testCreateXmlPrefixFromUri2() {
        final String uri = "http://www.metamatrix.com/metamodels/Relational"; //$NON-NLS-1$
        final String expected = "Relational"; //$NON-NLS-1$
        final String actual = XmlDocumentUtil.createXmlPrefixFromUri(uri);
        Assert.assertEquals(expected,actual);
    }

    public void testCreateXmlPrefixFromUriWithExtension() {
        final String uri = "http://www.metamatrix.com/metamodels/Relational.xmi"; //$NON-NLS-1$
        final String expected = "Relational"; //$NON-NLS-1$
        final String actual = XmlDocumentUtil.createXmlPrefixFromUri(uri);
        Assert.assertEquals(expected,actual);
    }
    
    public void testCreateXmlPrefixFromUriDblBackSlash() {
        final String uri = "http:\\test"; //$NON-NLS-1$
        final String expected = "test"; //$NON-NLS-1$
        final String actual = XmlDocumentUtil.createXmlPrefixFromUri(uri);
        Assert.assertEquals(expected,actual);
    }

    public void testCreateXmlPrefixFromShortUriWithExtension() {
        final String uri = "Relational.xmi"; //$NON-NLS-1$
        final String expected = "Relational"; //$NON-NLS-1$
        final String actual = XmlDocumentUtil.createXmlPrefixFromUri(uri);
        Assert.assertEquals(expected,actual);
    }

    public void testCreateXmlPrefixFromShortUri() {
        final String uri = "Relational"; //$NON-NLS-1$
        final String expected = "Relational"; //$NON-NLS-1$
        final String actual = XmlDocumentUtil.createXmlPrefixFromUri(uri);
        Assert.assertEquals(expected,actual);
    }

    public void testCreateXmlPrefixFromNullUri() {
        final String uri = null; 
        final String expected = ""; //$NON-NLS-1$
        final String actual = XmlDocumentUtil.createXmlPrefixFromUri(uri);
        Assert.assertEquals(expected,actual);
    }

    public void testCreateXmlPrefixFromZeroLengthUri() {
        final String uri = ""; //$NON-NLS-1$
        final String expected = ""; //$NON-NLS-1$
        final String actual = XmlDocumentUtil.createXmlPrefixFromUri(uri);
        Assert.assertEquals(expected,actual);
    }

    public void testCreateXmlPrefixFromXmlSchema() {
        final String uri = "http://www.w3.org/2001/XMLSchema"; //$NON-NLS-1$
        final String expected = "xs"; //$NON-NLS-1$
        final String actual = XmlDocumentUtil.createXmlPrefixFromUri(uri);
        Assert.assertEquals(expected,actual);
    }

    public void testCreateXmlPrefixFromXmlSchemaInstance() {
        final String uri = "http://www.w3.org/2001/XMLSchema-instance"; //$NON-NLS-1$
        final String expected = "xsi"; //$NON-NLS-1$
        final String actual = XmlDocumentUtil.createXmlPrefixFromUri(uri);
        Assert.assertEquals(expected,actual);
    }
    
    public void testCreateXmlPrefixCase6360_1() {
        final String uri = "urn:us:gov:ic:enterprise:ssg:applicationSystem:0.0"; //$NON-NLS-1$
        final String expected = "urn_us_gov_ic_enterprise_ssg_applicationSystem_0"; //$NON-NLS-1$
        final String actual = XmlDocumentUtil.createXmlPrefixFromUri(uri);
        Assert.assertEquals(expected,actual);
    }

    public void testCreateXmlPrefixCase6360_2() {
        final String uri = "urn:us:gov:ic:enterprise:ssg:applicationSystem"; //$NON-NLS-1$
        final String expected = "urn_us_gov_ic_enterprise_ssg_applicationSystem"; //$NON-NLS-1$
        final String actual = XmlDocumentUtil.createXmlPrefixFromUri(uri);
        Assert.assertEquals(expected,actual);
    }
    
    
    public void testGetPathInDocument() {
        helpTestPathInDocument(this.doc,        "/"); //$NON-NLS-1$
        helpTestPathInDocument(this.root,       "/root"); //$NON-NLS-1$
        helpTestPathInDocument(this.e1,         "/root/e1"); //$NON-NLS-1$
        helpTestPathInDocument(this.e2,         "/root/e2"); //$NON-NLS-1$
        helpTestPathInDocument(this.e3,         "/root/e3"); //$NON-NLS-1$
        helpTestPathInDocument(this.e1_e1,      "/root/e1/e1_e1"); //$NON-NLS-1$
        helpTestPathInDocument(this.e1_e2,      "/root/e1/e1_e2"); //$NON-NLS-1$
        helpTestPathInDocument(this.e1_ns1,     "/root/e1/@xmlns:e1_ns1"); //$NON-NLS-1$
        helpTestPathInDocument(this.e1_ns2,     "/root/e1/@xmlns:e1_ns2"); //$NON-NLS-1$
        helpTestPathInDocument(this.e1_e2_ns1,  "/root/e1/e1_e2/@xmlns:e1_e2_ns1"); //$NON-NLS-1$
        helpTestPathInDocument(this.e1_a1,      "/root/e1/@e1_a1"); //$NON-NLS-1$
        helpTestPathInDocument(this.e2_a1,      "/root/e2/@e2_a1"); //$NON-NLS-1$
        helpTestPathInDocument(this.e2_s1,      "/root/e2/sequence"); //$NON-NLS-1$
        helpTestPathInDocument(this.e2_s1_e1,   "/root/e2/sequence/e2_s1_e1"); //$NON-NLS-1$
        helpTestPathInDocument(this.e3_pi1,     "/root/e3/<?target RAW_TEXT ?>"); //$NON-NLS-1$
        helpTestPathInDocument(this.e3_pi2,     "/root/e3/<? RAW_TEXT ?>"); //$NON-NLS-1$
        helpTestPathInDocument(this.e3_comment1,"/root/e3/<!-- COMMENT_TEXT -->"); //$NON-NLS-1$
        helpTestPathInDocument(this.e3_comment2,"/root/e3/<!--  -->"); //$NON-NLS-1$
    }

    public void testGetXPath() {
        helpTestXPath(this.doc,        "/"); //$NON-NLS-1$
        helpTestXPath(this.root,       "/root"); //$NON-NLS-1$
        helpTestXPath(this.e1,         "/root/e1"); //$NON-NLS-1$
        helpTestXPath(this.e2,         "/root/e2"); //$NON-NLS-1$
        helpTestXPath(this.e3,         "/root/e3"); //$NON-NLS-1$
        helpTestXPath(this.e1_e1,      "/root/e1/e1_e1"); //$NON-NLS-1$
        helpTestXPath(this.e1_e2,      "/root/e1/e1_e2"); //$NON-NLS-1$
        helpTestXPath(this.e1_ns1,     "/root/e1/@xmlns:e1_ns1"); //$NON-NLS-1$
        helpTestXPath(this.e1_ns2,     "/root/e1/@xmlns:e1_ns2"); //$NON-NLS-1$
        helpTestXPath(this.e1_e2_ns1,  "/root/e1/e1_e2/@xmlns:e1_e2_ns1"); //$NON-NLS-1$
        helpTestXPath(this.e1_a1,      "/root/e1/@e1_a1"); //$NON-NLS-1$
        helpTestXPath(this.e2_a1,      "/root/e2/@e2_a1"); //$NON-NLS-1$
        helpTestXPath(this.e2_s1,      "/root/e2"); //$NON-NLS-1$
        helpTestXPath(this.e2_s1_e1,   "/root/e2/e2_s1_e1"); //$NON-NLS-1$
        helpTestXPath(this.e3_pi1,     "/root/e3"); //$NON-NLS-1$
        helpTestXPath(this.e3_pi2,     "/root/e3"); //$NON-NLS-1$
        helpTestXPath(this.e3_comment1,"/root/e3"); //$NON-NLS-1$
        helpTestXPath(this.e3_comment2,"/root/e3"); //$NON-NLS-1$
    }

    
}
