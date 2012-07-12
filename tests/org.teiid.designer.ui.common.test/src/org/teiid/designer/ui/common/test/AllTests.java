package org.teiid.designer.ui.common.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.teiid.designer.ui.common.dialogs.TestFileUiUtils;
import org.teiid.designer.ui.common.eventsupport.TestSelectionProvider;
import org.teiid.designer.ui.common.eventsupport.TestSelectionUtilities;
import org.teiid.designer.ui.common.viewsupport.TestUiBusyIndicator;


@RunWith( Suite.class )
@Suite.SuiteClasses({ TestSelectionUtilities.class,
        TestSelectionProvider.class, TestFileUiUtils.class,
        TestUiBusyIndicator.class })
public class AllTests {
    // nothing to do
}
