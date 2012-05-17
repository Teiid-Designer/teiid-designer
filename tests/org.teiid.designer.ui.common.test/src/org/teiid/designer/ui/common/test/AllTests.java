package org.teiid.designer.ui.common.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.metamatrix.ui.dialogs.TestFileUiUtils;
import com.metamatrix.ui.internal.eventsupport.TestSelectionProvider;
import com.metamatrix.ui.internal.eventsupport.TestSelectionUtilities;
import com.metamatrix.ui.internal.viewsupport.TestUiBusyIndicator;

@RunWith( Suite.class )
@Suite.SuiteClasses({ TestSelectionUtilities.class,
        TestSelectionProvider.class, TestFileUiUtils.class,
        TestUiBusyIndicator.class })
public class AllTests {
    // nothing to do
}
