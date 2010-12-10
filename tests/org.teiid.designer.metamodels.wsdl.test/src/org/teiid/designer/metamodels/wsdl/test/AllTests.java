package org.teiid.designer.metamodels.wsdl.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import com.metamatrix.metamodels.wsdl.io.TestWsdlHelper;
import com.metamatrix.metamodels.wsdl.io.TestWsdlIo;

@RunWith( Suite.class )
@Suite.SuiteClasses( {TestWsdlHelper.class, TestWsdlIo.class} )
public class AllTests {
    // nothing to do
}
