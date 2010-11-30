package org.teiid.designer.core.xslt.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import com.metamatrix.core.xslt.TestCoreXsltPlugin;
import com.metamatrix.core.xslt.TestStyleFromResource;
import com.metamatrix.core.xslt.impl.TestStyleRegistryImpl;

@RunWith( Suite.class )
@Suite.SuiteClasses( {TestStyleRegistryImpl.class, TestStyleFromResource.class, TestCoreXsltPlugin.class} )
public class AllTests {
    // nothing to do
}
