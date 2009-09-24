/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.product;

import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.product.IModelerProductContexts;


/**
 * @since 4.4
 */
public class ModelerRcpWorkbenchAdvisor extends WorkbenchAdvisor
                                        implements IModelerProductContexts,
                                                   UiConstants {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * @see org.eclipse.ui.application.WorkbenchAdvisor#createWorkbenchWindowAdvisor(org.eclipse.ui.application.IWorkbenchWindowConfigurer)
     * @since 4.4
     */
    @Override
    public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer theConfigurer) {
        return new ModelerRcpWindowAdvisor(theConfigurer);
    }

    /**
     * @see org.eclipse.ui.application.WorkbenchAdvisor#getInitialWindowPerspectiveId()
     * @since 4.4
     */
    @Override
    public String getInitialWindowPerspectiveId() {
        return Extensions.PERSPECTIVE;
    }

    /**
     * @see org.eclipse.ui.application.WorkbenchAdvisor#getMainPreferencePageId()
     * @since 4.3
     */
    @Override
    public String getMainPreferencePageId() {
        return Extensions.MODELER_PREFERENCE_PAGE;
    }

    /**
     * @see org.eclipse.ui.application.WorkbenchAdvisor#initialize(org.eclipse.ui.application.IWorkbenchConfigurer)
     * @since 4.4
     */
    @Override
    public void initialize(IWorkbenchConfigurer theConfigurer) {
        super.initialize(theConfigurer);

        // set save/restore based on customizer
        theConfigurer.setSaveAndRestore(UiPlugin.getDefault().isProductContextSupported(Workbench.SAVE_AND_RESTORE));
    }

}
