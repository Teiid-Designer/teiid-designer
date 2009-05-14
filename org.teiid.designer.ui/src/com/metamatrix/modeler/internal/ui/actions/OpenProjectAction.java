/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.wizards.datatransfer.ExternalProjectImportWizard;

import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.ui.internal.util.WidgetFactory;


/** 
 * @since 4.3
 */
public class OpenProjectAction extends Action {
    private static final String TITLE = "Open Existing Project"; // UiPlugin.Util.getString("ImportMetadata.noProjectTitle"); //$NON-NLS-1$;
    /**
     * Construct an instance of ImportMetadata.
     */
    public OpenProjectAction() {
        super(TITLE);
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IAction#run()
     */
    @Override
    public void run() {
        IWorkbenchWindow iww = UiPlugin.getDefault().getCurrentWorkbenchWindow();
        WidgetFactory.createWizardDialog(iww.getShell(), new ExternalProjectImportWizard()).open();
    }
}
