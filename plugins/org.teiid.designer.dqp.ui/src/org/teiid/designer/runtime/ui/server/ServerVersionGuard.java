/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.server;

import static org.teiid.designer.runtime.ui.DqpUiConstants.UTIL;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.runtime.spi.ExecutionConfigurationEvent;
import org.teiid.designer.runtime.spi.IExecutionConfigurationListener;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.ui.DqpUiPlugin;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion;

/**
 *
 */
public class ServerVersionGuard implements IExecutionConfigurationListener, StringConstants {

    private static IExecutionConfigurationListener instance;

    /**
     * @return singleton instance
     */
    public static IExecutionConfigurationListener getInstance() {
        if (instance == null)
            instance = new ServerVersionGuard();

        return instance;
    }

    @Override
    public void configurationChanged(ExecutionConfigurationEvent event) {
        if (ExecutionConfigurationEvent.EventType.DEFAULT != event.getEventType())
            return;

        if (ExecutionConfigurationEvent.TargetType.SERVER != event.getTargetType())
            return;

        ITeiidServer instance = event.getUpdatedServer();
        ITeiidServerVersion version = instance.getServerVersion();
        if (! version.isGreaterThan(TeiidServerVersion.Version.TEIID_DEFAULT.get()))
            return;

        IWorkbench workbench = DqpUiPlugin.getDefault().getWorkbench();
        if (workbench == null)
            return;

        IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
        if (window == null)
            return;

        MessageDialog.openWarning(window.getShell(),
                                   UTIL.getString(getClass().getSimpleName() + DOT + "notifyUnsupportedMsgTitle"),  //$NON-NLS-1$
                                   UTIL.getString(getClass().getSimpleName() + DOT + "notifyUnsupportedMsg")); //$NON-NLS-1$
    }

}
