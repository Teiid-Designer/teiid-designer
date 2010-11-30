package org.teiid.designer.dqp.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.teiid.designer.runtime.ExecutionAdminTest;
import org.teiid.designer.runtime.ExecutionConfigurationEventTest;
import org.teiid.designer.runtime.ServerManagerTest;
import org.teiid.designer.runtime.ServerTest;
import org.teiid.designer.runtime.ServerUtilsTest;
import org.teiid.designer.runtime.TeiidTranslatorTest;
import org.teiid.designer.runtime.connection.ConnectionInfoHelperTest;
import org.teiid.designer.runtime.connection.ConnectionProfileFactoryTest;
import com.metamatrix.modeler.dqp.workspace.SourceBindingTest;

@RunWith( Suite.class )
@Suite.SuiteClasses( {ConnectionProfileFactoryTest.class, ConnectionInfoHelperTest.class, TeiidTranslatorTest.class,
    ServerUtilsTest.class, ServerTest.class, ServerManagerTest.class, ExecutionConfigurationEventTest.class,
    ExecutionAdminTest.class, SourceBindingTest.class} )
public class AllTests {
    // nothing to do
}
