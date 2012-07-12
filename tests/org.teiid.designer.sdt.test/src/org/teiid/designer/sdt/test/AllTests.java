package org.teiid.designer.sdt.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.teiid.designer.sdt.TestModelerSdtPlugin;
import org.teiid.designer.sdt.types.TestBuiltInTypesManager;

@RunWith( Suite.class )
@Suite.SuiteClasses( {TestModelerSdtPlugin.class, TestBuiltInTypesManager.class} )
public class AllTests {
    // nothing to do
}
