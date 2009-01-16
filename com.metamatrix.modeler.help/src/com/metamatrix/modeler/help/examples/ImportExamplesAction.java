/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */

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
