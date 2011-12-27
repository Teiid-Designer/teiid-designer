package org.teiid.designer.relationship.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import com.metamatrix.modeler.internal.relationship.TestNavigationHistoryImpl;
import com.metamatrix.modeler.internal.relationship.TestNavigationLinkImpl;
import com.metamatrix.modeler.internal.relationship.TestRelationshipEditorImpl;
import com.metamatrix.modeler.internal.relationship.TestRelationshipSearchImpl;
import com.metamatrix.modeler.internal.relationship.TestRelationshipTypeEditorImpl;
import com.metamatrix.modeler.relationship.TestNavigationContextInfo;
import com.metamatrix.modeler.relationship.TestRelationshipPlugin;

@RunWith( Suite.class )
@Suite.SuiteClasses( {TestRelationshipPlugin.class, TestNavigationContextInfo.class, TestRelationshipTypeEditorImpl.class,
    TestRelationshipSearchImpl.class, TestRelationshipEditorImpl.class, TestNavigationLinkImpl.class,
    TestNavigationHistoryImpl.class} )
public class AllTests {
    // nothing to do
}
