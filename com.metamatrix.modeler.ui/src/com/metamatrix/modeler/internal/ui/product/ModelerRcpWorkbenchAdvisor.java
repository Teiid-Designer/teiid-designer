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
