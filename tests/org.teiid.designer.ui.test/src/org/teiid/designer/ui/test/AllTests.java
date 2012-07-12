package org.teiid.designer.ui.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.teiid.designer.ui.product.TestAbstractProductCustomizer;
import org.teiid.designer.ui.viewsupport.TestModelUtilities;


@RunWith( Suite.class )
@Suite.SuiteClasses( {TestAbstractProductCustomizer.class, TestModelUtilities.class} )
public class AllTests {
    // nothing to do
}
