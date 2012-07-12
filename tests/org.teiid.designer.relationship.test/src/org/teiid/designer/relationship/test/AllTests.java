package org.teiid.designer.relationship.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.teiid.designer.relationship.TestNavigationContextInfo;
import org.teiid.designer.relationship.TestNavigationHistoryImpl;
import org.teiid.designer.relationship.TestNavigationLinkImpl;
import org.teiid.designer.relationship.TestRelationshipEditorImpl;
import org.teiid.designer.relationship.TestRelationshipPlugin;
import org.teiid.designer.relationship.TestRelationshipSearchImpl;
import org.teiid.designer.relationship.TestRelationshipTypeEditorImpl;


@RunWith( Suite.class )
@Suite.SuiteClasses( {TestRelationshipPlugin.class, TestNavigationContextInfo.class, TestRelationshipTypeEditorImpl.class,
    TestRelationshipSearchImpl.class, TestRelationshipEditorImpl.class, TestNavigationLinkImpl.class,
    TestNavigationHistoryImpl.class} )
public class AllTests {
    // nothing to do
}
