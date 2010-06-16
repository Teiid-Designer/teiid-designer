package com.metamatrix.modeler.core.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import com.metamatrix.core.util.StringUtilities;

public class StringUtilitiesTest {

    @Test
    public void emptyStringAndEmptyStringAreSame() {
        assertTrue("empty string and empty string should be same", StringUtilities.equalsIgnoreCase(StringUtilities.EMPTY_STRING, StringUtilities.EMPTY_STRING)); //$NON-NLS-1$
    }

    @Test
    public void emptyStringAndNonEmptyStringAreNotSame() {
        assertFalse("empty string and non-empty string should not be same", StringUtilities.equalsIgnoreCase(StringUtilities.EMPTY_STRING, "non-empty-string")); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void nullStringAndEmptyStringAreNotSame() {
        assertFalse("Null string and empty string should not be same", StringUtilities.equalsIgnoreCase(null, StringUtilities.EMPTY_STRING)); //$NON-NLS-1$
    }

    @Test
    public void nullStringsAreSame() {
        assertTrue("Two null strings should be same", StringUtilities.equalsIgnoreCase(null, null)); //$NON-NLS-1$
    }

    @Test
    public void sameStringsAreSame() {
        assertTrue("same strings should be same", StringUtilities.equalsIgnoreCase("non-empty-string", "non-empty-string")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    @Test
    public void sameStringsDifferentCaseAreNotSameIfMatchCase() {
        assertFalse("same strings should be same", StringUtilities.equals("Non-Empty-String", "non-empty-string")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    @Test
    public void sameStringsDifferentCaseAreSameIfNotMatchCase() {
        assertTrue("same strings should be same", StringUtilities.equalsIgnoreCase("Non-Empty-String", "non-empty-string")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
}
