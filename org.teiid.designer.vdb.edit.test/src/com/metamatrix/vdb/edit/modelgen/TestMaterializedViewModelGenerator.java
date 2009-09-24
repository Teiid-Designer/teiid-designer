/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.edit.modelgen;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryRegistryImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import com.metamatrix.core.util.SmartTestSuite;
import com.metamatrix.metamodels.core.impl.CorePackageImpl;
import com.metamatrix.metamodels.relational.Table;
import com.metamatrix.metamodels.relational.impl.RelationalPackageImpl;

/**
 * TestVdbEditingContextImpl2
 */
public class TestMaterializedViewModelGenerator extends TestCase {

    private final String PATH_SEPARATOR = File.separator;
    private final String NESTED_MATERIALIZATION_PATH = SmartTestSuite.getTestDataPath() + PATH_SEPARATOR + "TestMatView.xmi";//$NON-NLS-1$

    /**
     * Constructor for PdeTestVdbEditingContextImpl.
     * 
     * @param name
     */
    public TestMaterializedViewModelGenerator( String name ) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        RelationalPackageImpl.init();
        CorePackageImpl.init();
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
        final TestSuite suite = new SmartTestSuite("org.teiid.designer.vdb.edit", "TestMaterializedViewModelGenerator"); //$NON-NLS-1$//$NON-NLS-2$
        suite.addTestSuite(TestMaterializedViewModelGenerator.class);
        return new TestSetup(suite);
    }

    // =========================================================================
    // H E L P E R M E T H O D S
    // =========================================================================
    private void helpValidateMatViewTableNames( final Resource rsrc ) {
        final Set tableNames = new HashSet();
        final Iterator contents = rsrc.getAllContents();
        while (contents.hasNext()) {
            Object next = contents.next();
            if (next instanceof Table) {
                final String name = ((Table)next).getName();
                if (tableNames.contains(name)) {
                    fail("Materialized View table names were not unique.  Found multiple tables with name " + name); //$NON-NLS-1$                   
                }
                tableNames.add(name);
            }
        }
    }

    private void helpValidateMatViewTableNameInSource( final Resource rsrc,
                                                       final HashSet expectedValues ) {
        final Set tableNames = new HashSet();
        final Iterator contents = rsrc.getAllContents();
        while (contents.hasNext()) {
            Object next = contents.next();
            if (next instanceof Table) {
                final String name = ((Table)next).getNameInSource();
                tableNames.add(name);
            }
        }

        assertEquals("Actual Materialized View table names are not equal to expected names.", expectedValues, tableNames); //$NON-NLS-1$

    }

    public void testMaterializationWithNestedTables() throws Exception {
        final ResourceSet rs = new ResourceSetImpl();
        Resource.Factory.Registry reg = rs.getResourceFactoryRegistry();
        if (reg == null) {
            reg = new ResourceFactoryRegistryImpl();
            rs.setResourceFactoryRegistry(reg);
        }
        final Map m = reg.getExtensionToFactoryMap();
        m.put("xmi", new XMIResourceFactoryImpl()); //$NON-NLS-1$

        // Load the virtual model
        final URI uri = URI.createFileURI(NESTED_MATERIALIZATION_PATH);
        final Resource rsrc = rs.createResource(uri);
        rsrc.load(new HashMap());

        MaterializedViewModelGenerator mvmg = new MaterializedViewModelGenerator();
        mvmg.execute(rsrc, false, "TestingMVModel", uri);//$NON-NLS-1$
        HashSet expectedValues = new HashSet();
        Object[] values = new Object[] {
            "NestedST1000003", "MV1000004ST", "MV1000004", "Nested1000002", "NestedST1000002", "Nested1000003", "NestedST", "Nested"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
        for (int i = 0; i < values.length; i++) {
            expectedValues.add(values[i]);
        }
        helpValidateMatViewTableNames(mvmg.getMaterializedViewModel());
        helpValidateMatViewTableNameInSource(mvmg.getMaterializedViewModel(), expectedValues);
    }
}
