package org.teiid.designer.ui.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import com.metamatrix.modeler.internal.ui.viewsupport.TestModelUtilities;
import com.metamatrix.modeler.ui.product.TestAbstractProductCustomizer;

@RunWith( Suite.class )
@Suite.SuiteClasses( {TestAbstractProductCustomizer.class, TestModelUtilities.class} )
public class AllTests {
    // nothing to do
}
