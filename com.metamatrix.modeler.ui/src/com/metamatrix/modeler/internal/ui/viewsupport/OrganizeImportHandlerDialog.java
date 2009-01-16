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
package com.metamatrix.modeler.internal.ui.viewsupport;

import java.util.List;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import com.metamatrix.modeler.core.refactor.OrganizeImportHandler;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerLabelProvider;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;

/**
 * OrganizeImportHandlerDialog
 */
public class OrganizeImportHandlerDialog implements OrganizeImportHandler {

    static final String TITLE = UiConstants.Util.getString("OrganizeImportHandlerDialog.title"); //$NON-NLS-1$
    static final String MESSAGE = UiConstants.Util.getString("OrganizeImportHandlerDialog.message"); //$NON-NLS-1$
    static final Provider provider = new Provider();

    /**
     * Construct an instance of OrganizeImportHandlerDialog.
     */
    public OrganizeImportHandlerDialog() {
    }

    /**
     * @see com.metamatrix.modeler.core.refactor.OrganizeImportHandler#choose(java.util.List)
     */
    public Object choose( List options ) {
        ChoiceRunnable cr = new ChoiceRunnable();
        cr.options = options;
        Display.getDefault().asyncExec(cr);
        return cr.result;
    }

    /*
     * Inner class to run the ElementListSelectionDialog on the Event Dispatch Thread. 
     * @since 4.2
     */
    class ChoiceRunnable implements Runnable {
        public Object result;
        public List options;

        public void run() {
            Shell shell = UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
            ElementListSelectionDialog dialog = new ElementListSelectionDialog(shell, provider);
            dialog.setMessage(MESSAGE);
            dialog.setTitle(TITLE);
            dialog.setElements(options.toArray());
            dialog.setMultipleSelection(false);
            int response = dialog.open();
            if (response == Window.OK) {
                result = dialog.getResult()[0];
            }
        }
    }

}

class Provider implements IStructuredContentProvider, ILabelProvider {

    private ILabelProvider labelProvider = new ModelExplorerLabelProvider();

    public void addListener( ILabelProviderListener listener ) {
    }

    public void dispose() {
    }

    public Object[] getElements( Object inputElement ) {
        return ((List)inputElement).toArray();
    }

    public Image getImage( Object element ) {
        if (element instanceof ModelResource) {
            return labelProvider.getImage(((ModelResource)element).getResource());
        }

        return labelProvider.getImage(element);
    }

    public String getText( Object element ) {
        if (element instanceof ModelResource) {
            return ((ModelResource)element).getResource().getFullPath().makeRelative().toString();
        }
        return labelProvider.getText(element);
    }

    public void inputChanged( Viewer viewer,
                              Object oldInput,
                              Object newInput ) {
    }

    public boolean isLabelProperty( Object element,
                                    String property ) {
        return false;
    }

    public void removeListener( ILabelProviderListener listener ) {
    }
}
