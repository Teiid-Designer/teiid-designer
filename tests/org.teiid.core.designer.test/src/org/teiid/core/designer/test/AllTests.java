package org.teiid.core.designer.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.metamatrix.core.TestCorePlugin;
import com.metamatrix.core.util.TestI18nUtil;
import com.metamatrix.core.util.TestIPathComparator;
import com.metamatrix.core.util.TestPluginUtilImpl;
import com.metamatrix.core.util.TestRunnableState;
import com.metamatrix.core.util.TestStreamPipe;
import com.metamatrix.modeler.core.TestModelerCoreException;

@RunWith( Suite.class )
@Suite.SuiteClasses( {TestModelerCoreException.class, TestStreamPipe.class, TestRunnableState.class, TestPluginUtilImpl.class,
    TestIPathComparator.class, TestI18nUtil.class, TestCorePlugin.class} )
public class AllTests {
    // nothing to do
}
