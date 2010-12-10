package org.teiid.designer.metamodels.xml.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.metamatrix.metamodels.xml.namespace.TestNamespaceContext;
import com.metamatrix.metamodels.xml.util.TestXmlDocumentUtil;

@RunWith( Suite.class )
@Suite.SuiteClasses( {TestXmlDocumentUtil.class, TestNamespaceContext.class} )
public class AllTests {
    // nothing to do
}
