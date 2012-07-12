package org.teiid.designer.modelgenerator.uml2.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.teiid.designer.modelgenerator.uml2.util.TestRelationalObjectGenerator;
import org.teiid.designer.modelgenerator.uml2.util.TestRelationalObjectNamingStrategyImpl;


@RunWith( Suite.class )
@Suite.SuiteClasses( {TestRelationalObjectGenerator.class, TestRelationalObjectNamingStrategyImpl.class} )
public class AllTests {
    // nothing to do
}