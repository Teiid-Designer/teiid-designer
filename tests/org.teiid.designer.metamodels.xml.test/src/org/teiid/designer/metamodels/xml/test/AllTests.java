package org.teiid.designer.metamodels.xml.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.teiid.designer.metamodels.xml.namespace.TestNamespaceContext;
import org.teiid.designer.metamodels.xml.util.TestXmlDocumentUtil;


@RunWith( Suite.class )
@Suite.SuiteClasses( {TestXmlDocumentUtil.class, TestNamespaceContext.class} )
public class AllTests {
    // nothing to do
}
