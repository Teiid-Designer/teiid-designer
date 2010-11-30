package org.teiid.designer.modelgenerator.uml2.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import com.metamatrix.modeler.modelgenerator.uml2.util.TestRelationalObjectGenerator;
import com.metamatrix.modeler.modelgenerator.uml2.util.TestRelationalObjectNamingStrategyImpl;

@RunWith( Suite.class )
@Suite.SuiteClasses( {TestRelationalObjectNamingStrategyImpl.class, TestRelationalObjectGenerator.class} )
public class AllTests {
    // nothing to do
}
