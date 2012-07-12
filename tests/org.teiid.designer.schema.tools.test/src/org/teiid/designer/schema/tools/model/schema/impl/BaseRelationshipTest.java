/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.schema.tools.model.schema.impl;

import org.teiid.designer.schema.tools.model.schema.impl.SimpleRelationship;

import junit.framework.TestCase;

public class BaseRelationshipTest extends TestCase {

	SimpleRelationship singleOccurance;
	SimpleRelationship unboundedOccurance;
	SimpleRelationship tripleOccurance;
	
	@Override
    protected void setUp() throws Exception {
		super.setUp();
		singleOccurance = new SimpleRelationship(null,null, 1, 1);
		unboundedOccurance = new SimpleRelationship(null,null, 1, -1);
		tripleOccurance = new SimpleRelationship(null,null, 3, 3);
	}

	/*
	 * Test method for 'org.teiid.designer.schema.tools.model.schema.impl.BaseRelationship.setType(int)'
	 */
	public void testSetType() {

	}

	/*
	 * Test method for 'org.teiid.designer.schema.tools.model.schema.impl.BaseRelationship.getType()'
	 */
	public void testGetType() {

	}

	/*
	 * Test method for 'org.teiid.designer.schema.tools.model.schema.impl.BaseRelationship.multiplyCardinalities(int, int)'
	 */
	public void testMultiplyCardinalities() {

	}

	/*
	 * Test method for 'org.teiid.designer.schema.tools.model.schema.impl.BaseRelationship.removeRelationship()'
	 */
	public void testRemoveRelationship() {

	}

	/*
	 * Test method for 'org.teiid.designer.schema.tools.model.schema.impl.BaseRelationship.addNewRelationship()'
	 */
	public void testAddNewRelationship() {

	}

	/*
	 * Test method for 'org.teiid.designer.schema.tools.model.schema.impl.BaseRelationship.merge(Relationship)'
	 */
	public void testMerge() {

	}

}
