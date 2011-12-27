package org.teiid.designer.datatools.ui.actions;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.teiid.designer.datatools.connection.ConnectionInfoProviderFactory;
import org.teiid.designer.datatools.connection.IConnectionInfoProvider;
import org.teiid.designer.datatools.ui.DatatoolsUiConstants;
import org.teiid.designer.datatools.ui.DatatoolsUiPlugin;
import org.teiid.designer.datatools.ui.dialogs.ConnectionProfileSummaryDialog;

import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.actions.SortableSelectionAction;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

public class ViewConnectionProfileAction extends SortableSelectionAction  {
    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(ViewConnectionProfileAction.class);
    private static final String TITLE = getString("title"); //$NON-NLS-1$

    private static String getString( final String id ) {
        return DatatoolsUiConstants.UTIL.getString(I18N_PREFIX + id);
    }

    private static String getString( final String id,
            final Object value ) {
    	return DatatoolsUiConstants.UTIL.getString(I18N_PREFIX + id, value);
    }

    private ConnectionInfoProviderFactory providerFactory;

    /**
     * @since 5.0
     */
    public ViewConnectionProfileAction() {
        super(TITLE, SWT.DEFAULT);
        setImageDescriptor(DatatoolsUiPlugin.getDefault().getImageDescriptor(DatatoolsUiConstants.Images.VIEW_CONNECTION_ICON));
        providerFactory = new ConnectionInfoProviderFactory();
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
        ModelResource modelResource = null;
        if (!getSelection().isEmpty()) {
            IFile modelFile = (IFile)SelectionUtilities.getSelectedObjects(getSelection()).get(0);
            modelResource = ModelUtilities.getModelResource(modelFile);
        }
        
        if( modelResource != null) {
        	Properties props = getModelConnectionProperties(modelResource);
        	
        	if( props == null || props.isEmpty() ) {
        		MessageDialog.openInformation(getShell(), getString("noInfo.title"),  //$NON-NLS-1$
        				getString("noInfo.message", modelResource.getItemName())); //$NON-NLS-1$
        		return;
        	}
        	String name = modelResource.getItemName();
        	
        	ConnectionProfileSummaryDialog dialog = new ConnectionProfileSummaryDialog(getShell(), name, props);
        	
        	dialog.open();
        }
    }

    private Properties getModelConnectionProperties(ModelResource mr) {

        try {
            if (ModelIdentifier.isRelationalSourceModel(mr)) {
                IConnectionInfoProvider provider = null;

                try {
                    provider = getProvider(mr);
                } catch (Exception e) {
                    // If provider throws exception its OK because some models may not have connection info.
                }

                if (provider != null) {
                    Properties properties = provider.getProfileProperties(mr); //ConnectionProperties(mr);
                    Properties p2 = provider.getConnectionProperties(mr);
                    String translatorName = provider.getTranslatorName(mr);
                    for( Object key : p2.keySet()) {
                    	properties.put(key, p2.get(key));
                    }
                    if( translatorName != null ) {
                    	properties.put(getString("translatorKey"), translatorName); //$NON-NLS-1$
                    }
                    if (properties != null && !properties.isEmpty()) {
                        return properties;
                    }
                }
            }
        } catch (CoreException e) {
            DatatoolsUiConstants.UTIL.log(e);
        }

        return null;
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

    public IConnectionInfoProvider getProvider( ModelResource modelResource ) throws Exception {
        IConnectionInfoProvider provider = null;
        provider = providerFactory.getProvider(modelResource);
        if (null == provider) {
            throw new Exception(getString("noConnectionInfoProvider.message")); //$NON-NLS-1$
        }
        return provider;

    }

    private Shell getShell() {
        return Display.getCurrent().getActiveShell();
    }
}
