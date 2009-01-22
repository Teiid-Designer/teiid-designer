/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.internal.dialog;

import org.eclipse.core.resources.IContainer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.NewFolderDialog;
import com.metamatrix.ui.internal.InternalUiConstants;

/**
 * Based on the eclipse class of the same name.
 */
public class FolderSelectionDialog extends ElementTreeSelectionDialog implements ISelectionChangedListener {

    private Button fNewFolderButton;
    private IContainer fSelectedContainer;

    private static final String TITLE
        = InternalUiConstants.Util.getString("FolderSelectionDialog.title.text"); //$NON-NLS-1$
    private static final String FOLDER_SELECTION_DIALOG_MESSAGE
        = InternalUiConstants.Util.getString("FolderSelectionDialog.message.text"); //$NON-NLS-1$
    private static final String CREATE_NEW_BUTTON_TEXT
        = InternalUiConstants.Util.getString("FolderSelectionDialog.createNewButton.text"); //$NON-NLS-1$

    public FolderSelectionDialog(Shell parent, ILabelProvider labelProvider, ITreeContentProvider contentProvider) {
        super(parent, labelProvider, contentProvider);
        setTitle( TITLE );
        setMessage( FOLDER_SELECTION_DIALOG_MESSAGE );
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite result= (Composite)super.createDialogArea(parent);

        getTreeViewer().addSelectionChangedListener(this);

        Button button = new Button(result, SWT.PUSH);
        button.setText( CREATE_NEW_BUTTON_TEXT );
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                newFolderButtonPressed();
            }
        });
        button.setFont(parent.getFont());
        GridData data= new GridData();
		//        data.heightHint = convertVerticalDLUsToPixels(IDialogConstants.BUTTON_HEIGHT);
        button.setLayoutData(data);
        fNewFolderButton= button;

        applyDialogFont(result);

        return result;
    }

    private void updateNewFolderButtonState() {
        IStructuredSelection selection= (IStructuredSelection) getTreeViewer().getSelection();
        fSelectedContainer= null;
        if (selection.size() == 1) {
            Object first= selection.getFirstElement();
            if (first instanceof IContainer) {
                fSelectedContainer= (IContainer) first;
            }
        }
        fNewFolderButton.setEnabled(fSelectedContainer != null);
    }

    protected void newFolderButtonPressed() {
        NewFolderDialog dialog= new NewFolderDialog(getShell(), fSelectedContainer);
        if (dialog.open() == Window.OK) {
            TreeViewer treeViewer= getTreeViewer();
            treeViewer.refresh(fSelectedContainer);
            Object createdFolder= dialog.getResult()[0];
            treeViewer.reveal(createdFolder);
            treeViewer.setSelection(new StructuredSelection(createdFolder));
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
     */
    public void selectionChanged(SelectionChangedEvent event) {
        updateNewFolderButtonState();
    }



}
