/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.relationship;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.runtime.IStatus;

import com.metamatrix.metamodels.relationship.Relationship;
import com.metamatrix.metamodels.relationship.RelationshipFactory;
import com.metamatrix.metamodels.relationship.RelationshipType;
import com.metamatrix.metamodels.relationship.impl.RelationshipFactoryImpl;
import com.metamatrix.modeler.relationship.RelationshipEditor;
import com.metamatrix.modeler.relationship.RelationshipPlugin;

/**
 * TestRelationshipEditorImpl
 */
public class TestRelationshipEditorImpl extends TestCase {

    private static final RelationshipFactory FACTORY = new RelationshipFactoryImpl();

    private Relationship relationship;
    private Relationship relationshipNoType;
    private RelationshipType type;
    private RelationshipEditor editor;
    private RelationshipEditor editorNoType;

    /**
     * Constructor for TestRelationshipEditorImpl.
     * @param name
     */
    public TestRelationshipEditorImpl(String name) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.type = FACTORY.createRelationshipType();

        this.relationship = FACTORY.createRelationship();
        this.relationship.setType(this.type);
        this.editor = RelationshipPlugin.createEditor(this.relationship);

        this.relationshipNoType = FACTORY.createRelationship();
        this.editorNoType = RelationshipPlugin.createEditor(this.relationshipNoType);
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
        TestSuite suite = new TestSuite("TestRelationshipEditorImpl"); //$NON-NLS-1$
        suite.addTestSuite(TestRelationshipEditorImpl.class);
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

    // =========================================================================
    //                         T E S T     C A S E S
    // =========================================================================

//    public void testDataPathSupplied() {
//        UnitTestUtil.assertTestDataPathSet();
//    }
    public void testSetup() {
        assertNotNull(this.relationship);
        assertNotNull(this.relationshipNoType);
        assertNotNull(this.type);
        assertNotNull(this.editor);
        assertNotNull(this.editorNoType);
    }

    public void testRelationshipEditorImplWithNullRelationship() {
        try {
            new RelationshipEditorImpl(null,true);
            fail("Failed to catch null argument"); //$NON-NLS-1$
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            new RelationshipEditorImpl(null,false);
            fail("Failed to catch null argument"); //$NON-NLS-1$
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    public void testRelationshipEditorImplWithRelationship() {
        final RelationshipEditor ed = new RelationshipEditorImpl(this.relationship,true);
        assertNotNull(ed);
        final RelationshipEditor ed2 = new RelationshipEditorImpl(this.relationship,false);
        assertNotNull(ed2);
    }

    public void testGetRelationship() {
        assertSame(this.relationship, this.editor.getRelationship());
    }

    public void testValidate() {
        final IStatus status = this.editor.validate();
        assertNotNull(status);
        assertEquals(IStatus.ERROR, status.getSeverity());
    }

    public void testGetName() {
        final String name = "This is the name"; //$NON-NLS-1$
        this.relationship.setName(name);
        final String nameViaEditor = this.editor.getName();
        assertSame(name, nameViaEditor);
    }

    public void testSetName() {
        final String name = "This is the name"; //$NON-NLS-1$
        assertNull(this.editor.getName());
        this.editor.setName(name);
        final String nameViaEditor = this.editor.getName();
        assertSame(name, nameViaEditor);
		assertSame(name, this.relationship.getName());
    }

    public void testGetRelationshipType() {
        assertSame(this.type, this.relationship.getType());
		assertSame(this.type, this.editor.getRelationshipType());
        assertNull(this.editorNoType.getRelationshipType());
    }

    public void testSetRelationshipType() {
        assertSame(this.type, this.relationship.getType());
		assertSame(this.type, this.editor.getRelationshipType());

        this.editor.setRelationshipType(null);
        assertNull(this.editor.getRelationshipType());
        assertNull(this.relationship.getType());

        this.editor.setRelationshipType(this.type);
        assertSame(this.type, this.relationship.getType());
		assertSame(this.type, this.editor.getRelationshipType());

        assertNull(this.editorNoType.getRelationshipType());
    }

    public void testGetComment() {
    }

    public void testSetComment() {
    }

    public void testGetSourceRoleName() {
    }

    public void testGetTargetRoleName() {
    }

    public void testGetSourceParticipants() {
    }

    public void testGetTargetParticipants() {
    }

    /*
     * Test for boolean moveSourceParticipantToTargetParticipant(EObject)
     */
    public void testMoveSourceParticipantToTargetParticipantEObject() {
    }

    /*
     * Test for boolean moveTargetParticipantToSourceParticipant(EObject)
     */
    public void testMoveTargetParticipantToSourceParticipantEObject() {
    }

    /*
     * Test for boolean canMoveSourceParticipantToTargetParticipant(EObject)
     */
    public void testCanMoveSourceParticipantToTargetParticipantEObject() {
    }

    /*
     * Test for boolean canMoveTargetParticipantToSourceParticipant(EObject)
     */
    public void testCanMoveTargetParticipantToSourceParticipantEObject() {
    }

    /*
     * Test for boolean canAddToTargetParticipants(EObject)
     */
    public void testCanAddToTargetParticipantsEObject() {
    }

    /*
     * Test for boolean canAddToSourceParticipants(EObject)
     */
    public void testCanAddToSourceParticipantsEObject() {
    }

    /*
     * Test for boolean moveSourceParticipantToTargetParticipant(List)
     */
    public void testMoveSourceParticipantToTargetParticipantList() {
    }

    /*
     * Test for boolean moveTargetParticipantToSourceParticipant(List)
     */
    public void testMoveTargetParticipantToSourceParticipantList() {
    }

    /*
     * Test for boolean canMoveSourceParticipantToTargetParticipant(List)
     */
    public void testCanMoveSourceParticipantToTargetParticipantList() {
    }

    /*
     * Test for boolean canMoveTargetParticipantToSourceParticipant(List)
     */
    public void testCanMoveTargetParticipantToSourceParticipantList() {
    }

    /*
     * Test for boolean canAddToTargetParticipants(List)
     */
    public void testCanAddToTargetParticipantsList() {
    }

    /*
     * Test for boolean canAddToSourceParticipants(List)
     */
    public void testCanAddToSourceParticipantsList() {
    }

    public void testExecuteAsTransaction() {
    }

    public void testIsExclusive() {
    }

    public void testIsTargetUnique() {
    }

    public void testIsSourceUnique() {
    }

}
