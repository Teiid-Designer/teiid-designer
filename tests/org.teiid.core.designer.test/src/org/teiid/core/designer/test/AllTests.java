package org.teiid.core.designer.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.teiid.core.designer.TestCorePlugin;
import org.teiid.core.designer.TestModelerCoreException;
import org.teiid.core.designer.util.TestI18nUtil;
import org.teiid.core.designer.util.TestIPathComparator;
import org.teiid.core.designer.util.TestPluginUtilImpl;
import org.teiid.core.designer.util.TestRunnableState;
import org.teiid.core.designer.util.TestStreamPipe;


@RunWith( Suite.class )
@Suite.SuiteClasses( {TestModelerCoreException.class, TestStreamPipe.class, TestRunnableState.class, TestPluginUtilImpl.class,
    TestIPathComparator.class, TestI18nUtil.class, TestCorePlugin.class} )
public class AllTests {
    // nothing to do
}
