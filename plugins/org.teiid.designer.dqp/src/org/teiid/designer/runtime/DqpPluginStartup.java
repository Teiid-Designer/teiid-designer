/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime;

import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

/**
 * Class implemented to initialise objects once the 
 * workbench has been created. 
 */
public class DqpPluginStartup implements IStartup {

    @Override
    public void earlyStartup() {
        /*
         * Window listener that will initialise the server manager if it has not already been.
         * This means it is not up to the TeiidServerProvider, which may never be called if
         * the Servers view is not displayed. The most important point of this is to ensure 
         * that the default server has been set in ModelerCore in order to ensure the correct
         * runtime client is used.
         */
        final IWorkbench workbench = PlatformUI.getWorkbench();
        workbench.getDisplay().asyncExec(new Runnable() {
            
            @Override
            public void run() {
                DqpPlugin.getInstance().getServerManager();
            }
        });
    }

}
