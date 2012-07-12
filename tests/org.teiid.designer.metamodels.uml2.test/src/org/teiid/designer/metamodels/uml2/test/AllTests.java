package org.teiid.designer.metamodels.uml2.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.teiid.designer.metamodels.uml2.TestUml2Compatibility;
import org.teiid.designer.metamodels.uml2.util.TestPrimitiveTypeManager;


@RunWith( Suite.class )
@Suite.SuiteClasses( {TestUml2Compatibility.class, TestPrimitiveTypeManager.class} )
public class AllTests {
    // nothing to do
}
