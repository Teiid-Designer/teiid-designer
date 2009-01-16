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

package com.metamatrix.modeler.transformation.ui.search;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import com.metamatrix.modeler.transformation.ui.PluginConstants;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.modeler.transformation.ui.UiPlugin;
import com.metamatrix.ui.internal.util.UiUtil;

/**
 * @since 5.0
 */
public class OpenTransformationSearchPageAction2 extends Action implements UiConstants {

    private static final String DIALOG_TITLE = UiConstants.Util.getString("OpenTransformationSearchPageAction.dialog.title"); //$NON-NLS-1$

    /**
     * @since 5.0
     */
    public OpenTransformationSearchPageAction2() {
        super();
        setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(PluginConstants.Images.SEARCH_TRANSFORMATION_ICON));
        setToolTipText(UiConstants.Util.getString("OpenTransformationSearchPageAction.tooltip")); //$NON-NLS-1$
    }

    @Override
    public void run() {
        Dialog dialog = new TransformationSearchDialog(UiUtil.getWorkbenchShellOnlyIfUiThread(), DIALOG_TITLE);
        dialog.open();
    }
}
