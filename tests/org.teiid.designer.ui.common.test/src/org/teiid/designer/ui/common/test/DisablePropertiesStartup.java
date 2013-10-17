package org.teiid.designer.ui.common.test;
import org.eclipse.ui.IStartup;

/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/

/**
 * Early startup class for conditioning the UI for this unit test.
 *
 * Though unusual for such a startup class to exist in a unit test plugin, it is necessary to
 * set the usage reporting property before the UI has been started since the usage reporting
 * dialog is executed from a job thread prior to the loading of the unit test.
 */
public class DisablePropertiesStartup implements IStartup {

    private static final String USAGE_REPORTING_ENABLED_KEY = "usage_reporting_enabled"; //$NON-NLS-1$

    @Override
    public void earlyStartup() {
        /**
         * Disable the dialog that asks the user to record usage. Since this test is
         * responsible for testing UI behaviour, this dialog interferes.
         */
        System.setProperty(USAGE_REPORTING_ENABLED_KEY, Boolean.FALSE.toString());
    }

}
