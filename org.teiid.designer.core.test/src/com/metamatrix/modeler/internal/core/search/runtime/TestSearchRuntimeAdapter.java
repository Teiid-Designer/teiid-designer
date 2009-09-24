/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.search.runtime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.metamatrix.internal.core.index.WordEntry;
import com.metamatrix.modeler.core.metamodel.aspect.relationship.RelationshipAspect;
import com.metamatrix.modeler.core.metamodel.aspect.relationship.RelationshipTypeAspect;
import com.metamatrix.modeler.core.search.runtime.RelatedObjectRecord;
import com.metamatrix.modeler.core.search.runtime.RelationshipRecord;
import com.metamatrix.modeler.core.search.runtime.RelationshipTypeRecord;
import com.metamatrix.modeler.internal.core.search.runtime.SearchRuntimeAdapter;

/**
 * TestSearchRuntimeAdapter
 */
public class TestSearchRuntimeAdapter extends TestCase {
	private static final List WORD_ENTRIES = new ArrayList(7);

	// -------------------------------------------------
	// Variables initialized during one-time startup ...
	// -------------------------------------------------

	// ---------------------------------------
	// Variables initialized for each test ...
	// ---------------------------------------

	// =========================================================================
	//                        F R A M E W O R K
	// =========================================================================

	/**
	 * Constructor for TestRuntimeAdapter.
	 * @param name
	 */
	public TestSearchRuntimeAdapter(String name) {
		super(name);
	}

	// =========================================================================
	//                        T E S T   C O N T R O L
	// =========================================================================

	/** 
	 * Construct the test suite, which uses a one-time setup call
	 * and a one-time tear-down call.
	 */
	public static Test suite() {
		TestSuite suite = new TestSuite("TestRelationshipRuntimeAdapter"); //$NON-NLS-1$
//		  suite.addTest(new TestRelationshipRuntimeAdapter("testJoinEntryResultsWithContinuations")); //$NON-NLS-1$
		suite.addTestSuite(TestSearchRuntimeAdapter.class);

		return new TestSetup(suite) { // junit.extensions package
			// One-time setup and teardown
			@Override
            public void setUp() throws Exception {
				oneTimeSetUp();
			}
			@Override
            public void tearDown() {
				oneTimeTearDown();
			}
		};
	}

	// =========================================================================
	//                                 M A I N
	// =========================================================================

	public static void main(String args[]) {
		junit.textui.TestRunner.run(suite());
		System.exit(0);
	}

	// =========================================================================
	//                 S E T   U P   A N D   T E A R   D O W N
	// =========================================================================

	@Override
    protected void setUp() throws Exception {
	}

	@Override
    protected void tearDown() throws Exception {
	}

	public static void oneTimeSetUp() {
	}

	public static void oneTimeTearDown() {
	}

	// =========================================================================
	//                      H E L P E R   M E T H O D S
	// =========================================================================

	private WordEntry createRelatedObjectWord(final RelationshipAspect aspect) {
		WORD_ENTRIES.clear();
		SearchRuntimeAdapter.addRelatedObjectWords(aspect, null, WORD_ENTRIES);
		return (WordEntry)WORD_ENTRIES.get(0);
	}

	private WordEntry createRelationshipWord(final RelationshipAspect aspect, final String modelPath) {
		WORD_ENTRIES.clear();
		SearchRuntimeAdapter.addRelationshipWord(aspect, modelPath, null, WORD_ENTRIES);
		return (WordEntry)WORD_ENTRIES.get(0);
	}

	private WordEntry createRelationshipTypeWord(final RelationshipTypeAspect aspect) {
		WORD_ENTRIES.clear();
		SearchRuntimeAdapter.addRelationshipTypeWord(aspect, null, WORD_ENTRIES);
		return (WordEntry)WORD_ENTRIES.get(0);
	}

	// =========================================================================
	//                         T E S T   C A S E S
	// =========================================================================

