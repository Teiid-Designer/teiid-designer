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
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.ui.internal.util.UiUtil;

/**
 * The <code>UdfModelUtil</code> provides utilities related to the <code>FunctionDefinitions.xmi</code> model. This model allows
 * the user to define their own functions.
 * 
 * @since 5.5.3
 */
public class UdfModelUtil implements UiConstants {
    private static final String UDFS_VIEW_ID = DqpUiConstants.Extensions.UDF_MODEL_VIEW;

    // ===========================================================================================================================
    // Class Methods
    // ===========================================================================================================================

    public static UdfModelView getUdfsView() {

        if (UDFS_VIEW_ID != null) {
            IWorkbenchPage page = UiUtil.getWorkbenchPage();
            IViewPart view = page.findView(UDFS_VIEW_ID);
            if (view instanceof UdfModelView) {
                return (UdfModelView)view;
            }
        }

        return null;
    }

    public static IAction getDeleteUdfJarsAction() {
        UdfModelView udfView = getUdfsView();
        if (udfView != null) {
            return udfView.getAction(UdfModelView.ID_DELETE_JARS_ACTION);
        }

        return null;
    }

    public static IAction getImportUdfJarsAction() {
        UdfModelView udfView = getUdfsView();
        if (udfView != null) {
            return udfView.getAction(UdfModelView.ID_IMPORT_JARS_ACTION);
        }

        return null;
    }

    public static IAction getAddUdfJarsAction() {
        UdfModelView udfView = getUdfsView();
        if (udfView != null) {
            return udfView.getAction(UdfModelView.ID_ADD_JARS_ACTION);
        }

        return null;
    }

    // ===========================================================================================================================
    // Constructors
    // ===========================================================================================================================

    /**
     * No construction allowed.
     */
    private UdfModelUtil() {
        // don't allow construction
    }
}
