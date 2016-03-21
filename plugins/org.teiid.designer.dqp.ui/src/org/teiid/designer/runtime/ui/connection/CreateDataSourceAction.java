package org.teiid.designer.runtime.ui.connection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceManager;
import org.teiid.designer.datatools.connection.ConnectionInfoProviderFactory;
import org.teiid.designer.datatools.connection.IConnectionInfoProvider;
import org.teiid.designer.datatools.ui.DatatoolsUiConstants;
import org.teiid.designer.runtime.DqpPlugin;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.DqpUiPlugin;
import org.teiid.designer.runtime.ui.server.RuntimeAssistant;
import org.teiid.designer.ui.actions.IConnectionAction;
import org.teiid.designer.ui.actions.SortableSelectionAction;
import org.teiid.designer.ui.common.dialog.AbstractPasswordDialog;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.viewsupport.ModelIdentifier;
import org.teiid.designer.ui.viewsupport.ModelUtilities;
import org.teiid.designer.vdb.ui.VdbUiPlugin;


/**
 * @since 8.0
 */
public class CreateDataSourceAction extends SortableSelectionAction implements IConnectionAction, DqpUiConstants {
    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(CreateDataSourceAction.class);
    private static final String label = DqpUiConstants.UTIL.getString("label"); //$NON-NLS-1$

    private static String getString( final String id ) {
        return DqpUiConstants.UTIL.getString(I18N_PREFIX + id);
    }

    private static String getString( final String id,
                                     final Object value ) {
        return DqpUiConstants.UTIL.getString(I18N_PREFIX + id, value);
    }

    private String pwd;
    private ConnectionInfoProviderFactory providerFactory;

    private ITeiidServer cachedServer;

