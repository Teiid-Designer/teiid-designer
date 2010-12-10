/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.relationship;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import com.metamatrix.core.util.SmartTestDesignerSuite;
import com.metamatrix.metamodels.relationship.RelationshipFactory;
import com.metamatrix.metamodels.relationship.RelationshipType;
import com.metamatrix.modeler.core.index.IndexSelectorFactory;
import com.metamatrix.modeler.core.workspace.FakeModelWorkspace;
import com.metamatrix.modeler.core.workspace.FakeModelWorkspaceItem;
import com.metamatrix.modeler.core.workspace.ModelWorkspace;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceItem;
import com.metamatrix.modeler.relationship.RelationshipSearch;

/**
 * TestRelationshipSearchImpl
 */
public class TestRelationshipSearchImpl extends TestCase {

    private RelationshipSearchImpl search;
    private ModelWorkspace workspace;
    private IndexSelectorFactory factory;

    private RelationshipType type1;
    private RelationshipType type2;

    /**
     * Constructor for TestRelationshipSearchImpl.
     * @param name
     */
    public TestRelationshipSearchImpl(String name) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        this.workspace = new FakeModelWorkspace();
        this.factory = new FakeIndexSelectorFactory();
        this.search = new RelationshipSearchImpl(workspace,factory);

        this.type1 = RelationshipFactory.eINSTANCE.createRelationshipType();
        this.type2 = RelationshipFactory.eINSTANCE.createRelationshipType();
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
        SmartTestDesignerSuite suite = new SmartTestDesignerSuite("com.metamatrix.modeler.relationship", "TestRelationshipSearchImpl"); //$NON-NLS-1$//$NON-NLS-2$
        suite.addTestSuite(TestRelationshipSearchImpl.class);
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

    public static List helpCreateModelWorkspaceItems( final String[] paths ) {
        final List items = new LinkedList();
        for (int i = 0; i < paths.length; ++i) {
            final String path = paths[i];
            final ModelWorkspaceItem item = new FakeModelWorkspaceItem(ModelWorkspaceItem.MODEL_FOLDER,path);
            items.add(item);
        }
        return items;
    }

    public void helpCheckPaths( final String[] inputPaths, final String[] expectedPaths ) {
        final List modelWorkspaceItems = helpCreateModelWorkspaceItems(inputPaths);
        final List outputPaths = this.search.getPaths(modelWorkspaceItems);

        assertEquals(expectedPaths.length, outputPaths.size());

        int i=0;
        final Iterator iter = outputPaths.iterator();
        while (iter.hasNext()) {
            final IPath path = (IPath)iter.next();
            final String expectedPathStr = expectedPaths[i++];
            final IPath expectedPath = new Path(expectedPathStr);
            assertEquals(expectedPath, path);
			assertEquals(expectedPathStr, path.toString());
        }
    }

    public void helpCheckStatus( final IStatus status, final int severity ) {
        assertNotNull(status);
        if ( severity != status.getSeverity() ) {
            System.out.println(status);
        }
        assertEquals(severity, status.getSeverity());
    }

    // =========================================================================
    //                         T E S T     C A S E S
    // =========================================================================

//    public void testDataPathSupplied() {
//        UnitTestUtil.assertTestDataPathSet();
//    }
//

    public void testSetup() {
        assertNotNull(this.workspace);
        assertNotNull(this.factory);
        assertNotNull(this.search);
        assertNotNull(this.type1);
        assertNotNull(this.type2);
    }

