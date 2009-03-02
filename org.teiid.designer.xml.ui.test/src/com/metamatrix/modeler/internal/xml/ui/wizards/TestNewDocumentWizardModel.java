/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.xml.ui.wizards;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.metamatrix.metamodels.xml.XmlFragment;
import com.metamatrix.modeler.xml.IVirtualDocumentFragmentSource;

public class TestNewDocumentWizardModel extends TestCase {
    //
    // Instance variables:
    //
    private NewDocumentWizardModel wizmdl;
    private IProgressMonitor       progMon;

    //
    // Set up:
    //
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        wizmdl = new NewDocumentWizardModel();
        wizmdl.setSource(new FakeVirtualDocumentFragmentSource());
        progMon = new NullProgressMonitor();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        wizmdl  = null;
        progMon = null;
    }

    //
    // Test methods:
    //
    public void testgetFragments() {
        XmlFragment[] frags = wizmdl.getFragments(null, progMon);
        // make sure not null:
        assertNotNull(frags);
        // make sure cached when no settings changed:
        assertSame(frags, wizmdl.getFragments(null, progMon));
    }

    public void testSetBuildEntireDocuments() {
        // setting to different: ---------------------------
        boolean newValue = !wizmdl.getBuildEntireDocuments();

        // settings this flag should trigger a rebuild:
        XmlFragment[] fragsBefore = wizmdl.getFragments(null, progMon);
        wizmdl.setBuildEntireDocuments(newValue);
        assertNotSame(fragsBefore, wizmdl.getFragments(null, progMon));

        // make sure the getter has been updated:
        assertEquals(newValue, wizmdl.getBuildEntireDocuments());

        // setting to same: --------------------------------
        fragsBefore = wizmdl.getFragments(null, progMon);
        wizmdl.setBuildEntireDocuments(newValue);
        assertSame(fragsBefore, wizmdl.getFragments(null, progMon));
    }

    public void testSetSource() {
        IVirtualDocumentFragmentSource srcBefore = wizmdl.getSource();
        XmlFragment[] fragsBefore = wizmdl.getFragments(null, progMon);

        wizmdl.setSource(new FakeVirtualDocumentFragmentSource());
        
        // different source, different fragments:
        assertNotSame(srcBefore, wizmdl.getSource());
        assertNotSame(fragsBefore, wizmdl.getFragments(null, progMon));
    }

    public void testSetSelectedFragmentCount() {
        // setting to different: ---------------------------
        int newValue = wizmdl.getSelectedFragmentCount() + 20;

        // settings this flag should trigger a rebuild:
        XmlFragment[] fragsBefore = wizmdl.getFragments(null, progMon);
        wizmdl.setSelectedFragmentCount(newValue);
        assertNotSame(fragsBefore, wizmdl.getFragments(null, progMon));

        // make sure the getter has been updated:
        assertEquals(newValue, wizmdl.getSelectedFragmentCount());

        // setting to same: --------------------------------
        fragsBefore = wizmdl.getFragments(null, progMon);
        wizmdl.setSelectedFragmentCount(newValue);
        assertSame(fragsBefore, wizmdl.getFragments(null, progMon));
    }

    public void testSetBuildMappingClasses() {
        boolean newValue = !wizmdl.getBuildMappingClasses();

        // settings this flag should *not* trigger a rebuild:
        XmlFragment[] fragsBefore = wizmdl.getFragments(null, progMon);
        wizmdl.setBuildMappingClasses(newValue);
        assertSame(fragsBefore, wizmdl.getFragments(null, progMon));

        // make sure the getter has been updated:
        assertEquals(newValue, wizmdl.getBuildMappingClasses());
    }

    public void testSetEstimatedNodeCount() {
        int newValue = wizmdl.getEstimatedNodeCount() + 2000;

        // settings this flag should *not* trigger a rebuild:
        XmlFragment[] fragsBefore = wizmdl.getFragments(null, progMon);
        wizmdl.setEstimatedNodeCount(newValue);
        assertSame(fragsBefore, wizmdl.getFragments(null, progMon));

        // make sure the getter has been updated:
        assertEquals(newValue, wizmdl.getEstimatedNodeCount());
    }

    public void testSetUseSchemaTypes() {
        // setting to different: ---------------------------
        boolean newValue = !wizmdl.getUseSchemaTypes();

        // settings this flag should trigger a rebuild:
        XmlFragment[] fragsBefore = wizmdl.getFragments(null, progMon);
        wizmdl.setUseSchemaTypes(newValue);
        assertNotSame(fragsBefore, wizmdl.getFragments(null, progMon));

        // make sure the getter has been updated:
        assertEquals(newValue, wizmdl.getUseSchemaTypes());

        // setting to same: --------------------------------
        fragsBefore = wizmdl.getFragments(null, progMon);
        wizmdl.setUseSchemaTypes(newValue);
        assertSame(fragsBefore, wizmdl.getFragments(null, progMon));
    }

    public void testSetBuildGlobalOnly() {
        // setting to different: ---------------------------
        boolean newValue = !wizmdl.getBuildGlobalOnly();

        // settings this flag should trigger a rebuild:
        XmlFragment[] fragsBefore = wizmdl.getFragments(null, progMon);
        wizmdl.setBuildGlobalOnly(newValue);
        assertNotSame(fragsBefore, wizmdl.getFragments(null, progMon));

        // make sure the getter has been updated:
        assertEquals(newValue, wizmdl.getBuildGlobalOnly());

        // setting to same: --------------------------------
        fragsBefore = wizmdl.getFragments(null, progMon);
        wizmdl.setBuildGlobalOnly(newValue);
        assertSame(fragsBefore, wizmdl.getFragments(null, progMon));
    }
}
