package org.teiid.core.designer.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.teiid.core.TestCorePlugin;
import org.teiid.core.util.TestI18nUtil;
import org.teiid.core.util.TestIPathComparator;
import org.teiid.core.util.TestPluginUtilImpl;
import org.teiid.core.util.TestRunnableState;
import org.teiid.core.util.TestStreamPipe;
import org.teiid.designer.core.TestModelerCoreException;


@RunWith( Suite.class )
@Suite.SuiteClasses( {TestModelerCoreException.class, TestStreamPipe.class, TestRunnableState.class, TestPluginUtilImpl.class,
    TestIPathComparator.class, TestI18nUtil.class, TestCorePlugin.class} )
public class AllTests {
    // nothing to do
}