    /**
     * @since 5.0
     */
    public CreateDataSourceAction() {
        super(label, SWT.DEFAULT);
        setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(Images.SOURCE_BINDING_ICON));
        providerFactory = new ConnectionInfoProviderFactory();
    }

    public void setTeiidServer( ITeiidServer teiidServer ) {
        this.cachedServer = teiidServer;
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
        final IWorkbenchWindow iww = VdbUiPlugin.singleton.getCurrentWorkbenchWindow();

        ModelResource modelResource = null;
        if (!getSelection().isEmpty()) {
            IFile modelFile = (IFile)SelectionUtilities.getSelectedObjects(getSelection()).get(0);
            modelResource = ModelUtilities.getModelResource(modelFile);
        }
        try {
        	// Check Server status. If none defined, query to create or cancel.
        	
            ITeiidServer teiidServer = cachedServer;
            if (teiidServer == null) {
            	teiidServer = DqpPlugin.getInstance().getServerManager().getDefaultServer();
            	if( teiidServer == null ) {
	            	if( RuntimeAssistant.ensureServerConnection(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
	            			getString("noServer.message"), true) ) { //$NON-NLS-1$
	            		teiidServer = DqpPlugin.getInstance().getServerManager().getDefaultServer();
	            		teiidServer.connect();	
	            	} else {
	            		// User has cancelled this action or decided not to create a new server
	            		return;
	            	}
            	} else {
            		if( RuntimeAssistant.ensureServerConnection(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
	            			getString("noServer.message"), false) ) { //$NON-NLS-1$
	            		teiidServer = DqpPlugin.getInstance().getServerManager().getDefaultServer();
	            		teiidServer.connect();	
	            	} else {
	            		// User has cancelled this action or decided not to create a new server
	            		return;
	            	}
            	}
            }
            
            /* 
             * check whether this was called from selection (i.e. right clicking on a model)
             * and if so, check whether the source model has a connection profile set.
             * Otherwise, this was called from the server view.
             */
            if (modelResource != null){
	        	Properties connProps = getModelConnectionProperties(modelResource);
	        	
	        	if( connProps == null || connProps.isEmpty() ) {
	        		MessageDialog.openInformation(getShell(), getString("noInfo.title"),  //$NON-NLS-1$
	        				getString("noInfo.message", modelResource.getItemName())); //$NON-NLS-1$
	        		return;
	        	}
            }
            
            // A) get the selected model and extract a "ConnectionProfileInfo" from it using the ConnectionProfileInfoHandler

            // B) Use ConnectionProfileHandler.getConnectionProfile(connectionProfileInfo) to query the user to
            // select a ConnectionProfile (or create new one)

            // C) Get the resulting ConnectionProfileInfo from the dialog and re-set the model's connection info
            // via the ConnectionProfileInfoHandler

            Collection<ModelResource> relationalModels = getRelationalModelsWithConnections();
            final CreateDataSourceWizard dialog = new CreateDataSourceWizard(iww.getShell(), teiidServer, relationalModels, modelResource);

//            wizard.init(iww.getWorkbench(), new StructuredSelection());
//            final WizardDialog dialog = new WizardDialog(wizard.getShell(), wizard);
            final int rc = dialog.open();
            if (rc != Window.OK)
                return;

            // Need to check if the connection needs a password

            TeiidDataSourceInfo info = dialog.getTeiidDataSourceInfo();
            Properties props = info.getProperties();
            IConnectionInfoProvider provider = info.getConnectionInfoProvider();
            // Model may not have a provider (created from scratch)
            
            if( provider != null ) {
	            boolean cancelledPassword = false;
	            if (null != provider.getDataSourcePasswordPropertyKey() && props.get(provider.getDataSourcePasswordPropertyKey()) == null) {
	                if (info.requiresPassword()) {
	
	                    int result = new AbstractPasswordDialog(iww.getShell(), getString("passwordTitle"), null) { //$NON-NLS-1$
	                        @SuppressWarnings( "synthetic-access" )
	                        @Override
	                        protected boolean isPasswordValid( final String password ) {
	                            pwd = password;
	                            return true;
	                        }
	                    }.open();
	                    if (result == Window.OK) {
	                        props.put(provider.getDataSourcePasswordPropertyKey(), this.pwd);
	                    } else {
	                        cancelledPassword = true;
	                    }
	                }
	            }
	
	            if( !cancelledPassword) {
	                teiidServer.getOrCreateDataSource(info.getDisplayName(),
	                                                  info.getJndiName(),
	                                                  provider.getDataSourceType(),
	                                                  props);
	            }
            }

        } catch (Exception e) {
            if (modelResource != null) {
                MessageDialog.openError(getShell(),
                                        getString("errorCreatingDataSourceForModel", modelResource.getItemName()), e.getMessage()); //$NON-NLS-1$
                DqpUiConstants.UTIL.log(IStatus.ERROR, e, getString("errorCreatingDataSourceForModel", modelResource.getItemName())); //$NON-NLS-1$
            } else {
                MessageDialog.openError(getShell(), getString("errorCreatingDataSource"), e.getMessage()); //$NON-NLS-1$
                DqpUiConstants.UTIL.log(IStatus.ERROR, e, getString("errorCreatingDataSource")); //$NON-NLS-1$

            }
        }
    }

    private Collection<ModelResource> getRelationalModelsWithConnections() {
        Collection<ModelResource> result = new ArrayList<ModelResource>();

        try {
            ModelResource[] mrs = ModelWorkspaceManager.getModelWorkspaceManager().getModelWorkspace().getModelResources();
            for (ModelResource mr : mrs) {
                if (ModelIdentifier.isRelationalSourceModel(mr)) {
                    IConnectionInfoProvider provider = null;

                    try {
                        provider = getProvider(mr);
                    } catch (Exception e) {
                        // If provider throws exception its OK because some models may not have connection info.
                    }

                    if (provider != null) {
                        Properties properties = provider.getConnectionProperties(mr);
                        if (properties != null && !properties.isEmpty()) {
                            result.add(mr);
                        }
                    }
                }
            }

        } catch (CoreException e) {
            DqpUiConstants.UTIL.log(e);
        }

        return result;
    }

    /**
     * @see org.teiid.designer.ui.actions.ISelectionAction#isApplicable(org.eclipse.jface.viewers.ISelection)
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
                    	Object value = p2.get(key);
                    	if( value != null ) {
                    		properties.put(key, value);
                    	}
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
}
