/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.connection;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.DqpUiPlugin;
import org.teiid.designer.ui.actions.IConnectionAction;
import org.teiid.designer.ui.actions.SortableSelectionAction;
import org.teiid.designer.ui.common.actions.ModelActionConstants;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.viewsupport.ModelIdentifier;
import org.teiid.designer.ui.viewsupport.ModelUtilities;

public class SetJBossDataSourceNameAction extends SortableSelectionAction  implements IConnectionAction {
    private static final String ACTION_TITLE = DqpUiConstants.UTIL.getString("SetJBossDataSourceNameAction.title"); //$NON-NLS-1$
    private JndiNameInModelHelper jndiHelper;

    /**
     * @since 5.0
     */
    public SetJBossDataSourceNameAction() {
        super(ACTION_TITLE, SWT.DEFAULT);
        setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(DqpUiConstants.Images.SET_CONNECTION_ICON));
        this.jndiHelper = new JndiNameInModelHelper();
        setId(ModelActionConstants.Resource.SET_JBOSS_DATA_SOURCE_NAME);
    }

    /**
     * @see org.teiid.designer.ui.actions.SortableSelectionAction#isValidSelection(org.eclipse.jface.viewers.ISelection)
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
        // A) get the selected model and extract a "ConnectionProfileInfo" from it using the ConnectionProfileInfoHandler

        // B) Use ConnectionProfileHandler.getConnectionProfile(connectionProfileInfo) to query the user to
        // select a ConnectionProfile (or create new one)

        // C) Get the resulting ConnectionProfileInfo from the dialog and re-set the model's connection info
        // via the ConnectionProfileInfoHandler
        IFile modelFile = (IFile)SelectionUtilities.getSelectedObjects(getSelection()).get(0);
        ModelResource mr = ModelUtilities.getModelResourceForIFile(modelFile, true);
        
        jndiHelper.ensureJndiNameExists(mr, false);
    }
    


    /**
     * @see org.teiid.designer.ui.actions.ISelectionAction#isApplicable(org.eclipse.jface.viewers.ISelection)
     * @since 5.0
     */
    @Override
    public boolean isApplicable( ISelection selection ) {
        return sourceModelSelected(selection);
    }

    @SuppressWarnings("rawtypes")
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