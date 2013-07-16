/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.connection;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.teiid.designer.runtime.connection.spi.IPasswordProvider;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.ui.common.util.UiUtil;

/**
 * Implementation of {@link IPasswordProvider} for providing a UI
 * for entering passwords.
 */
public class ConnectionPasswordProvider implements IPasswordProvider {

    /**
     * {@inheritDoc}
     *
     * @see org.teiid.designer.runtime.connection.spi.IPasswordProvider#getPassword(java.lang.String, java.lang.String)
     */
    @Override
    public String getPassword( final String modelName, final String profileName ) {
        final String[] password = new String[1];

        UiUtil.runInSwtThread(new Runnable() {

            @Override
            public void run() {
                String message = DqpUiConstants.UTIL.getString("PasswordProvider.missingPasswordMessage", //$NON-NLS-1$
                                                               new Object[] {modelName, profileName});

                Shell workbenchShell = UiUtil.getWorkbenchShellOnlyIfUiThread();
                PreviewMissingPasswordDialog dialog = new PreviewMissingPasswordDialog(workbenchShell, message);

                if (dialog.open() == Window.OK) {
                    password[0] = dialog.getPassword();
                }
            }
        },
                              false);

        return password[0];
    }
}