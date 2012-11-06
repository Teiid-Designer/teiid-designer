package org.teiid.designer.runtime.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.teiid.designer.runtime.ExecutionConfigurationEventTest;
import org.teiid.designer.runtime.ServerManagerTest;
import org.teiid.designer.runtime.ServerTest;
import org.teiid.designer.runtime.ServerUtilsTest;
import org.teiid.designer.runtime.connection.ConnectionInfoHelperTest;
import org.teiid.designer.runtime.connection.ConnectionProfileFactoryTest;
import org.teiid.designer.runtime.connection.SourceBindingTest;

@RunWith( Suite.class )
@Suite.SuiteClasses( {ConnectionProfileFactoryTest.class, ConnectionInfoHelperTest.class,
    ServerUtilsTest.class, ServerTest.class, ServerManagerTest.class, ExecutionConfigurationEventTest.class,
    SourceBindingTest.class} )
public class AllTests {
    // nothing to do
}
