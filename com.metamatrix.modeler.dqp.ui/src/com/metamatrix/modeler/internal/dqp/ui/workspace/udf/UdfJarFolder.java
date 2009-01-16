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
package com.metamatrix.modeler.internal.dqp.ui.workspace.udf;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.views.properties.IPropertySource;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.internal.dqp.ui.workspace.actions.AddExistingUdfJarsAction;
import com.metamatrix.modeler.internal.dqp.ui.workspace.actions.ImportUdfJarsAction;
import com.metamatrix.modeler.ui.viewsupport.IExtendedModelObject;

/**
 * @since 5.0
 */
public class UdfJarFolder implements IExtendedModelObject {
    private final static IAction importJarsAction = new ImportUdfJarsAction();
    private final static IAction addJarsAction = new AddExistingUdfJarsAction();

    private ModelResource udfResource;
    private static final String FOLDER_LABEL = DqpUiConstants.UTIL.getString("UdfJarFolder.folderLabel"); //$NON-NLS-1$

    /**
     * @since 5.0
     */
    public UdfJarFolder( ModelResource theResource ) {
        super();
        udfResource = theResource;
    }

    public ModelResource getUdfResource() {
        return udfResource;
    }

    public String getLabel() {
        return FOLDER_LABEL;
    }

    public IPropertySource getPropertySource() {
        return null;
    }

    public String getStatusLabel() {
        return DqpUiConstants.UTIL.getString("UdfJarFolder.statusLabel"); //$NON-NLS-1$
    }

    public boolean overrideContextMenu() {
        return true;
    }

    public void fillContextMenu( IMenuManager theMenu ) {
        // TODO: Add DELETE & IMPORT JARS actions
        theMenu.add(importJarsAction);
        theMenu.add(addJarsAction);
    }
}
