/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.core;

import org.eclipse.ui.IStartup;

/**
 * Class implemented to initialise objects once the workbench has been created.
 */
public class ModelerCoreStartup implements IStartup {

    @Override
    public void earlyStartup() {
        /*
         * Will initialise the server manager if it has not already been initialised as early as
         * possible, bearing in mind it has to wait on ServerCore for its restoration.
         *
         * This means it is not up to the TeiidServerProvider, which may never be called if
         * the Servers view is not displayed. The most important point of this is to ensure
         * that the default server has been set in ModelerCore in order to ensure the correct
         * runtime client is used.
         *
         * Add this in a thread to avoid hogging the UI thread.
         */
        Thread serverManagerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                ModelerCore.getTeiidServerManager();
            }
        });
        serverManagerThread.run();
    }

}