	public void testRelatedObjectWord() {
		System.out.println("TestRuntimeAdapter.testRelatedObjectWord()"); //$NON-NLS-1$

		FakeRelationshipAspect aspect = new FakeRelationshipAspect();

		String[] sources = new String[]{"source1","source2"}; //$NON-NLS-1$ //$NON-NLS-2$
		aspect.sources = Arrays.asList(sources);
		String[] targets = new String[]{"target1","target2"}; //$NON-NLS-1$ //$NON-NLS-2$
		aspect.targets = Arrays.asList(targets);
		aspect.sourceRoleName = "sourceRoleName"; //$NON-NLS-1$
		aspect.targetRoleName = "targetRoleName"; //$NON-NLS-1$

		WordEntry word = createRelatedObjectWord(aspect);
		RelatedObjectRecord record = SearchRuntimeAdapter.createRelatedObjectRecord(word.getWord());
		//assertEquals(aspect.uuid,record.getRelationshipUUID());
		assertEquals("source1",record.getUUID()); //$NON-NLS-1$
		assertEquals("target1",record.getRelatedObjectUUID()); //$NON-NLS-1$
		// this record is the source record
		assertEquals(true, record.isSourceObject());
		// get the metaclass names for source and target
		assertEquals(" ", record.getMetaClassUri()); //$NON-NLS-1$
		assertEquals(" ", record.getRelatedMetaClassUri()); //$NON-NLS-1$
		// get the role names of source and target
		assertEquals("sourceRoleName",record.getRoleName()); //$NON-NLS-1$
		assertEquals("targetRoleName",record.getRelatedRoleName()); //$NON-NLS-1$
	}

	public void testRelationshipWord() {
		System.out.println("TestRuntimeAdapter.testRelationshipWord()"); //$NON-NLS-1$

		FakeRelationshipAspect aspect = new FakeRelationshipAspect();

		aspect.uuid = "uuid"; //$NON-NLS-1$
		aspect.typeUUid = "typeUUId"; //$NON-NLS-1$
		aspect.typeName = "myTypeName"; //$NON-NLS-1$
		aspect.name = "Name"; //$NON-NLS-1$
		String modelPath = "modelPath"; //$NON-NLS-1$

		WordEntry word = createRelationshipWord(aspect, modelPath);
		RelationshipRecord record = SearchRuntimeAdapter.createRelationshipRecord(word.getWord());
		assertEquals(aspect.uuid,record.getUUID());
		assertEquals(aspect.typeUUid,record.getTypeUUID());
		assertEquals(aspect.typeName,record.getTypeName());
		assertEquals(aspect.name, record.getName());
		assertEquals(modelPath, record.getResourcePath());		
	}

	public void testRelationshipTypeWord() {
		System.out.println("TestRuntimeAdapter.testRelationshipTypeWord()"); //$NON-NLS-1$

		FakeRelationshipTypeAspect aspect = new FakeRelationshipTypeAspect();

		aspect.uuid = "uuid"; //$NON-NLS-1$
		aspect.superTypeUUID = "superUUId"; //$NON-NLS-1$
		aspect.name = "myTypeName"; //$NON-NLS-1$
		aspect.sourceRoleName = "sourceName"; //$NON-NLS-1$
		aspect.targetRoleName = "targetName"; //$NON-NLS-1$

		WordEntry word = createRelationshipTypeWord(aspect);
		RelationshipTypeRecord record = SearchRuntimeAdapter.createRelationshipTypeRecord(word.getWord());
		assertEquals(aspect.uuid,record.getUUID());
		assertEquals(aspect.superTypeUUID,record.getSuperTypeUUID());
		assertEquals(aspect.name,record.getName());
		assertEquals(aspect.sourceRoleName, record.getSourceRoleName());
		assertEquals(aspect.targetRoleName , record.getTargetRoleName());
	}

}
