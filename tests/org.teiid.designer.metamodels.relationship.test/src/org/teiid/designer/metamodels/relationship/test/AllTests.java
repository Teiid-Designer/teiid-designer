package org.teiid.designer.metamodels.relationship.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.teiid.designer.metamodels.relationship.util.TestRelationshipTypeManager;
import org.teiid.designer.metamodels.relationship.util.TestRelationshipUtil;

@RunWith( Suite.class )
@Suite.SuiteClasses( {TestRelationshipTypeManager.class, TestRelationshipUtil.class} )
public class AllTests {
    // nothing to do
}
