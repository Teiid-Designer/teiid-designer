/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.search;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.teiid.designer.transformation.ui.PluginConstants;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.transformation.ui.UiPlugin;
import org.teiid.designer.ui.common.util.UiUtil;


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
