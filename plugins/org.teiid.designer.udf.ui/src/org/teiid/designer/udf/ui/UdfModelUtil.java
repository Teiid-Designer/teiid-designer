/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.udf.ui;

import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import com.metamatrix.ui.internal.util.UiUtil;

/**
 * The <code>UdfModelUtil</code> provides utilities related to the <code>FunctionDefinitions.xmi</code> model. This model allows
 * the user to define their own functions.
 * 
 * @since 5.5.3
 */
public class UdfModelUtil {

    private static final String UDFS_VIEW_ID = UdfUiPlugin.UDF_MODEL_VIEW;

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

    /**
     * No construction allowed.
     */
    private UdfModelUtil() {
        // don't allow construction
    }
}
