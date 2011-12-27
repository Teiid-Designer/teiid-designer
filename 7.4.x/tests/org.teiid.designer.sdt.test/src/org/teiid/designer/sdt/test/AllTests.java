package org.teiid.designer.sdt.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import com.metamatrix.modeler.internal.sdt.types.TestBuiltInTypesManager;
import com.metamatrix.modeler.sdt.TestModelerSdtPlugin;

@RunWith( Suite.class )
@Suite.SuiteClasses( {TestModelerSdtPlugin.class, TestBuiltInTypesManager.class} )
public class AllTests {
    // nothing to do
}
