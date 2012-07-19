/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.viewsupport;

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
import org.teiid.designer.core.refactor.OrganizeImportHandler;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.UiPlugin;
import org.teiid.designer.ui.explorer.ModelExplorerLabelProvider;


/**
 * OrganizeImportHandlerDialog
 *
 * @since 8.0
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
     * @see org.teiid.designer.core.refactor.OrganizeImportHandler#choose(java.util.List)
     */
    @Override
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

        @Override
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

    @Override
	public void addListener( ILabelProviderListener listener ) {
    }

    @Override
	public void dispose() {
    }

    @Override
	public Object[] getElements( Object inputElement ) {
        return ((List)inputElement).toArray();
    }

    @Override
	public Image getImage( Object element ) {
        if (element instanceof ModelResource) {
            return labelProvider.getImage(((ModelResource)element).getResource());
        }

        return labelProvider.getImage(element);
    }

    @Override
	public String getText( Object element ) {
        if (element instanceof ModelResource) {
            return ((ModelResource)element).getResource().getFullPath().makeRelative().toString();
        }
        return labelProvider.getText(element);
    }

    @Override
	public void inputChanged( Viewer viewer,
                              Object oldInput,
                              Object newInput ) {
    }

    @Override
	public boolean isLabelProperty( Object element,
                                    String property ) {
        return false;
    }

    @Override
	public void removeListener( ILabelProviderListener listener ) {
    }
}
