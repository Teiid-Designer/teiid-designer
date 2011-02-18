/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.xsd.ui.editor;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import com.metamatrix.modeler.core.types.DatatypeConstants;

public class EnterpriseTypeDialogProvider extends TextDialogProvider {

    private static final String DIALOG_MSG = GUIFacetHelper.getString("EnterpriseTypeDialogProvider.dialog.desc"); //$NON-NLS-1$
    private static final String DIALOG_TITLE = GUIFacetHelper.getString("EnterpriseTypeDialogProvider.dialog.title"); //$NON-NLS-1$

    public EnterpriseTypeDialogProvider( String launchButtonText ) {
        super(launchButtonText, null, null);
    }

    @Override
    public void showDialog( Shell shell,
                            Object initialValue ) {
        // Do NOT call super, here, because we don't want the text dialog
        // show dialog:
        ElementListSelectionDialog dialog = new ElementListSelectionDialog(shell, new StringLabelProvider());

        dialog.setElements(DatatypeConstants.getRuntimeTypeNames().toArray());
        dialog.setTitle(DIALOG_TITLE);
        dialog.setMessage(DIALOG_MSG);
        dialog.setBlockOnOpen(true);
        dialog.setInitialSelections(new Object[] {initialValue});

        int status = dialog.open();

        // process dialog result:
        if (status == Window.OK) {
            value = dialog.getResult()[0];
        } else {
            value = null;
        } // endif

    }

    static final class StringLabelProvider implements ILabelProvider {
        public Image getImage( Object element ) {
            return null;
        }

        public String getText( Object element ) {
            return (String)element;
        }

        public void addListener( ILabelProviderListener listener ) {
        }

        public void dispose() {
        }

        public boolean isLabelProperty( Object element,
                                        String property ) {
            return false;
        }

        public void removeListener( ILabelProviderListener listener ) {
        }
    }
}
