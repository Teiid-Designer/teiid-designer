package org.teiid.designer.core.xslt.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.teiid.designer.core.xslt.TestCoreXsltPlugin;
import org.teiid.designer.core.xslt.TestStyleFromResource;
import org.teiid.designer.core.xslt.impl.TestStyleRegistryImpl;


@RunWith( Suite.class )
@Suite.SuiteClasses( {TestStyleRegistryImpl.class, TestStyleFromResource.class, TestCoreXsltPlugin.class} )
public class AllTests {
    // nothing to do
}
