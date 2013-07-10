/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.server;

import static org.teiid.designer.runtime.ui.DqpUiConstants.UTIL;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.teiid.designer.runtime.DqpPlugin;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.spi.ITeiidServerManager;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.DqpUiPlugin;


/**
 * The <code>NewServerAction</code> runs a UI that allows the user to create a new {@link ITeiidServer server}.
 *
 * @since 8.0
 */
public class NewServerAction extends Action implements IHandler {

    /**
     * The shell used to display the dialog that edits and creates servers.
     */
    private Shell shell = null;

    public NewServerAction() {
        super(UTIL.getString("newServerActionText")); //$NON-NLS-1$
        
        if (Platform.isRunning()) {
            setToolTipText(UTIL.getString("newServerActionToolTip")); //$NON-NLS-1$
            setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(DqpUiConstants.Images.NEW_SERVER_ICON));
        }
    }
    
    /**
     * @param shell the parent shell used to display the dialog
     */
    public NewServerAction( Shell shell) {
        this();
        this.shell = shell;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        if (shell == null) {
            shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        }

        ITeiidServerManager teiidServerManager = DqpPlugin.getInstance().getServerManager();
        ServerWizard wizard = new ServerWizard(teiidServerManager);      
        WizardDialog dialog = new WizardDialog(this.shell, wizard) {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.wizard.WizardDialog#configureShell(org.eclipse.swt.widgets.Shell)
             */
            @Override
            protected void configureShell( Shell newShell ) {
                super.configureShell(newShell);
                if (Platform.isRunning()) {
                    newShell.setImage(DqpUiPlugin.getDefault().getImage(DqpUiConstants.Images.NEW_SERVER_ICON));
                }
            }
        };
        
        dialog.open();
    }
    
    @Override
    public Object execute(ExecutionEvent event) {
        run();
        return null;
    }

    @Override
    public void addHandlerListener(IHandlerListener handlerListener) {
        // Not required
    }

    @Override
    public void dispose() {
        // Not required
    }

    @Override
    public void removeHandlerListener(IHandlerListener handlerListener) {
        // Not required
    }
}
