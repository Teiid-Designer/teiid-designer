/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.relationship;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.teiid.core.util.PluginUtilImpl;
import org.teiid.core.util.SmartTestDesignerSuite;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.metamodels.relationship.Relationship;
import org.teiid.designer.metamodels.relationship.RelationshipFactory;
import org.teiid.designer.metamodels.relationship.RelationshipMetamodelPlugin;
import org.teiid.designer.metamodels.relationship.RelationshipType;

/**
 * TestRelationshipPlugin
 */
public class TestRelationshipPlugin extends TestCase {

    /**
     * Constructor for TestRelationshipPlugin.
     * 
     * @param name
     */
    public TestRelationshipPlugin( String name ) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ModelerCore.testLoadModelContainer();
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
        TestSuite suite = new TestSuite("TestRelationshipPlugin"); //$NON-NLS-1$
        suite.addTestSuite(TestRelationshipPlugin.class);
        // One-time setup and teardown
        return new TestSetup(suite) {
            @Override
            public void setUp() {
                RelationshipMetamodelPlugin plugin = new RelationshipMetamodelPlugin();
                SmartTestDesignerSuite.mockStartBundle(plugin, RelationshipMetamodelPlugin.PLUGIN_ID);
                ((PluginUtilImpl)RelationshipMetamodelPlugin.Util).initializePlatformLogger(plugin);
            }

            @Override
            public void tearDown() {
            }
        };
    }

    /*
     * Test for RelationshipEditor createEditor(Relationship)
     */
    public void testCreateEditorRelationship() {
        final Relationship relationship = RelationshipFactory.eINSTANCE.createRelationship();
        final RelationshipEditor editor = RelationshipPlugin.createEditor(relationship);
        assertNotNull(editor);
    }

    public void testCreateEditorWithNullRelationship() {
        final Relationship relationship = null;
        try {
            RelationshipPlugin.createEditor(relationship);
            fail("Did not catch null argument"); //$NON-NLS-1$
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    /*
     * Test for RelationshipTypeEditor createEditor(RelationshipType)
     */
    public void testCreateEditorRelationshipType() {
        final RelationshipType relationshipType = RelationshipFactory.eINSTANCE.createRelationshipType();
        final RelationshipTypeEditor editor = RelationshipPlugin.createEditor(relationshipType);
        assertNotNull(editor);
    }

    public void testCreateEditorWithNullRelationshipType() {
        final RelationshipType relationshipType = null;
        try {
            RelationshipPlugin.createEditor(relationshipType);
            fail("Did not catch null argument"); //$NON-NLS-1$
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

}