    public void testRelationshipSearchImpl() {
        try {
            new RelationshipSearchImpl(null,this.factory);
            fail("Failed to catch null workspace argument"); //$NON-NLS-1$
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            new RelationshipSearchImpl(this.workspace,null);
            fail("Failed to catch null workspace argument"); //$NON-NLS-1$
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    public void testGetParticipantsCriteria() {
        assertNotNull(this.search.getParticipantsCriteria());
        assertEquals(0, this.search.getParticipantsCriteria().size());
    }

    public void testSetParticipantsCriteria() {
    }

    public void testGetNameCriteria() {
        assertSame(RelationshipSearch.DEFAULT_NAME_CRITERIA, this.search.getNameCriteria());
    }

    public void testIsNameCriteriaCaseSensitive() {
        assertEquals(RelationshipSearch.DEFAULT_NAME_CASE_SENSITIVE, this.search.isNameCriteriaCaseSensitive());
    }

    public void testSetNameCriteria() {
        assertSame(RelationshipSearch.DEFAULT_NAME_CRITERIA, this.search.getNameCriteria());

        final String pattern1 = "*something*"; //$NON-NLS-1$
        final boolean pattern1CaseSensitive = !RelationshipSearch.DEFAULT_NAME_CASE_SENSITIVE;
        this.search.setNameCriteria(pattern1,pattern1CaseSensitive);
        assertEquals(pattern1, this.search.getNameCriteria());
		assertEquals(pattern1CaseSensitive, this.search.isNameCriteriaCaseSensitive());

        final String pattern2 = null;
        final boolean pattern2CaseSensitive = !RelationshipSearch.DEFAULT_NAME_CASE_SENSITIVE;
        this.search.setNameCriteria(pattern2,pattern2CaseSensitive);
        assertEquals(RelationshipSearch.DEFAULT_NAME_CRITERIA, this.search.getNameCriteria());
		assertEquals(pattern2CaseSensitive, this.search.isNameCriteriaCaseSensitive());

        final String pattern3 = "*something else?"; //$NON-NLS-1$
        final boolean pattern3CaseSensitive = RelationshipSearch.DEFAULT_NAME_CASE_SENSITIVE;
        this.search.setNameCriteria(pattern3,pattern3CaseSensitive);
        assertEquals(pattern3, this.search.getNameCriteria());
		assertEquals(pattern3CaseSensitive, this.search.isNameCriteriaCaseSensitive());
    }

    public void testGetRelationshipTypeCriteria() {
        // By default, should be null type
        assertEquals(null, this.search.getRelationshipTypeCriteria());
    }

    public void testIsIncludeSubtypes() {
        assertEquals(RelationshipSearch.DEFAULT_INCLUDE_SUBTYPES, this.search.isIncludeSubtypes());
    }

    public void testSetRelationshipTypeCriteria() {
        // By default, should be null type
        assertEquals(null, this.search.getRelationshipTypeCriteria());
		assertEquals(RelationshipSearch.DEFAULT_INCLUDE_SUBTYPES, this.search.isIncludeSubtypes());

        final boolean includeSubtypes1 = false;
        this.search.setRelationshipTypeCriteria(this.type1,includeSubtypes1);
        assertSame(this.type1, this.search.getRelationshipTypeCriteria());
        assertEquals(includeSubtypes1, this.search.isIncludeSubtypes());

        final boolean includeSubtypes2 = true;
        this.search.setRelationshipTypeCriteria(this.type1,includeSubtypes2);
        assertSame(this.type1, this.search.getRelationshipTypeCriteria());
        assertEquals(includeSubtypes2, this.search.isIncludeSubtypes());

        final boolean includeSubtypes3 = false;
        this.search.setRelationshipTypeCriteria(this.type2,includeSubtypes3);
        assertSame(this.type2, this.search.getRelationshipTypeCriteria());
        assertEquals(includeSubtypes3, this.search.isIncludeSubtypes());

        final boolean includeSubtypes4 = true;
        this.search.setRelationshipTypeCriteria(null,includeSubtypes4);
        assertEquals(null, this.search.getRelationshipTypeCriteria());
		assertEquals(includeSubtypes4, this.search.isIncludeSubtypes());

        final boolean includeSubtypes5 = false;
        this.search.setRelationshipTypeCriteria(RelationshipSearch.NO_RELATIONSHIP_TYPE,includeSubtypes5);
        assertSame(RelationshipSearch.NO_RELATIONSHIP_TYPE, this.search.getRelationshipTypeCriteria());
        assertEquals(includeSubtypes5, this.search.isIncludeSubtypes());

        final boolean includeSubtypes6 = false;
        this.search.setRelationshipTypeCriteria(RelationshipSearch.ANY_RELATIONSHIP_TYPE,includeSubtypes6);
        assertSame(RelationshipSearch.ANY_RELATIONSHIP_TYPE, this.search.getRelationshipTypeCriteria());
        assertEquals(includeSubtypes6, this.search.isIncludeSubtypes());
    }

    public void testGetRelationshipModelScope() {
        this.search.setRelationshipModelScope( Collections.EMPTY_LIST );
        assertNotNull(this.search.getRelationshipModelScope());
        assertEquals(0, this.search.getRelationshipModelScope().size());
    }

//    public void testSetRelationshipModelScope() {
//        this.search.setRelationshipModelScope( Collections.EMPTY_LIST );
//        UnitTestUtil.assertNotNull(this.search.getRelationshipModelScope());
	//        assertEquals(0,this.search.getRelationshipModelScope().size());
//
//        this.search.setRelationshipModelScope( Collections.singletonList(this.workspace) );
//        UnitTestUtil.assertNotNull(this.search.getRelationshipModelScope());
	//        assertEquals(Collections.EMPTY_LIST,this.search.getRelationshipModelScope()); //need to search the workspace,
//                                                                                                   //the command will default to
//                                                                                                   //the workspace when no values are set.
	//     // assertSame(this.search.getModelWorkspace(),this.search.getRelationshipModelScope()); --No longer true, see above comment
//    }

    public void testCanExecuteWithInitializedSettings() {
        final IStatus status1 = this.search.canExecute();
        helpCheckStatus(status1,IStatus.ERROR);
    }

//    public void testCanExecuteWithNoTypeCriteria() {
//        final IStatus status1 = this.search.canExecute();
//        helpCheckStatus(status1,IStatus.ERROR);
//
//        // Set the scope ...
//        this.search.setRelationshipModelScope( Collections.singletonList(this.workspace) );
//        helpCheckStatus(status1,IStatus.ERROR);
//    }

    public void testCanExecuteWithNoScope() {
        final IStatus status1 = this.search.canExecute();
        helpCheckStatus(status1,IStatus.ERROR);

        // Set the type ...
        this.search.setRelationshipTypeCriteria(RelationshipSearch.ANY_RELATIONSHIP_TYPE,true);
        helpCheckStatus(status1,IStatus.ERROR);
    }

//    public void testCanExecuteWithScopeAndAllTypes() {
//        final IStatus status1 = this.search.canExecute();
//        helpCheckStatus(status1,IStatus.ERROR);
//
//        // Set the scope ...
//        this.search.setRelationshipModelScope( Collections.singletonList(this.workspace) );
//
//        // Set the type ...
//        this.search.setRelationshipTypeCriteria(RelationshipSearch.ANY_RELATIONSHIP_TYPE,true);
//
//        final IStatus status2 = this.search.canExecute();
//        helpCheckStatus(status2,IStatus.OK);
//    }

// Must be done in a PDE test ...
//    public void testExecute() {
//        final IStatus status1 = this.search.execute(new NullProgressMonitor());
//        helpCheckStatus(status1,IStatus.OK);
//    }
//
//    public void testExecuteWithNullMonitor() {
//        final IStatus status1 = this.search.execute(null);
//        helpCheckStatus(status1,IStatus.OK);
//    }
//
//    public void testDoExecute() {
//    }

    public void testGetPaths() {
        helpCheckPaths( new String[]{"/A/B/C/defg", //$NON-NLS-1$
                                     "/A/B/h/ijk", //$NON-NLS-1$
                                     "/A/B/C/def"}, //$NON-NLS-1$
                        new String[]{"/A/B/C/def", //$NON-NLS-1$
                                     "/A/B/C/defg",  //$NON-NLS-1$
                                     "/A/B/h/ijk"}); //$NON-NLS-1$

        helpCheckPaths( new String[]{"/A/B", //$NON-NLS-1$
                                     "/A/B/h/ijk", //$NON-NLS-1$
                                     "/A/B/C/def"}, //$NON-NLS-1$
                        new String[]{"/A/B"}); //$NON-NLS-1$

        helpCheckPaths( new String[]{"/A/B", //$NON-NLS-1$
                                     "/A/B/h/ijk", //$NON-NLS-1$
                                     "/"}, //$NON-NLS-1$
                        new String[]{"/"}); //$NON-NLS-1$

        helpCheckPaths( new String[]{"/A/B", //$NON-NLS-1$
                                     "/A/C/h/ijk", //$NON-NLS-1$
                                     "/"}, //$NON-NLS-1$
                        new String[]{"/"}); //$NON-NLS-1$

        helpCheckPaths( new String[]{"/A/B", //$NON-NLS-1$
                                     "/A/C/h/ijk", //$NON-NLS-1$
                                     "/D"}, //$NON-NLS-1$
                        new String[]{"/A/B", //$NON-NLS-1$
                                     "/A/C/h/ijk", //$NON-NLS-1$
                                     "/D"}); //$NON-NLS-1$

        helpCheckPaths( new String[]{},
                        new String[]{});
    }

    public void testGetResults() {
        // Should be empty before execution ...
        assertNotNull(this.search.getResults());
        assertEquals(0, this.search.getResults().size());

        // Execute; still should be empty (until doExecute is implemented) ...
        this.search.execute(null);
        assertNotNull(this.search.getResults());
        assertEquals(0, this.search.getResults().size());
    }

}
