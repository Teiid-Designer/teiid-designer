/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.workspace.actions;

import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.teiid.designer.runtime.Connector;
import org.teiid.designer.runtime.SourceBindingsManager;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.modeler.internal.dqp.ui.workspace.dialogs.SelectConnectorBindingDialog;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.actions.SortableSelectionAction;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.util.UiUtil;

/**
 * @since 5.0
 */
public class BindToConnectorAction extends SortableSelectionAction implements DqpUiConstants {
    private static final String label = UTIL.getString("BindToConnectorAction.label", SWT.DEFAULT); //$NON-NLS-1$

    /**
     * @since 5.0
     */
    public BindToConnectorAction() {
        super(label, SWT.DEFAULT);
        setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(Images.SOURCE_BINDING_ICON));
    }

    /**
     * @see com.metamatrix.modeler.ui.actions.SortableSelectionAction#isValidSelection(org.eclipse.jface.viewers.ISelection)
     * @since 5.0
     */
    @Override
    public boolean isValidSelection( ISelection selection ) {
        // Enable for single/multiple Virtual Tables
        return sourceModelSelected(selection);
    }

    /**
     * @see org.eclipse.jface.action.IAction#run()
     * @since 5.0
     */
    @Override
    public void run() {
        ISelection cachedSelection = getSelection();
        if (cachedSelection != null && !cachedSelection.isEmpty()) {
            Object selectedObj = SelectionUtilities.getSelectedObject(cachedSelection);
            if (selectedObj != null && selectedObj instanceof IFile) {
                ModelResource modelResource = null;
                try {
                    modelResource = ModelUtilities.getModelResource(((IFile)selectedObj), false);
                    if (modelResource != null) {
                        SelectConnectorBindingDialog dialog = new SelectConnectorBindingDialog(
                                                                                               UiUtil.getWorkbenchShellOnlyIfUiThread());

                        dialog.open();

                        if (dialog.getReturnCode() == Window.OK) {
                            Connector connector = dialog.getSelectedConnector();
          
                            if (connector != null) {
                                SourceBindingsManager sourceBindingsMgr = connector.getType().getAdmin().getSourceBindingsManager();
                                sourceBindingsMgr.createSourceBinding(modelResource, connector);
                            }
                        }
                    }
                } catch (ModelWorkspaceException e) {
                    UTIL.log(e);
                }
            }

        }
        selectionChanged(null, new StructuredSelection());
    }

    /**
     * @see com.metamatrix.modeler.ui.actions.ISelectionAction#isApplicable(org.eclipse.jface.viewers.ISelection)
     * @since 5.0
     */
    @Override
    public boolean isApplicable( ISelection selection ) {
        return sourceModelSelected(selection);
    }

    private boolean sourceModelSelected( ISelection theSelection ) {
        boolean result = false;
        List allObjs = SelectionUtilities.getSelectedObjects(theSelection);
        if (!allObjs.isEmpty() && allObjs.size() == 1) {
            Iterator iter = allObjs.iterator();
            result = true;
            Object nextObj = null;
            while (iter.hasNext() && result) {
                nextObj = iter.next();

                if (nextObj instanceof IFile) {
                    result = ModelIdentifier.isRelationalSourceModel((IFile)nextObj);
                } else {
                    result = false;
                }
            }
        }

        return result;
    }
}
