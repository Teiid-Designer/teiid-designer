package org.teiid.designer.ui.common.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import com.metamatrix.ui.dialogs.TestFileUiUtils;
import com.metamatrix.ui.internal.eventsupport.TestSelectionProvider;
import com.metamatrix.ui.internal.eventsupport.TestSelectionUtilities;

@RunWith( Suite.class )
@Suite.SuiteClasses( {TestSelectionUtilities.class, TestSelectionProvider.class, TestFileUiUtils.class} )
public class AllTests {
    // nothing to do
}
