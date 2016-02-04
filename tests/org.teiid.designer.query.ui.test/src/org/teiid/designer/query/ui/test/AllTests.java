package org.teiid.designer.query.ui.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.teiid.query.ui.sqleditor.component.TestDisplayNodeFactory;
import org.teiid.query.ui.sqleditor.component.TestDisplayNodeWithComments;

@RunWith( Suite.class )
@Suite.SuiteClasses( {TestDisplayNodeFactory.class, TestDisplayNodeWithComments.class} )
public class AllTests {
    // nothing to do
}
