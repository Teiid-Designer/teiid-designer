/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.webservice;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.core.runtime.Path;
import com.metamatrix.core.util.SmartTestDesignerSuite;
import com.metamatrix.modeler.compare.selector.ModelSelector;
import com.metamatrix.modeler.compare.selector.TransientModelSelector;
import com.metamatrix.modeler.webservice.FakeIWebServiceModelBuilder;
import com.metamatrix.modeler.webservice.IWebServiceModelBuilder;

/**
 * @since 4.2
 */
public class TestWebServiceModelGenerator extends TestCase {

    private IWebServiceModelBuilder builder;
    private WebServiceModelGenerator generator;
    private ModelSelector wsSelector;
    private ModelSelector xmlSelector;

    /**
     * Constructor for TestWebServiceModelGenerator.
     * 
     * @param name
     */
    public TestWebServiceModelGenerator( String name ) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        this.builder = new FakeIWebServiceModelBuilder();
        this.builder.setModelPath(new Path("/Project/WebServiceModelGenerator_WsdlFile.wsdl")); //$NON-NLS-1$
        this.builder.setXmlModel(new Path("/Project/WebServiceModelGenerator_XmlDocumentModelOutput.xmi")); //$NON-NLS-1$

        final String uri = this.builder.getModelPath().toString();
        this.wsSelector = new TransientModelSelector(uri);

        final String uri2 = this.builder.getXmlModel().toString();
        this.xmlSelector = new TransientModelSelector(uri2);

        this.generator = new WebServiceModelGenerator(this.builder, this.wsSelector, this.xmlSelector);
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
        TestSuite suite = new SmartTestDesignerSuite("org.teiid.designer.webservice", "TestWebServiceModelGenerator"); //$NON-NLS-1$ //$NON-NLS-2$
        suite.addTestSuite(TestWebServiceModelGenerator.class);
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
    // H E L P E R M E T H O D S
    // =========================================================================

    // =========================================================================
    // T E S T C A S E S
    // =========================================================================

    public void testGetWebServiceModelBuilder() {
        assertSame(this.builder, this.generator.getWebServiceModelBuilder());
    }

    public void testGetDescription() {
        assertNotNull(this.generator.getDescription());
    }

    public void testSetDescription() {
        assertNotNull(this.generator.getDescription());

        final String desc = "This is the new description"; //$NON-NLS-1$
        this.generator.setDescription(desc);
        assertNotNull(this.generator.getDescription());
        assertEquals(desc, this.generator.getDescription());
        assertSame(desc, this.generator.getDescription());
    }

    public void testClose() {
        this.generator.close();
    }

}
