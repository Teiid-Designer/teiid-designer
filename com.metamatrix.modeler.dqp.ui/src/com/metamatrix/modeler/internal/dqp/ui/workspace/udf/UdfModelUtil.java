/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
