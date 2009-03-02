/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.help.examples;

import java.io.File;
import java.net.URL;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PlatformUI;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 * ImportExamplesAction is a hook for the active help system to run the Import example Model Project wizard. The action is not
 * exposed anywhere in the Modeler ui.
 */
public class ImportExamplesAction extends Action {

    // ===========================================================================================================================
    // Constants
    // ===========================================================================================================================

    private static final String EXAMPLES_DIR_NAME = "examples/projectSets"; //$NON-NLS-1$

    // ===========================================================================================================================
    // Constructors
    // ===========================================================================================================================

    /**
     * @since 6.0.0
     */
    public ImportExamplesAction() {
    }

    // ===========================================================================================================================
    // Methods
    // ===========================================================================================================================

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.action.Action#run()
     * @since 6.0.0
     */
    @Override
    public void run() {
        try {
            URL url = FileLocator.find(Platform.getBundle("com.metamatrix.modeler.help"), new Path("/"), null); //$NON-NLS-1$  //$NON-NLS-2$
            String helpPluginDir = FileLocator.toFileURL(url).getFile();
            File examplesDir = new File(helpPluginDir, EXAMPLES_DIR_NAME);

            IWorkbenchWizard wizard = new ImportExampleMPSWizard(examplesDir);
            wizard.init(PlatformUI.getWorkbench(), null);
            WizardDialog dialog = new WizardDialog(UiUtil.getWorkbenchShellOnlyIfUiThread(), wizard);
            dialog.open();
        } catch (Exception e) {
            UiConstants.Util.log(e);
            WidgetUtil.showError(e);
        }

    }

}
